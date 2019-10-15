import fxmltableview.LFile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jooq.meta.derby.sys.Sys;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditPage implements EventHandler<ActionEvent>, Initializable {
    @FXML
    Button cancel;
    @FXML
    TextField file_number;
    @FXML
    TextField type_1;
    @FXML
    TextField type_2;
    @FXML
    ChoiceBox zone;
    @FXML
    TextField icra_dairesi;
    @FXML
    TextField adliye;
    @FXML
    ChoiceBox haciz_gunu;
    @FXML
    TextField city;
    @FXML
    TextArea evliyat;

    private Stage stage;


    public void start() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/EditLocalFile.fxml"));

        stage = new Stage();
        stage.setTitle("Dosya Güncelle");

        Image image = new Image("/assets/notebook.png");
        stage.getIcons().add(image);

        Scene screen = new Scene(root);

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
        LFile lData = Main.remoteData;
        city.setText(lData.getCity());
        type_1.setText(lData.getType1());
        type_2.setText(lData.getType2());
        file_number.setText(lData.getFileNumber());
        adliye.setText(lData.getAdliye());
        evliyat.setText(lData.getEvliyat());
        zone.setValue(lData.getZone());
        haciz_gunu.setValue(lData.getHacizGunu());
        icra_dairesi.setText(lData.getIcraDairesi());
    }

    @FXML
    private void removeAction(){
        Searcher.removeFile(file_number.getText());
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
        Main.searchLayer.loadAllLists();
    }

    @FXML
    private void editAction(){
        Searcher.editLocalFile(
                file_number.getText(),
                type_1.getText(),
                type_2.getText(),
                zone.getValue().toString(),
                city.getText(),
                adliye.getText(),
                icra_dairesi.getText(),
                haciz_gunu.getValue().toString(),
                evliyat.getText());
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
        Main.searchLayer.loadAllLists();
    }
}