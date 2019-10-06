import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AddPage implements EventHandler<ActionEvent> {
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
    DatePicker haciz_gunu;
    @FXML
    TextField city;
    @FXML
    TextArea address;

    private Stage primaryStage;

    public void start() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/AddLocalFile.fxml"));
        primaryStage = new Stage();
        primaryStage.setTitle("Yeni Dosya Ekle");

        Image image = new Image("/assets/notebook.png");
        primaryStage.getIcons().add(image);

        Scene screen = new Scene(root);


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

        Searcher.addLocalFile(
                file_number.getText(),
                type_1.getText(),
                type_2.getText(),
                zone.getValue().toString(),
                city.getText(),
                adliye.getText(),
                icra_dairesi.getText(),
                haciz_gunu.getValue().toString(),
                address.getText());
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
}
