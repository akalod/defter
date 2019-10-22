package fxmltableview;

import java.util.Arrays;
import java.util.List;

public class Zones {
    public static List<Zone> list = Arrays.asList(
            new Zone(0, "- BÖLGE -"), //0
            new Zone(1, "MARMARA BÖLGESİ"), //1
            new Zone(2, "AKDENİZ BÖLGESİ"), //2
            new Zone(3, "DOĞU ANADOLU BÖLGESİ"),//3
            new Zone(4, "EGE BÖLGESİ"), //4
            new Zone(5, "GÜNEYDOĞU ANADOLU BÖLGESİ"), //5
            new Zone(6, "İÇ ANADOLU BÖGESİ"), //6
            new Zone(7, "KARADENİZ BÖLGESİ") //7
    );

    public static List<Zone> getAll() {
        return list;
    }

    public static Zone getFirst() {
        return list.get(0);
    }

    public static Zone getZone(Integer id) {
        return list.get(id);
    }

    public static Integer getIdByString(String par) {
        Integer result = 0;

        for (final Zone zone : list) {
            if (zone.getName().toUpperCase().trim().equals(par.toUpperCase().trim())) {
                result = zone.getId();
            }
        }

        return result;
    }

}
