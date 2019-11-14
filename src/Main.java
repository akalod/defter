import akalod.Cities;
import akalod.LFile;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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
        if (Controller.FileLister) {
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

    static void selector(KeyEvent event, Scene scene, ComboBox comboBoxOne, ComboBox comboBoxTwo, ComboBox comboBoxThree, ComboBox comboBoxFour) {


        System.out.println(event.getCode());

        List<String> allow = new ArrayList<>();
        List<String> change = new ArrayList<>();

        allow.add("SEMICOLON"); //ş
        change.add("Ş");
        allow.add("PERIOD"); //ç
        change.add("Ç");
        allow.add("CLOSE_BRACKET"); //ü
        change.add("Ü");
        allow.add("COMMA"); //ö
        change.add("Ö");
        allow.add("QUOTE");
        change.add("İ");

        int index = allow.indexOf(event.getCode().toString());

        if (event.getCode().isLetterKey() || index != -1) {

            Node element = scene.focusOwnerProperty().get();
            ComboBox selectionModel = null;
            ObservableList selectionItems = null;
            boolean run = false;
            String charStart = event.getCode().toString().toUpperCase();

            if (index != -1) {
                charStart = change.get(index);
            }

            if (element == comboBoxOne) {
                selectionModel = comboBoxOne;
                run = true;
            } else if (element == comboBoxTwo) {
                selectionModel = comboBoxTwo;
                run = true;
            } else if (element == comboBoxThree) {
                selectionModel = comboBoxThree;
                run = true;
            }else if (element == comboBoxFour) {
                selectionModel = comboBoxFour;
                run = true;
            }

            if (run) {
                try {
                    selectionItems = selectionModel.getItems();
                    Object selectedItem = selectionModel.getSelectionModel().getSelectedItem();
                    for (Object item :
                            selectionItems) {
                        if (item.toString().toUpperCase().startsWith(charStart) && selectedItem != item) {
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
