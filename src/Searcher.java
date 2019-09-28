import fxmltableview.LFile;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.net.URL;
import java.util.ResourceBundle;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.trim;

public class Searcher implements EventHandler<ActionEvent>, Initializable {

    @FXML
    private TableView<LFile> queryTable;

    @FXML
    private TextField q;

    @FXML
    private ComboBox<String> zone;

    @FXML
    private ComboBox adliye;
    @FXML
    private ComboBox city;

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/Search.fxml"));
        primaryStage.setTitle("Dosya Takip Programı");

        Image image = new Image("/assets/notebook.png");
        primaryStage.getIcons().add(image);

        Scene screen = new Scene(root);

        primaryStage.setResizable(false);
        primaryStage.setScene(screen);
        primaryStage.show();


    }

    public void loadAdliyeList(){

        ObservableList a = adliye.getItems();
        a.clear();
        a.add("Adliye");
        for (Record rec :
                DSL.using(Controller.conn)
                        .select(DSL.field("DISTINCT(adliye)").as("adliye"))
                        .from("local_files")
                        .orderBy(DSL.field("adliye")).fetch()
        ) {
            a.add(rec.get("adliye"));
        }
    }

    public void loadSehirList(){

        ObservableList a = city.getItems();
        a.clear();
        a.add("Şehir");
        for (Record rec :
                DSL.using(Controller.conn)
                        .select(DSL.field("DISTINCT(city)").as("city"))
                        .from("local_files")
                        .orderBy(DSL.field("city")).fetch()
        ) {
            a.add(rec.get("city"));
        }
    }


    public void refreshList() {
        ObservableList<LFile> data = queryTable.getItems();
        data.clear();

        SelectSeekStep1<Record, Object> ra;

        if (zone.getValue() != null && !"Bölge".equalsIgnoreCase(zone.getValue().toString().trim())) {
            // Bölge Seçimi
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .where("zone ='" + zone.getValue().toString() + "'")
                    .orderBy(DSL.field("file_number"));
        }
        else if(city.getValue() != null && !"Şehir".equalsIgnoreCase(city.getValue().toString().trim())) {
            //şehir seçimi
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .where("city ='" + city.getValue().toString() + "'")
                    .orderBy(DSL.field("file_number"));
        }
        else if(adliye.getValue() != null && !"Adliye".equalsIgnoreCase(adliye.getValue().toString().trim())) {
            //Adliye seçimi
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .where("adliye ='" + adliye.getValue().toString() + "'")
                    .orderBy(DSL.field("file_number"));
        }
        else if (q.getText() != null && !q.getText().equals("")) {
            // query sonucu
            String nQ = q.getText().trim().replaceAll("'", "");
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .where("file_number like '%" + nQ + "%' ")
                    .or("type_1 like '%" + nQ + "%' ")
                    .or("zone like '%" + nQ + "%' ")
                    .or("type_2 like '%" + nQ + "%' ")
                    .or("city like '%" + nQ + "%' ")
                    .or("icra_dairesi like '%" + nQ + "%' ")
                    .or("haciz_gunu like '%" + nQ + "%' ")
                    .or("adliye like '%" + nQ + "%' ")
                    .or("id ='" + nQ + "' ")

                    .orderBy(DSL.field("file_number"));

        } else {
            // Tüm liste
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .orderBy(DSL.field("file_number"));

        }


        for (Record rec :
                ra.fetch()) {
            data.add(new LFile(
                    rec.get("zone").toString(),
                    rec.get("city").toString(),
                    rec.get("adliye").toString(),
                    rec.get("type_1").toString(),
                    rec.get("type_2").toString(),
                    rec.get("file_number").toString(),
                    rec.get("haciz_gunu").toString(),
                    rec.get("icra_dairesi").toString()));
        }

    }


    public static void addLocalFile(String fileName, String type1, String type2, String zone, String city, String adliye, String icraDairesi, String hacizGunu, String address) {
        try {
            DSLContext query = DSL.using(Controller.conn, SQLDialect.SQLITE);
            query.insertInto(
                    DSL.table("local_files"),
                    DSL.field("file_number"),
                    DSL.field("type_1"),
                    DSL.field("type_2"),
                    DSL.field("address"),
                    DSL.field("zone"),
                    DSL.field("city"),
                    DSL.field("adliye"),
                    DSL.field("icra_dairesi"),
                    DSL.field("haciz_gunu")
            ).values(fileName, type1, type2, address, zone, city, adliye, icraDairesi, hacizGunu).returning().fetchOne();

        } catch (Exception ex) {
            Controller.showAlert("İşlem Yapılamadı", ex.getMessage());
        }
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println(event.getEventType());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        refreshList();
        loadAdliyeList();
        loadSehirList();
        Main.searchLayer = this;
    }

    @FXML
    private void zoneChange(){
        q.setText("");
        city.setValue("Şehir");
        adliye.setValue("Adliye");
    }
    @FXML
    private void adliyeChange(){
        q.setText("");
        zone.setValue("Bölge");
        city.setValue("Şehir");
    }
    @FXML
    private void cityChange(){
        q.setText("");
        zone.setValue("Bölge");
        adliye.setValue("Adliye");
    }
    @FXML
    private void qChanged() {
        zone.setValue("Bölge");
        adliye.setValue("Adliye");
        city.setValue("Şehir");
    }

    @FXML
    private void searchHandle(ActionEvent event) {

        refreshList();
    }

    @FXML
    private void addPageAction(ActionEvent event) {
        System.out.println(event.getSource());
        Main.addPageLayer = new AddPage();
        refreshList();
        try {
            Main.addPageLayer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
