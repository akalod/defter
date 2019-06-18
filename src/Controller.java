import java.io.File;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {

    private static void createDB(){
        try (Connection conn = DriverManager.getConnection(Main.sql)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                Statement s = conn.createStatement();

                s.execute("CREATE TABLE settings ( id integer PRIMARY KEY, key text NOT NULL, value text NOT NULL);");
                s.execute("CREATE TABLE persons (id integer PRIMARY KEY, full_name text NOT NULL, phone text,address text,note text);");
                s.execute("CREATE TABLE transactions (id integer PRIMARY KEY, person_id integer NOT NULL, date text NOT NULL, amount real NOT NULL,type integer NOT NULL,note text);");
                s.execute("INSERT INTO settings (id,key,value) VALUES(1,'admin','pass')");

                System.out.println("Database olusturuldu");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    public static void startup(){
        File dbFile = new File(Main.dbName);
        boolean exists = dbFile.exists();
        if(!exists){
            createDB();
        }
    }
}
