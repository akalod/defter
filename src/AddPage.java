import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AddPage implements EventHandler<ActionEvent> {
    Button button;
    @FXML
    TextField file_number;
    @FXML
    TextField type_1;
    @FXML
    TextField type_2;
    @FXML
    TextField zone;
    @FXML
    TextField branch;
    @FXML
    TextField city;
    @FXML
    TextArea address;

    private Stage primaryStage;

    public void start() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/AddLocalFile.fxml"));
        primaryStage = new Stage();
        primaryStage.setTitle("Defter: Ekle");

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

        Searcher.addLocalFile(file_number.getText(), type_1.getText(), type_2.getText(), zone.getText(), city.getText(), branch.getText(), address.getText());
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        //Controller.refreshList();
    }

    @FXML
    private javafx.scene.control.Button closeButton;

    @FXML
    private void cancelAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
