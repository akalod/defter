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
import org.jooq.meta.derby.sys.Sys;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Searcher implements EventHandler<ActionEvent>, Initializable {
    private Date lastClickTime;
    private LFile temp;

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

    public void loadAdliyeList() {

        ObservableList a = adliye.getItems();
        a.clear();
        a.add("Adliye");
        for (Record rec :
                DSL.using(Controller.conn)
                        .select(DSL.field("DISTINCT(UPPER(adliye))").as("adliye"))
                        .from("local_files")
                        .orderBy(DSL.field("adliye")).fetch()
        ) {
            a.add(rec.get("adliye"));
        }
    }

    public void loadSehirList() {

        ObservableList a = city.getItems();
        a.clear();
        a.add("Şehir");
        for (Record rec :
                DSL.using(Controller.conn)
                        .select(DSL.field("DISTINCT(UPPER(city))").as("city"))
                        .from("local_files")
                        .orderBy(DSL.field("city")).fetch()
        ) {
            a.add(rec.get("city"));
        }
    }

    public String trFilterLike(String Par) {
        return Par
                .replaceAll("Ş", "_")
                .replaceAll("S", "_")
                .replaceAll("Ç", "_")
                .replaceAll("C", "_")
                .replaceAll("Ü", "_")
                .replaceAll("Ğ", "_")
                .replaceAll("G", "_")
                .replaceAll("İ", "_")
                .replaceAll("I", "_");
    }


    public void refreshList() {
        ObservableList<LFile> data = queryTable.getItems();
        data.clear();

        SelectSeekStep1<Record, Object> ra;
        //önce girdi var mı?
        if (zone.getValue() != null && !"Bölge".equalsIgnoreCase(zone.getValue().trim())
                || city.getValue() != null && !"Şehir".equalsIgnoreCase(city.getValue().toString().trim())
                || adliye.getValue() != null && !"Adliye".equalsIgnoreCase(adliye.getValue().toString().trim())
                || q.getText() != null && !q.getText().equals("")
        ) {
            //System.out.println(trFilterLike(q.getText().toUpperCase()));
        } else {
            //diğer koşul
        }

        if (zone.getValue() != null && !"Bölge".equalsIgnoreCase(zone.getValue().trim())) {
            // Bölge Seçimi
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .where("zone ='" + zone.getValue().toString() + "'")
                    .orderBy(DSL.field("file_number"));
        } else if (city.getValue() != null && !"Şehir".equalsIgnoreCase(city.getValue().toString().trim())) {
            //şehir seçimi
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .where("city ='" + city.getValue().toString() + "'")
                    .orderBy(DSL.field("file_number"));
        } else if (adliye.getValue() != null && !"Adliye".equalsIgnoreCase(adliye.getValue().toString().trim())) {
            //Adliye seçimi
            ra = DSL
                    .using(Controller.conn)
                    .selectFrom("local_files")
                    .where("adliye ='" + adliye.getValue().toString() + "'")
                    .orderBy(DSL.field("file_number"));
        } else if (q.getText() != null && !q.getText().equals("")) {
            // query sonucu
            String nQ = trFilterLike(q.getText().trim().replaceAll("'", "").toUpperCase());

            System.out.println(nQ);
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
                    rec.get("icra_dairesi").toString(),
                    rec.get("address").toString()));
        }

    }

    public static void removeFile(String fileName) {
        try {
            DSLContext query = DSL.using(Controller.conn, SQLDialect.SQLITE);
            query.deleteFrom(DSL.table("local_files"))
                    .where("file_number='" + fileName + "'").returning().fetchOne();
        } catch (Exception ex) {
            Controller.showAlert("İşlem Yapılamadı", ex.getMessage());
        }
    }

    public static void editLocalFile(String fileName, String type1, String type2, String zone, String city, String adliye, String icraDairesi, String hacizGunu, String address){
        try {
            DSLContext query = DSL.using(Controller.conn, SQLDialect.SQLITE);
            query.update(DSL.table("local_files"))
                    .set(DSL.field("type_1"),type1.toUpperCase())
                    .set(DSL.field("type_2"),type2.toUpperCase())
                    .set(DSL.field("address"),address)
                    .set(DSL.field("zone"),zone)
                    .set(DSL.field("city"),city.toUpperCase())
                    .set(DSL.field("adliye"),adliye.toUpperCase())
                    .set(DSL.field("icra_dairesi"),icraDairesi.toUpperCase())
                    .set(DSL.field("haciz_gunu"),hacizGunu.toUpperCase())
                    .where("file_number='"+fileName+"'")
                    .execute();
        } catch (Exception ex) {
            Controller.showAlert("İşlem Yapılamadı", ex.getMessage());
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
            ).values(fileName.toUpperCase(), type1.toUpperCase(), type2.toUpperCase(), address, zone, city.toUpperCase(), adliye.toUpperCase(), icraDairesi.toUpperCase(), hacizGunu.toUpperCase()).returning().fetchOne();

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
    private void zoneChange() {
        q.setText("");
        city.setValue("Şehir");
        adliye.setValue("Adliye");
    }

    @FXML
    private void adliyeChange() {
        q.setText("");
        zone.setValue("Bölge");
        city.setValue("Şehir");
    }

    @FXML
    private void cityChange() {
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

    @FXML
    private void openPage() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://www.beyazbulutbilisim.com/"));
        }
    }
    public void loadAllLists(){
        refreshList();
        loadAdliyeList();
        loadSehirList();
    }

    @FXML
    private void showFile() {
        LFile row = queryTable.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (row != temp) {
            temp = row;
            lastClickTime = new Date();
        } else if (row == temp) {
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300) { //another click registered in 300 millis
                try {
                    EditPage editPage = new EditPage();
                    Main.remoteData = row;
                    editPage.start();
                } catch (Exception e) {
                    System.out.println("::" + e.toString());
                }

            } else {
                lastClickTime = new Date();
            }
        }
    }
}
