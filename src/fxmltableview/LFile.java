package fxmltableview;

import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LFile {

    private final SimpleStringProperty zone = new SimpleStringProperty("");
    private final SimpleStringProperty city = new SimpleStringProperty("");
    private final SimpleStringProperty adliye = new SimpleStringProperty("");
    private final SimpleStringProperty icraDairesi = new SimpleStringProperty("");
    private final SimpleStringProperty hacizGunu = new SimpleStringProperty("");
    private final SimpleStringProperty type1 = new SimpleStringProperty("");
    private final SimpleStringProperty type2 = new SimpleStringProperty("");
    private final SimpleStringProperty fileNumber = new SimpleStringProperty("");
    private final SimpleStringProperty evliyat = new SimpleStringProperty("");

    public LFile() {
        this("", "", "", "", "", "", "", "", "");
    }

    public LFile(String zone, String city, String adliye, String type1, String type2, String fileNumber, String hacizGunu, String icraDairesi, String evliyat) {
        setZone(zone);
        setCity(city);
        setAdliye(adliye);
        setHacizGunu(hacizGunu);
        setIcraDairesi(icraDairesi);
        setType1(type1);
        setType2(type2);
        setFileNumber(fileNumber);
        setEvliyat(evliyat);
    }

    public void setHacizGunu(String par) {
        hacizGunu.set(par);
    }

    public void setIcraDairesi(String par) {
        icraDairesi.set(par);
    }

    public void setEvliyat(String par) {
        evliyat.set(par);
    }

    public void setZone(String par) {
        zone.set(par);
    }

    public void setCity(String par) {
        city.set(par);
    }

    public void setAdliye(String par) {
        adliye.set(par);
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
        return type2.get();
    }

    public String getAdliye() {
        return adliye.get();
    }

    public String getIcraDairesi() {
        return icraDairesi.get();
    }

    public String getHacizGunu() {
        return hacizGunu.get();
    }

    public LocalDate getHacizGunuDate(){
        return LocalDate.parse(hacizGunu.get());
    }

    public String getEvliyat() {
        return evliyat.get();
    }
}
