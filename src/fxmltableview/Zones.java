package fxmltableview;

import java.util.Arrays;
import java.util.List;

public class Zones {
    public static List<Zone> list = Arrays.asList(
            new Zone(0,"Bölge"), //0
            new Zone(1,"Marmara Bölgesi"), //1
            new Zone(2,"Akdeniz Bölgesi"), //2
            new Zone(3,"Doğu Anadolu Bölgesi"),//3
            new Zone(4,"Ege Bölgesi"), //4
            new Zone(5,"Güneydoğu Anadolu Bölgesi"), //5
            new Zone(6,"İç Anadolu Bölgesi"), //6
            new Zone(7,"Karadeniz Bölgesi") //7
    );

    public  static List<Zone> getAll(){
        return list;
    }

    public static Zone getFirst(){
        return  list.get(0);
    }

    public static Zone getZone(Integer id){
        return list.get(id);
    }

}
