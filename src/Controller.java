import akalod.Cities;
import akalod.LFile;
import akalod.Zones;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.jooq.Record;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;


import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.trim;

public class Controller {
    public static Connection conn;
    public static boolean FileLister = true;


    private static void loadDataFromFile() {
        try {
            String XLS_FILE = "./backUp.xlsx";
            Workbook workbook = WorkbookFactory.create(new File(XLS_FILE));
            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("\n\nIterating over Rows and Columns using Iterator\n");
            Iterator<Row> rowIterator = ((Sheet) sheet).rowIterator();

            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                if (loop != 0) {
                    LFile dosya = new LFile();
                    /**
                     * 0 icra dairesi
                     * 1 dosya numarası
                     * 2 alacaklı  (type_1)
                     * 3 borclu (type_2)
                     * 4 adliye
                     * 5 bölge (zone)
                     * 6 haciz günü
                     * 7 evveliyat
                     * 8 il
                     */
                    int activeColumn = 0;
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();

                        if (activeColumn == 0) {
                            dosya.setIcraDairesi(cell.toString());
                        } else if (activeColumn == 1) {
                            dosya.setFileNumber(cell.toString());
                        } else if (activeColumn == 2) {
                            dosya.setType1(cell.toString());
                        } else if (activeColumn == 3) {
                            dosya.setType2(cell.toString());
                        } else if (activeColumn == 4) {
                            dosya.setAdliye(cell.toString());
                        } else if (activeColumn == 5) {
                            System.out.println(cell.toString() + ":" + Zones.getIdByString(cell.toString()));
                            dosya.setZone(Zones.getIdByString(cell.toString()), cell.toString());
                        } else if (activeColumn == 6) {
                            dosya.setHacizGunu(cell.toString());
                        } else if (activeColumn == 7) {
                            dosya.setEvliyat(cell.toString());
                        } else if (activeColumn == 8) {
                            dosya.setCity(Cities.getIdByString(cell.toString()), cell.toString());
                        }
                        activeColumn++;
                    }
                    Searcher.quickAddLocalFile(dosya);

                }
                loop++;
            }

            workbook.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void loadMacFromFile() {

        try {
            Statement s = conn.createStatement();
            StringBuilder sb = new StringBuilder();

            try (BufferedReader br = Files.newBufferedReader(Paths.get("macAddress.txt"))) {

                // read line by line
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    // sql into
                    s.execute("INSERT INTO mac_allow (mac) VALUES('" + sb.toString() + "')");
                    sb = new StringBuilder();
                }

            } catch (IOException e) {
                System.err.format("IOException: %s%n", e);
                System.exit(-1);
            }

            System.out.println(sb);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void createDB() {

        if (conn != null) {
            try {
                Statement s = conn.createStatement();
                s.execute("CREATE TABLE settings ( id integer PRIMARY KEY, key text NOT NULL, value text NOT NULL);");
                if (!FileLister) {
                    s.execute("CREATE TABLE persons (id integer PRIMARY KEY, full_name text NOT NULL, phone text,address text,note text);");
                    s.execute("CREATE TABLE transactions (id integer PRIMARY KEY, person_id integer NOT NULL, date text NOT NULL, amount real NOT NULL,type integer NOT NULL,note text);");
                    s.execute("INSERT INTO settings (id,key,value) VALUES(1,'admin','admin')");
                } else {
                    /* bu kısım bir kaç avukat arkadaş için revize edilerek başlatılmıştır */
                    s.execute("CREATE TABLE local_files (id integer PRIMARY KEY, file_number text NOT NULL UNIQUE , type_1 text, type_2 text,evliyat text,zone integer,city integer,icra_dairesi text,haciz_gunu text,adliye text ) ;");
                    s.execute("CREATE TABLE mac_allow (id integer PRIMARY KEY, mac text NOT NULL UNIQUE ) ;");
                    loadMacFromFile();
                    loadDataFromFile();
                }
                System.out.println("Database olusturuldu");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }

    public static void checkMacAllow() {

        DSLContext create = DSL.using(conn);

        String tempMac = "";

        for (final String mac : Main.MACAddress) {
            tempMac = mac;
            System.out.println(mac);
            Record r = create.select(trim("*"), count()).from("mac_allow").where("mac='" + mac + "'").fetchOne();
            if (Integer.parseInt(r.get("count").toString()) != 0)
                return;
        }

        Controller.showAlert("Erişim izini", "MAC Adresi geçersiz:" + tempMac);
        System.out.println("Erişime izin verilmedi");
        System.exit(-1);


    }

    public static void startup() {
        File dbFile = new File(Main.dbName);
        boolean exists = dbFile.exists();
        try {
            conn = DriverManager.getConnection(Main.sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        if (!exists) {
            createDB();
        }
        checkMacAllow();
    }

    public static void showAlert(String Title, String notice) {
        showAlert(Title, notice, Alert.AlertType.ERROR);
    }

    public static void showWarning(String Title, String notice) {
        showAlert(Title, notice, Alert.AlertType.WARNING);
    }

    public static void showAlert(String Title, String notice, Alert.AlertType errorType) {
        Alert alert = new Alert(errorType);
        alert.setHeaderText(Title);
        alert.setContentText(notice);
        alert.showAndWait();
    }

    public static void selectFirst(Stage primaryStage) throws Exception {
        //login şifresi var mı diye bak yoksa oluşturma ekranına geçir
        DSLContext create = DSL.using(conn);
        Record r = create.select(trim("*"), count()).from("settings").where("key='admin'").fetchOne();
        if (Integer.parseInt(r.get("count").toString()) != 0) {
            Main.loginLayer = new Login();
            Main.loginLayer.start(primaryStage);
        } else {
            System.out.println("Şifresiz giriş...");
            Main.searchLayer = new Searcher();
            Main.searchLayer.start(primaryStage);
        }

    }


}
