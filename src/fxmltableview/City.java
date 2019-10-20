package fxmltableview;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class City {

    private final SimpleIntegerProperty id = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty zone_id = new SimpleIntegerProperty(0);
    private final SimpleStringProperty name = new SimpleStringProperty("");

    public City() {
        this(0, 0, "");
    }

    public City(Integer id, Integer zone_id, String name) {
        setId(id);
        setZoneId(zone_id);
        setName(name);
    }

    public void setName(String par) {
        name.set(par);
    }

    public void setZoneId(Integer par) {
        zone_id.set(par);
    }

    public void setId(Integer par) {
        id.set(par);
    }

    public Integer getId() {
        return id.get();
    }

    public Integer getZoneId() {
        return zone_id.get();
    }

    public String getName() {
        return name.get().toUpperCase();
    }


    @Override
    public String toString() {
        return name.get().toUpperCase();
    }
}
