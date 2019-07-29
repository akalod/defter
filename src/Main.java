import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static Login loginLayer;
    public static Searcher searchLayer;
    public static AddPage addPageLayer;
    public static String dbName = "defter.db";
    public static String sql = "jdbc:sqlite:" + dbName;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller.startup();
        Controller.selectFirst(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
