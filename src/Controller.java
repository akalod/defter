import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;


import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.trim;

public class Controller {
    public static Connection conn;
    private static DSLContext dsl;
    private static boolean FileLister = true;
    private static Stage stage;

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
                    s.execute("CREATE TABLE local_files (id integer PRIMARY KEY, file_number text NOT NULL, type_1 text, type_2 text,address text,zone text,city text,branch text);");
                }

                System.out.println("Database olusturuldu");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }


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
    }

    public static void showAlert(String Title, String notice) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(Title);
        alert.setContentText(notice);
        alert.showAndWait();
    }

    public static void selectFirst(Stage primaryStage) throws Exception {
        //login şifresi var mı diye bak yoksa oluşturma ekranına geçir
        DSLContext create = DSL.using(conn);
        Record r = create.select(trim("*"), count()).from("settings").where("key='admin'").fetchOne();
        stage = primaryStage;
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
