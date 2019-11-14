import akalod.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.vas7n.va.widgets.MaskField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditPage implements EventHandler<ActionEvent>, Initializable {
    @FXML
    Button cancel;
    @FXML
    MaskField file_number;
    @FXML
    TextField type_1;
    @FXML
    TextField type_2;
    @FXML
    private ComboBox<Zone> zone;
    @FXML
    TextField icra_dairesi;
    @FXML
    TextField adliye;
    @FXML
    ComboBox haciz_gunu;
    @FXML
    private ComboBox<City> city;
    @FXML
    TextField evliyat;

    @FXML
    Integer id;

    private Stage stage;


    public void start() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/EditLocalFile.fxml"));

        stage = new Stage();
        stage.setTitle("Dosya Güncelle");

        Image image = new Image("/assets/notebook.png");
        stage.getIcons().add(image);

        Scene screen = new Scene(root);

        stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                stage.close();
            }
        });
        stage.setResizable(false);
        stage.setScene(screen);
        stage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println(event.getEventType());
    }


    @FXML
    private void cancelAction() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zone.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                city.getItems().clear();
                city.getItems().addAll(Cities.getByZone(zone.getValue().getId()));
                city.setValue(Cities.getFirst());
            }
        });
        LFile lData = Main.remoteData;

        type_1.setText(lData.getType1());
        type_2.setText(lData.getType2());
        file_number.setText(lData.getFileNumber());
        adliye.setText(lData.getAdliye());
        evliyat.setText(lData.getEvliyat());
        zone.getItems().addAll(Zones.getAll());
        zone.setValue(Zones.getFirst());
        id = lData.getId();
        if (lData.getZoneId() != 0) {
            zone.setValue(Zones.getZone(lData.getZoneId()));
        }
        if (lData.getCityId() != 0) {
            city.getItems().setAll(Cities.getByZone(lData.getZoneId()));
            city.setValue(Cities.getCity(lData.getCityId()));
        }
        haciz_gunu.setValue(lData.getHacizGunu());
        icra_dairesi.setText(lData.getIcraDairesi());
    }

    @FXML
    private void removeAction() {
        Searcher.removeFile(id);
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
        Main.searchLayer.loadAllLists();
    }

    @FXML
    private void editAction() {
        Searcher.editLocalFile(
                id,
                file_number.getText(),
                type_1.getText(),
                type_2.getText(),
                zone.getValue(),
                city.getValue(),
                adliye.getText(),
                icra_dairesi.getText(),
                haciz_gunu.getValue().toString(),
                evliyat.getText());
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
        Main.searchLayer.loadAllLists();
    }

    @FXML
    public void keyEvent(KeyEvent event) {
        Main.selector(event, cancel.getScene(), city, zone, haciz_gunu,null);
    }
}
