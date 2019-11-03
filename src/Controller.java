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
                     * 6 il
                     * 7 haciz günü
                     * 8 evveliyat
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
                            dosya.setZone(Zones.getIdByString(cell.toString()), cell.toString());
                        } else if (activeColumn == 6) {
                            dosya.setCity(Cities.getIdByString(cell.toString().trim()), cell.toString().trim());
                        } else if (activeColumn == 7) {
                            dosya.setHacizGunu(cell.toString());
                        } else if (activeColumn == 8) {
                            dosya.setEvliyat(cell.toString());
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

            try (BufferedReader br = Files.newBufferedReader(Paths.get("BBBSetting"))) {

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

    private static void loadSQLFiles(){

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
                    s.execute("CREATE TABLE local_files (id integer PRIMARY KEY, file_number text NOT NULL , type_1 text, type_2 text,evliyat text,zone integer,city integer,icra_dairesi text,haciz_gunu text,adliye text ) ;");
                    s.execute("CREATE TABLE mac_allow (id integer PRIMARY KEY, mac text NOT NULL UNIQUE ) ;");
                    s.execute("CREATE TABLE cities (id integer PRIMARY KEY, zone_id integer NOT NULL, city_name text NOT NULL ) ;");
                    s.execute("CREATE TABLE zones (id integer PRIMARY KEY, zone_name text NOT NULL ) ;");
                    s.execute("INSERT INTO cities VALUES (1, 2,  \"Adana \");");
                    s.execute("INSERT INTO cities VALUES (2, 5,  \"Adıyaman \");");
                    s.execute("INSERT INTO cities VALUES (3, 4,  \"Afyonkarahisar \");");
                    s.execute("INSERT INTO cities VALUES (4, 3,  \"Ağrı \");");
                    s.execute("INSERT INTO cities VALUES (5, 7,  \"Amasya \");");
                    s.execute("INSERT INTO cities VALUES (6, 6,  \"Ankara \");");
                    s.execute("INSERT INTO cities VALUES (7, 2,  \"Antalya \");");
                    s.execute("INSERT INTO cities VALUES (8, 7,  \"Artvin \");");
                    s.execute("INSERT INTO cities VALUES (9, 4,  \"Aydın \");");
                    s.execute("INSERT INTO cities VALUES (10, 1,  \"Balıkesir \");");
                    s.execute("INSERT INTO cities VALUES (11, 1,  \"Bilecik \");");
                    s.execute("INSERT INTO cities VALUES (12, 3,  \"Bingöl \");");
                    s.execute("INSERT INTO cities VALUES (13, 3,  \"Bitlis \");");
                    s.execute("INSERT INTO cities VALUES (14, 7,  \"Bolu \");");
                    s.execute("INSERT INTO cities VALUES (15, 2,  \"Burdur \");");
                    s.execute("INSERT INTO cities VALUES (16, 1,  \"Bursa \");");
                    s.execute("INSERT INTO cities VALUES (17, 1,  \"Çanakkale \");");
                    s.execute("INSERT INTO cities VALUES (18, 6,  \"Çankırı \");");
                    s.execute("INSERT INTO cities VALUES (19, 7,  \"Çorum \");");
                    s.execute("INSERT INTO cities VALUES (20, 4,  \"Denizli \");");
                    s.execute("INSERT INTO cities VALUES (21, 5,  \"Diyarbakır \");");
                    s.execute("INSERT INTO cities VALUES (22, 1,  \"Edirne \");");
                    s.execute("INSERT INTO cities VALUES (23, 3,  \"Elazığ \");");
                    s.execute("INSERT INTO cities VALUES (24, 3,  \"Erzincan \");");
                    s.execute("INSERT INTO cities VALUES (25, 3,  \"Erzurum \");");
                    s.execute("INSERT INTO cities VALUES (26, 6,  \"Eskişehir \");");
                    s.execute("INSERT INTO cities VALUES (27, 5,  \"Gaziantep \");");
                    s.execute("INSERT INTO cities VALUES (28, 7,  \"Giresun \");");
                    s.execute("INSERT INTO cities VALUES (29, 7,  \"Gümüşhane \");");
                    s.execute("INSERT INTO cities VALUES (30, 3,  \"Hakkari \");");
                    s.execute("INSERT INTO cities VALUES (31, 2,  \"Hatay \");");
                    s.execute("INSERT INTO cities VALUES (32, 2,  \"Isparta \");");
                    s.execute("INSERT INTO cities VALUES (33, 2,  \"Mersin(İçel) \");");
                    s.execute("INSERT INTO cities VALUES (34, 1,  \"İstanbul \");");
                    s.execute("INSERT INTO cities VALUES (35, 4,  \"İzmir \");");
                    s.execute("INSERT INTO cities VALUES (36, 3,  \"Kars \");");
                    s.execute("INSERT INTO cities VALUES (37, 7,  \"Kastamonu \");");
                    s.execute("INSERT INTO cities VALUES (38, 6,  \"Kayseri \");");
                    s.execute("INSERT INTO cities VALUES (39, 1,  \"Kırklareli \");");
                    s.execute("INSERT INTO cities VALUES (40, 6,  \"Kırşehir \");");
                    s.execute("INSERT INTO cities VALUES (41, 1,  \"Kocaeli \");");
                    s.execute("INSERT INTO cities VALUES (42, 6,  \"Konya \");");
                    s.execute("INSERT INTO cities VALUES (43, 4,  \"Kütahya \");");
                    s.execute("INSERT INTO cities VALUES (44, 3,  \"Malatya \");");
                    s.execute("INSERT INTO cities VALUES (45, 4,  \"Manisa \");");
                    s.execute("INSERT INTO cities VALUES (46, 2,  \"Kahramanmaraş \");");
                    s.execute("INSERT INTO cities VALUES (47, 5,  \"Mardin \");");
                    s.execute("INSERT INTO cities VALUES (48, 4,  \"Muğla \");");
                    s.execute("INSERT INTO cities VALUES (49, 3,  \"Muş \");");
                    s.execute("INSERT INTO cities VALUES (50, 6,  \"Nevşehir \");");
                    s.execute("INSERT INTO cities VALUES (51, 6,  \"Niğde \");");
                    s.execute("INSERT INTO cities VALUES (52, 7,  \"Ordu \");");
                    s.execute("INSERT INTO cities VALUES (53, 7,  \"Rize \");");
                    s.execute("INSERT INTO cities VALUES (54, 1,  \"Sakarya \");");
                    s.execute("INSERT INTO cities VALUES (55, 7,  \"Samsun \");");
                    s.execute("INSERT INTO cities VALUES (56, 5,  \"Siirt \");");
                    s.execute("INSERT INTO cities VALUES (57, 7,  \"Sinop \");");
                    s.execute("INSERT INTO cities VALUES (58, 6,  \"Sivas \");");
                    s.execute("INSERT INTO cities VALUES (59, 1,  \"Tekirdağ \");");
                    s.execute("INSERT INTO cities VALUES (60, 7,  \"Tokat \");");
                    s.execute("INSERT INTO cities VALUES (61, 7,  \"Trabzon \");");
                    s.execute("INSERT INTO cities VALUES (62, 3,  \"Tunceli \");");
                    s.execute("INSERT INTO cities VALUES (63, 5,  \"Şanlıurfa \");");
                    s.execute("INSERT INTO cities VALUES (64, 4,  \"Uşak \");");
                    s.execute("INSERT INTO cities VALUES (65, 3,  \"Van \");");
                    s.execute("INSERT INTO cities VALUES (66, 6,  \"Yozgat \");");
                    s.execute("INSERT INTO cities VALUES (67, 7,  \"Zonguldak \");");
                    s.execute("INSERT INTO cities VALUES (68, 6,  \"Aksaray \");");
                    s.execute("INSERT INTO cities VALUES (69, 7,  \"Bayburt \");");
                    s.execute("INSERT INTO cities VALUES (70, 6,  \"Karaman \");");
                    s.execute("INSERT INTO cities VALUES (71, 6,  \"Kırıkkale \");");
                    s.execute("INSERT INTO cities VALUES (72, 5,  \"Batman \");");
                    s.execute("INSERT INTO cities VALUES (73, 5,  \"Şırnak \");");
                    s.execute("INSERT INTO cities VALUES (74, 7,  \"Bartın \");");
                    s.execute("INSERT INTO cities VALUES (75, 3,  \"Ardahan \");");
                    s.execute("INSERT INTO cities VALUES (76, 3,  \"Iğdır \");");
                    s.execute("INSERT INTO cities VALUES (77, 1,  \"Yalova \");");
                    s.execute("INSERT INTO cities VALUES (78, 7,  \"Karabük \");");
                    s.execute("INSERT INTO cities VALUES (79, 5,  \"Kilis \");");
                    s.execute("INSERT INTO cities VALUES (80, 2,  \"Osmaniye \");");
                    s.execute("INSERT INTO cities VALUES (81, 7,  \"Düzce \");");
                    s.execute("INSERT INTO zones VALUES (1, \"MARMARA BÖLGESİ\");");
                    s.execute("INSERT INTO zones VALUES (2, \"AKDENİZ BÖLGESİ\");");
                    s.execute("INSERT INTO zones VALUES (3, \"DOĞU ANADOLU BÖLGESİ\");");
                    s.execute("INSERT INTO zones VALUES (4, \"EGE BÖLGESİ\");");
                    s.execute("INSERT INTO zones VALUES (5, \"GÜNEYDOĞU ANADOLU BÖLGESİ\");");
                    s.execute("INSERT INTO zones VALUES (6, \"İÇ ANADOLU BÖGESİ\");");
                    s.execute("INSERT INTO zones VALUES (7, \"KARADENİZ BÖLGESİ\");");
                    loadSQLFiles();
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
