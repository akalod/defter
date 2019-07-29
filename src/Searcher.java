import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class Searcher implements EventHandler<ActionEvent> {
    Button button;

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Views/Search.fxml"));
        primaryStage.setTitle("Defter: Arama");

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
