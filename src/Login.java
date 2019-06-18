import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Login {

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setTitle("Defter: Giriş");

        Image image = new Image("/notebook.png");
        primaryStage.getIcons().add(image);

        Scene screen = new Scene(root, 300, 275);
        screen.getStylesheets().add("style.css");

        primaryStage.setResizable(false);
        primaryStage.setScene(screen);
        primaryStage.show();
    }
}
