package akalod;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Zone {
    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleIntegerProperty id = new SimpleIntegerProperty(0);

    public Zone() {
        this(0, "");
    }

    public Zone(Integer id, String name) {
        setName(name);
        setId(id);
    }

    public void setName(String par) {
        name.set(par);
    }

    public void setId(Integer par) {
        id.set(par);
    }

    public String getName() {
        return name.get().toUpperCase();
    }

    public Integer getId() {
        return id.get();
    }

    @Override
    public String toString() {
        return name.get().toUpperCase();
    }
}
