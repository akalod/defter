import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.vas7n.va.widgets.MaskField;

public class AddPage implements EventHandler<ActionEvent> {
    @FXML
    MaskField file_number;
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
}
