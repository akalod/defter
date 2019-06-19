import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private static Connection conn;
    private static DSLContext dsl;

    private static void createDB() {

        if (conn != null) {
            try {
                Statement s = conn.createStatement();
                s.execute("CREATE TABLE settings ( id integer PRIMARY KEY, key text NOT NULL, value text NOT NULL);");
                s.execute("CREATE TABLE persons (id integer PRIMARY KEY, full_name text NOT NULL, phone text,address text,note text);");
                s.execute("CREATE TABLE transactions (id integer PRIMARY KEY, person_id integer NOT NULL, date text NOT NULL, amount real NOT NULL,type integer NOT NULL,note text);");
                // s.execute("INSERT INTO settings (id,key,value) VALUES(1,'admin','pass')");

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

    private void loginGenerate(Stage primaryStage) throws Exception {
        Main.loginLayer = new Login();
        Main.loginLayer.start(primaryStage);
    }

    public static void selectFirst(Stage primaryStage) throws Exception {
        //login şifresi var mı diye bak yoksa oluşturma ekranına geçir
        DSLContext create = DSL.using(conn);
        Record r = create.select(trim("*"),count()).from("settings").where("key='admin'").fetchOne();
        if (Integer.parseInt(r.get("count").toString()) != 0)  {
                System.out.println(r.get("count")+"var");
        } else {
            System.out.println("yok");

        }

    }

    @FXML
    private void loginHandle(ActionEvent event) {
        System.out.println("aaa");
    }
}
