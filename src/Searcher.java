import fxmltableview.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.meta.derby.sys.Sys;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Searcher implements EventHandler<ActionEvent>, Initializable {
    public Button newFileButton;
    public Pane queryBorder;
    public Button searchButton;
    public Button cleanButton;
    private Date lastClickTime;
    private LFile temp;

    @FXML
    private TableView<LFile> queryTable;

    @FXML
    private TextField q;

    @FXML
    private ComboBox<Zone> zone;

    @FXML
    private ComboBox adliye;
    @FXML
    private ComboBox<City> city;

    private Integer totalResult = 0;

    @FXML
    private Text textResult;

    public static Rectangle2D bounds;

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/Search.fxml"));
        primaryStage.setTitle("Dosya Takip Programı");

        Image image = new Image("/assets/notebook.png");
        primaryStage.getIcons().add(image);

        Scene screen = new Scene(root);




        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.setScene(screen);
        primaryStage.show();


    }

    public void loadAdliyeList() {

        ObservableList a = adliye.getItems();
        a.clear();
        a.add("ADLİYE");
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
        city.getItems().clear();
        if (zone.getValue() != null && zone.getValue().getId() != 0) {
            city.getItems().addAll(Cities.getByZone(zone.getValue().getId()));
        } else {
            city.getItems().addAll(Cities.getAll());
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
        String[] looksUp = new String[5];
        Integer wCount = 0;

        looksUp[0] = "type_1";
        looksUp[1] = "type_2";
        looksUp[2] = "haciz_gunu";
        looksUp[3] = "icra_dairesi";
        looksUp[4] = "file_number";

        String nQ = trFilterLike(q.getText().trim().replaceAll("'", "").toUpperCase());
        SelectWhereStep<Record> dyn = DSL
                .using(Controller.conn)
                .selectFrom("local_files");

        //önce girdi var mı?
        if (zone.getValue() != null && zone.getValue().getId() != 0
                || city.getValue() != null && city.getValue().getId() != 0
                || adliye.getValue() != null && !"ADLİYE".equalsIgnoreCase(adliye.getValue().toString().trim())
                || q.getText() != null && !q.getText().equals("")
        ) {

            if (q.getText() != null && !q.getText().equals("")) {
                int size = looksUp.length;
                for (int i = 0; i < size; i++) {
                    if (i > 0) {
                        if (zone.getValue() != null && zone.getValue().getId() != 0
                                || city.getValue() != null && city.getValue().getId() != 0
                                || adliye.getValue() != null && !"ADLİYE".equalsIgnoreCase(adliye.getValue().toString().trim())
                        ) {
                            if (city.getValue() != null && city.getValue().getId() != 0) {
                                dyn.where("city ='" + city.getValue().getId() + "'").or(looksUp[i] + " like '%" + nQ + "%' ");
                            }
                            if (adliye.getValue() != null && !"adliye".equalsIgnoreCase(adliye.getValue().toString().trim())) {
                                dyn.where("adliye ='" + adliye.getValue().toString() + "'").or(looksUp[i] + " like '%" + nQ + "%' ");
                            }
                            if (zone.getValue() != null && zone.getValue().getId() != 0) {
                                dyn.where("zone ='" + zone.getValue().getId() + "'").or(looksUp[i] + " like '%" + nQ + "%' ");
                            }
                        } else {
                            dyn.where("1=1").or(looksUp[i] + " like '%" + nQ + "%' ");
                        }
                    } else {
                        dyn.where(looksUp[i] + " like '%" + nQ + "%' ");
                    }
                }
            } else {
                if (city.getValue() != null && city.getValue().getId() != 0) {
                    System.out.println("1");
                    dyn.where("city ='" + city.getValue().getId() + "'");
                }
                if (adliye.getValue() != null && !"ADLİYE".equalsIgnoreCase(adliye.getValue().toString().trim())) {
                    System.out.println("2");
                    dyn.where("adliye ='" + adliye.getValue().toString() + "'");
                }
                if (zone.getValue().getId() != 0) {
                    dyn.where("zone ='" + zone.getValue().getId() + "'");
                }
            }

            ra = dyn.orderBy(DSL.field("file_number"));


        } else {
            //filtre yokken yapılacaklar ile ilgili kısım
            ra = dyn.orderBy(DSL.field("file_number"));
        }

        totalResult = ra.fetchCount();

        for (Record rec :
                ra.fetch()) {
            data.add(new LFile(
                    Integer.parseInt(rec.get("zone").toString()),
                    Integer.parseInt(rec.get("city").toString()),
                    rec.get("adliye").toString(),
                    rec.get("type_1").toString(),
                    rec.get("type_2").toString(),
                    rec.get("file_number").toString(),
                    rec.get("haciz_gunu").toString(),
                    rec.get("icra_dairesi").toString(),
                    rec.get("evliyat").toString()));
        }

        textResult.getStyleClass().clear();
        if (totalResult == 0) {
            textResult.setText("Sonuç bulunamadı");
            textResult.getStyleClass().add("no-result");
        } else {
            textResult.setText("Toplam " + totalResult + " adet sonuç listelenmekte");
            textResult.getStyleClass().add("have-result");
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

    public static void editLocalFile(String fileName, String type1, String type2, Zone zone, City city, String adliye, String icraDairesi, String hacizGunu, String evliyat) {
        try {
            DSLContext query = DSL.using(Controller.conn, SQLDialect.SQLITE);
            query.update(DSL.table("local_files"))
                    .set(DSL.field("type_1"), type1.toUpperCase())
                    .set(DSL.field("type_2"), type2.toUpperCase())
                    .set(DSL.field("evliyat"), evliyat.toUpperCase())
                    .set(DSL.field("zone"), zone.getId())
                    .set(DSL.field("city"), city.getId())
                    .set(DSL.field("adliye"), adliye.toUpperCase())
                    .set(DSL.field("icra_dairesi"), icraDairesi.toUpperCase())
                    .set(DSL.field("haciz_gunu"), hacizGunu.toUpperCase())
                    .where("file_number='" + fileName + "'")
                    .execute();
        } catch (Exception ex) {
            Controller.showAlert("İşlem Yapılamadı", ex.getMessage());
        }
    }

    public static void addLocalFile(String fileName, String type1, String type2, Zone zone, City city, String adliye, String icraDairesi, String hacizGunu, String evliyat) {
        try {
            DSLContext query = DSL.using(Controller.conn, SQLDialect.SQLITE);
            query.insertInto(
                    DSL.table("local_files"),
                    DSL.field("file_number"),
                    DSL.field("type_1"),
                    DSL.field("type_2"),
                    DSL.field("evliyat"),
                    DSL.field("zone"),
                    DSL.field("city"),
                    DSL.field("adliye"),
                    DSL.field("icra_dairesi"),
                    DSL.field("haciz_gunu")
            ).values(fileName, type1.toUpperCase(), type2.toUpperCase(), evliyat.toUpperCase(), zone.getId(), city.getId(), adliye.toUpperCase(), icraDairesi.toUpperCase(), hacizGunu.toUpperCase()).returning().fetchOne();

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

        zone.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                refreshList();
                loadSehirList();
            }
        });

        city.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                refreshList();
            }
        });

        adliye.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                refreshList();
            }
        });

        zone.getItems().clear();
        zone.getItems().addAll(Zones.getAll());
        Screen monitorScreen = Screen.getPrimary();
        bounds = monitorScreen.getVisualBounds();

        textResult.setY(bounds.getHeight()-130);
        newFileButton.setLayoutX(bounds.getWidth()-168);
        queryBorder.setPrefWidth(bounds.getWidth()-40);
       // searchButton.setLayoutX(bounds.getWidth()-198);
        cleanButton.setLayoutX(bounds.getWidth()-150);

        refreshList();
        loadAdliyeList();
        loadSehirList();
        Main.searchLayer = this;
    }

    @FXML
    private void searchHandle(ActionEvent event) {
        refreshList();
    }

    @FXML
    private void searchKeyUp(KeyEvent event) {
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

    public void loadAllLists() {
        refreshList();
        loadAdliyeList();
        loadSehirList();
    }

    @FXML
    private void clearAction() {
        zone.setValue(Zones.getFirst());
        adliye.setValue("ADLİYE");
        city.setValue(Cities.getFirst());
        q.setText("");
        refreshList();
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
