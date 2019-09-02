package fxmltableview;

import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

public class LFile {

    private final SimpleStringProperty zone = new SimpleStringProperty("");
    private final SimpleStringProperty city = new SimpleStringProperty("");
    private final SimpleStringProperty branch = new SimpleStringProperty("");
    private final SimpleStringProperty type1 = new SimpleStringProperty("");
    private final SimpleStringProperty type2 = new SimpleStringProperty("");
    private final SimpleStringProperty fileNumber = new SimpleStringProperty("");

    public LFile() {
        this("", "", "","","","");
    }

    public LFile(String zone, String city, String branch, String type1, String type2, String fileNumber) {
        setZone(zone);
        setCity(city);
        setBranch(branch);
        setType1(type1);
        setType2(type2);
        setFileNumber(fileNumber);
    }

    public void setZone(String par) {
        zone.set(par);
    }

    public void setCity(String par) {
        city.set(par);
    }

    public void setBranch(String par) {
        branch.set(par);
    }

    public void setType1(String par) {
        type1.set(par);
    }

    public void setType2(String par) {
        type2.set(par);
    }

    public void setFileNumber(String par) {
        fileNumber.set(par);
    }

    public String getBranch() {
        return branch.get();
    }

    public String getCity() {
        return city.get();
    }

    public String getZone() {
        return zone.get();
    }

    public String getFileNumber() {
        return fileNumber.get();
    }

    public String getType1() {
        return type1.get();
    }

    public String getType2() {
        return type1.get();
    }



    /*
    public getColumns(){

          <TableColumn  text="Bölge" />
          <TableColumn  text="Şehir" />
          <TableColumn  text="Şube" />
          <TableColumn  text="Dosya/No" />
          <TableColumn  text="Alacaklı" />
          <TableColumn  text="Borçlu" />
    }*/
}
