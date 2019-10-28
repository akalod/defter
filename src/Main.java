import akalod.Cities;
import akalod.LFile;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Main extends Application {

    public static Login loginLayer;
    public static Searcher searchLayer;
    public static AddPage addPageLayer;
    public static String dbName = "defter.db";
    public static String sql = "jdbc:sqlite:" + dbName;

    public static LFile remoteData;

    public static List<String> MACAddress = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller.startup();
        Controller.selectFirst(primaryStage);
    }


    public static void main(String[] args) throws SocketException {

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
}
