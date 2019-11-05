import akalod.Cities;
import akalod.LFile;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class Main extends Application {

    public static Login loginLayer;
    public static Searcher searchLayer;
    public static AddPage addPageLayer;
    public static String dbName = "sys64.sys";
    public static String sql = "jdbc:sqlite:" + dbName;

    public static LFile remoteData;

    public static List<String> MACAddress = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller.startup();
        Controller.selectFirst(primaryStage);
    }


    public static void main(String[] args) throws SocketException {
        Locale.setDefault(new Locale("tr", "TR"));
        if(Controller.FileLister) {
            final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (((Enumeration) e).hasMoreElements()) {
                final byte[] mac = e.nextElement().getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++)
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                   MACAddress.add(sb.toString());//MAC Adreslerinin dizine ekleme
                }
            }
        }
        launch(args);
    }
    static void selector(KeyEvent event, Scene scene, ComboBox city, ComboBox zone, ComboBox haciz_gunu){
        if (event.getCode().isLetterKey()) {

            String element = scene.focusOwnerProperty().get().getId();
            ComboBox selectionModel = null;
            ObservableList selectionItems = null;
            boolean run = false;
            switch (element) {
                case "city":
                    selectionModel = city;
                    run = true;
                    break;
                case "zone":
                    selectionModel = zone;
                    run = true;
                    break;
                case "haciz_gunu":
                    selectionModel = haciz_gunu;
                    run = true;
                    break;
                default:

            }
            if (run) {
                try {
                    selectionItems = selectionModel.getItems();
                    Object selectedItem = selectionModel.getSelectionModel().getSelectedItem();
                    for (Object item :
                            selectionItems) {
                        if (item.toString().toUpperCase().startsWith(event.getCode().toString().toUpperCase()) && selectedItem != item) {
                            selectionModel.setValue(item);
                            break;
                        }
                    }

                } catch (Exception e) {
                    System.out.println("---e----");
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
