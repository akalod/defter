import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AddPage implements EventHandler<ActionEvent> {
    Button button;
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
}
