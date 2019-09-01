import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;


import java.io.File;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.trim;

public class Controller implements Initializable {
    private static Connection conn;
    private static DSLContext dsl;
    private static boolean FileLister = true;
    private static Stage stage;

    @FXML
    public static TableView<localFile> queryTable;
    @FXML
    public static TableColumn<localFile,String> col_zone;
    public static TableColumn<localFile,String> col_branch;
    public static TableColumn<localFile,String> col_city;
    public static TableColumn<localFile,String> col_fileNumber;
    public static TableColumn<localFile,String> col_type1;
    public static TableColumn<localFile,String> col_type2;

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

    public static void refreshList() {
       // ObservableList<localFile> list = FXCollections.observableArrayList();

      // queryTable.getItems().add(new localFile("zone","city","branch","dosya_n","alacakli","verecekli"));
    }

    public static void addLocalFile(String fileName, String type1, String type2, String zone, String city, String branch, String address) {
        try {
            DSLContext query = DSL.using(conn, SQLDialect.SQLITE);
            query.insertInto(
                    DSL.table("local_files"),
                    DSL.field("file_number"),
                    DSL.field("type_1"),
                    DSL.field("type_2"),
                    DSL.field("address"),
                    DSL.field("zone"),
                    DSL.field("city"),
                    DSL.field("branch")
            ).values(fileName, type1, type2, address, zone, city, branch).returning().fetchOne();

        } catch (Exception ex) {
            showAlert("İşlem Yapılamadı", ex.getMessage());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){
        col_zone.setCellValueFactory(new PropertyValueFactory<>("zone"));
        col_branch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        col_city.setCellValueFactory(new PropertyValueFactory<>("city"));
        col_fileNumber.setCellValueFactory(new PropertyValueFactory<>("file_number"));
        col_type1.setCellValueFactory(new PropertyValueFactory<>("type_1"));
        col_type2.setCellValueFactory(new PropertyValueFactory<>("type_2"));
    }

    @FXML
    private void loginHandle(ActionEvent event) {
        System.out.println("aaa");
    }

    @FXML
    private void searchHandle(ActionEvent event) {

    }

    @FXML
    private void addPageAction(ActionEvent event) {
        System.out.println(event.getSource());
        Main.addPageLayer = new AddPage();
        try {
            Main.addPageLayer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
