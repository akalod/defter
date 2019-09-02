import fxmltableview.LFile;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.net.URL;
import java.util.ResourceBundle;

public class Searcher implements EventHandler<ActionEvent>, Initializable {

    @FXML
    private TableView<LFile> queryTable;

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


    public void refreshList() {
        ObservableList<LFile> data = queryTable.getItems();
        data.clear();
        DSLContext create = DSL.using(Controller.conn);
      //  Record r = create.select(trim("*")).from("local_files").fetchAny();

        for (Record rec : DSL
                .using(Controller.conn)
                .selectFrom("local_files")
                .orderBy( DSL.field("file_number"))
                .fetch()) {
            data.add(new LFile(rec.get("zone").toString(), rec.get("city").toString(), rec.get("branch").toString(), rec.get("type_1").toString(), rec.get("type_2").toString(), rec.get("file_number").toString()));
        }


    }


    public static void addLocalFile(String fileName, String type1, String type2, String zone, String city, String branch, String address) {
        try {
            DSLContext query = DSL.using(Controller.conn, SQLDialect.SQLITE);
            query.insertInto(
                    DSL.table("local_files"),
                    DSL.field("file_number"),
                    DSL.field("type_1"),
                    DSL.field("type_2"),
                    DSL.field("address"),
                    DSL.field("zone"),
                    DSL.field("city"),
                    DSL.field("branch")
            ).values(fileName, type1, type2, address, zone, city, branch).returning().fetchOne();

        } catch (Exception ex) {
            Controller.showAlert("İşlem Yapılamadı", ex.getMessage());
        }
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println(event.getEventType());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        refreshList();
        Main.searchLayer = this;
    }

    @FXML
    private void searchHandle(ActionEvent event){

    }

    @FXML
    private void addPageAction(ActionEvent event) {
        System.out.println(event.getSource());
        Main.addPageLayer = new AddPage();
        refreshList();
        try {
            Main.addPageLayer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
