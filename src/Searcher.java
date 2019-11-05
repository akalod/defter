import akalod.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import org.jooq.*;
import org.jooq.impl.DSL;

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
    public Button cleanButton;
    private Date lastClickTime;
    private LFile temp;

    private Stage mainScene;

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

    public void selectCityFromAnother(KeyEvent event) {
        if (event.getCode() == KeyCode.F4) {
            event.consume();
            Main.searchLayer.city.requestFocus();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        zone.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                refreshList();
                loadSehirList();
                city.setValue(Cities.getFirst());
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

        city.addEventFilter(KeyEvent.KEY_PRESSED, this::selectCityFromAnother);
        zone.addEventFilter(KeyEvent.KEY_PRESSED, this::selectCityFromAnother);
        adliye.addEventFilter(KeyEvent.KEY_PRESSED, this::selectCityFromAnother);

        zone.getItems().clear();
        zone.getItems().addAll(Zones.getAll());

        refreshList();
        loadAdliyeList();
        loadSehirList();

        Main.searchLayer = this;
    }

    private void changeSize(Double x, Double y) {

        if (!y.isNaN() && y.toString() != null) {
            Main.searchLayer.textResult.setLayoutY(y - 50);
            Main.searchLayer.queryTable.setPrefHeight(y - 260);
        }
        Main.searchLayer.newFileButton.setLayoutX(x - 188);
        Main.searchLayer.cleanButton.setLayoutX(x - 170);
        Main.searchLayer.textResult.setWrappingWidth(x - 40);
        Main.searchLayer.queryBorder.setPrefWidth(x - 60);
        Main.searchLayer.queryTable.setPrefWidth(x - 60);

    }

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/Search.fxml"));
        mainScene = primaryStage;
        primaryStage.setTitle("Takipçi");

        Image image = new Image("/assets/notebook.png");
        primaryStage.getIcons().add(image);

        Scene scene = new Scene(root);

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
                changeSize(mainScene.getWidth(), mainScene.getHeight());

        mainScene.widthProperty().addListener(stageSizeListener);
        mainScene.heightProperty().addListener(stageSizeListener);

        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.F1) {
                Main.searchLayer.q.requestFocus();
            } else if (event.getCode() == KeyCode.F2) {
                Main.searchLayer.adliye.requestFocus();
            } else if (event.getCode() == KeyCode.F3) {
                Main.searchLayer.zone.requestFocus();
            } else if (event.getCode() == KeyCode.F4) {
                Main.searchLayer.city.requestFocus();
            } else if (event.getCode() == KeyCode.F5) {
                Main.searchLayer.clearAction();
            } else if (event.getCode() == KeyCode.F9) {
                Main.searchLayer.queryTable.requestFocus();
                Main.searchLayer.queryTable.getSelectionModel().select(0);
                Main.searchLayer.queryTable.getFocusModel().focus(0);
            } else if (event.getCode() == KeyCode.F12) {
                Main.searchLayer.addPageAction();
            } else if (event.getCode() == KeyCode.ENTER) {
                if (Main.searchLayer.queryTable.isFocused()) {
                    Main.searchLayer.showFile(true);
                }
            } else {
                    Main.selector(event, scene, Main.searchLayer.city, Main.searchLayer.adliye, Main.searchLayer.zone);
            }

        });
        mainScene.setMinWidth(990);
        mainScene.setMinHeight(640);
        mainScene.sizeToScene();
        mainScene.setResizable(true);
        mainScene.setMaximized(true);
        mainScene.setScene(scene);
        mainScene.show();

    }

    public void loadAdliyeList() {

        ObservableList a = adliye.getItems();
        a.clear();
        a.add("- ADLİYE -");
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
        String[] looksUp = new String[7];

        looksUp[0] = "type_1";
        looksUp[1] = "type_2";
        looksUp[2] = "haciz_gunu";
        looksUp[3] = "icra_dairesi";
        looksUp[4] = "file_number";
        looksUp[5] = "city_name";
        looksUp[6] = "zone_name";

        String nQ = trFilterLike(q.getText().trim().replaceAll("'", "").toUpperCase());
        SelectWhereStep<Record> dyn = DSL
                .using(Controller.conn)
                .select(DSL.table("local_files").asterisk())
                .from("local_files")
                .leftJoin("cities").on("cities.id=local_files.city")
                .leftJoin("zones").on("zones.id=local_files.zone");


        //önce girdi var mı?
        if (zone.getValue() != null && zone.getValue().getId() != 0
                || city.getValue() != null && city.getValue().getId() != 0
                || adliye.getValue() != null && !"- ADLİYE -".equalsIgnoreCase(adliye.getValue().toString().trim())
                || q.getText() != null && !q.getText().equals("")
        ) {

            if (q.getText() != null && !q.getText().equals("")) {
                int size = looksUp.length;
                for (int i = 0; i < size; i++) {
                    if (i > 0) {
                        if (zone.getValue() != null && zone.getValue().getId() != 0
                                || city.getValue() != null && city.getValue().getId() != 0
                                || adliye.getValue() != null && !"- ADLİYE -".equalsIgnoreCase(adliye.getValue().toString().trim())
                        ) {
                            if (city.getValue() != null && city.getValue().getId() != 0) {
                                dyn.where("city ='" + city.getValue().getId() + "'").or(looksUp[i] + " like '%" + nQ + "%' ");
                            }
                            if (adliye.getValue() != null && !"- ADLİYE -".equalsIgnoreCase(adliye.getValue().toString().trim())) {
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
                    dyn.where("city ='" + city.getValue().getId() + "'");
                }
                if (adliye.getValue() != null && !adliye.getValue().toString().trim().equals("- ADLİYE -")) {
                    dyn.where("adliye ='" + adliye.getValue().toString().trim() + "'");
                }
                if (zone.getValue() != null && zone.getValue().getId() != 0) {
                    dyn.where("zone ='" + zone.getValue().getId() + "'");
                }
            }
            ra = dyn.orderBy(DSL.field("file_number").desc());


        } else {
            //filtre yokken yapılacaklar ile ilgili kısım
            ra = dyn.orderBy(DSL.field("file_number").desc());
        }

        totalResult = ra.fetchCount();

        for (Record rec :
                ra.fetch()) {
            data.add(new LFile(
                    Integer.parseInt(rec.get("id").toString()),
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

    public static void removeFile(Integer id) {
        try {
            DSLContext query = DSL.using(Controller.conn, SQLDialect.SQLITE);
            query.deleteFrom(DSL.table("local_files"))
                    .where("id='" + id + "'").returning().fetchOne();
        } catch (Exception ex) {
            Controller.showAlert("İşlem Yapılamadı", ex.getMessage());
        }
    }

    public static void editLocalFile(Integer id, String fileName, String type1, String type2, Zone zone, City city, String adliye, String icraDairesi, String hacizGunu, String evliyat) {
        try {
            String localFileName = fileName.toUpperCase().replaceAll("_", "");
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
                    .set(DSL.field("file_number"), localFileName)
                    .where("id='" + id + "'")
                    .execute();
        } catch (Exception ex) {
            Controller.showAlert("İşlem Yapılamadı", ex.getMessage());
        }
    }

    public static void quickAddLocalFile(LFile dosya) {
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
            ).values(
                    dosya.getFileNumber(),
                    dosya.getType1(),
                    dosya.getType2(),
                    dosya.getEvliyat(),
                    dosya.getZoneId(),
                    dosya.getCityId(),
                    dosya.getAdliye(),
                    dosya.getIcraDairesi(),
                    dosya.getHacizGunu()
            ).returning().fetchOne();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
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

    @FXML
    private void searchKeyUp(KeyEvent event) {
        refreshList();
    }

    @FXML
    private void addPageAction() {
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
    private Runnable clearAction() {
        zone.setValue(Zones.getFirst());
        adliye.setValue("- ADLİYE -");
        city.setValue(Cities.getFirst());
        q.setText("");
        refreshList();
        return null;
    }

    public void showEditPage(LFile data) {
        try {
            EditPage editPage = new EditPage();
            Main.remoteData = data;
            editPage.start();
        } catch (Exception e) {
            System.out.println("::" + e.toString());
        }
    }

    public void showFile(boolean isFast) {
        LFile row = queryTable.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (isFast) {
            showEditPage(row);
        }
        if (row != temp) {
            temp = row;
            lastClickTime = new Date();
        } else if (row == temp) {
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (diff < 300) { //another click registered in 300 millis
                showEditPage(row);
            } else {
                lastClickTime = new Date();
            }
        }
    }

    @FXML
    private void showFile() {
        showFile(false);
    }
}
