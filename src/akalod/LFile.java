package akalod;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

public class LFile {

    private final SimpleStringProperty zoneName = new SimpleStringProperty("");
    private final SimpleIntegerProperty id = new SimpleIntegerProperty(0);
    private final SimpleStringProperty cityName = new SimpleStringProperty("");

    private final SimpleIntegerProperty zone = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty city = new SimpleIntegerProperty(0);
    private final SimpleStringProperty adliye = new SimpleStringProperty("");
    private final SimpleStringProperty icraDairesi = new SimpleStringProperty("");
    private final SimpleStringProperty hacizGunu = new SimpleStringProperty("");
    private final SimpleStringProperty type1 = new SimpleStringProperty("");
    private final SimpleStringProperty type2 = new SimpleStringProperty("");
    private final SimpleStringProperty fileNumber = new SimpleStringProperty("");
    private final SimpleStringProperty evliyat = new SimpleStringProperty("");

    public LFile() {
        this(0,0, 0, "", "", "", "", "", "", "");
    }

    public LFile(Integer id,Integer zone, Integer city, String adliye, String type1, String type2, String fileNumber, String hacizGunu, String icraDairesi, String evliyat) {
        setZone(zone);
        setId(id);
        setCity(city);
        setAdliye(adliye);
        setHacizGunu(hacizGunu);
        setIcraDairesi(icraDairesi);
        setType1(type1);
        setType2(type2);
        setFileNumber(fileNumber);
        setEvliyat(evliyat);
    }

    public void setId(Integer par){
        id.set(par);
    }

    public Integer getId(){
        return  id.get();
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

    public void setZone(Integer par) {
        zone.set(par);
        zoneName.set(Zones.getZone(par).getName());
    }
    public String getZone() {
        return zoneName.get();
    }

    public void setCity(Integer id, String name){
        cityName.set(name);
        city.set(id);
    }

    public void setZone(Integer id, String name){
        zoneName.set(name);
        zone.set(id);
    }

    public void setCity(Integer par) {
        cityName.set(Cities.getCity(par).getName());
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
        return cityName.get();
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

    public Integer getZoneId(){
        return zone.get();
    }

    public Integer getCityId(){
        return city.get();
    }
}
