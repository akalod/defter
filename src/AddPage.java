import fxmltableview.Cities;
import fxmltableview.City;
import fxmltableview.Zone;
import fxmltableview.Zones;
import javafx.event.ActionEvent;
import javafx.event.Event;
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

public class AddPage implements EventHandler<ActionEvent>, Initializable {
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

    private Stage primaryStage;

    public void start() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/AddLocalFile.fxml"));
        primaryStage = new Stage();
        primaryStage.setTitle("Yeni Dosya Ekle");

        Image image = new Image("/assets/notebook.png");
        primaryStage.getIcons().add(image);

        Scene screen = new Scene(root);

        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                primaryStage.close();
            }
        });
        primaryStage.setResizable(false);
        primaryStage.setScene(screen);
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println(event.getEventType());
    }

    @FXML
    private void addAction(ActionEvent event) {

        String localFileName = file_number.getText().toUpperCase().replaceAll("_","");

        if (localFileName.equals("/")
                || city.getValue().getId().equals(0)
                || type_1.getText().equals("")
                || type_2.getText().equals("")
                || adliye.getText().equals("")
                || icra_dairesi.getText().equals("")
                || zone.getValue().getId().equals(0)
        ) {
            Controller.showWarning("İşlem Yapılamadı", "Lütfen gerekli alanları doldurun");
            return;
        }
        Searcher.addLocalFile(
                localFileName,
                type_1.getText(),
                type_2.getText(),
                zone.getValue(),
                city.getValue(),
                adliye.getText(),
                icra_dairesi.getText(),
                haciz_gunu.getValue().toString(),
                evliyat.getText());
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Main.searchLayer.loadAllLists();
    }

    @FXML
    private javafx.scene.control.Button closeButton;

    @FXML
    private void cancelAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void keyUp(KeyEvent e) {
        if (e.getCode().isLetterKey() || e.getCode().isDigitKey()) {

            String t = file_number.getText();
            if (t.length() == 4 && t.matches("[-+]?\\d*\\.?\\d+")) {
                t = t + "/";
            } else if (t.length() < 4 && !t.matches("[-+]?\\d*\\.?\\d+")) {
                t = "";
            }
            file_number.setText(t);
            file_number.endOfNextWord();
            file_number.positionCaret(file_number.getCaretPosition());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        zone.getItems().addAll(Zones.getAll());
        zone.setValue(Zones.getZone(1));
        city.getItems().addAll(Cities.getByZone(zone.getValue().getId()));
        city.setValue(Cities.getFirst());

        zone.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                city.getItems().clear();
                city.getItems().addAll(Cities.getByZone(zone.getValue().getId()));
            }
        });
    }
}
