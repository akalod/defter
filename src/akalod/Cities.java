package akalod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Cities {
    public static List<City> list = Arrays.asList(
            new City(0, 0, "- Şehir -"), // index 0
            new City(1, 2, "Adana"),
            new City(2, 5, "Adıyaman"),
            new City(3, 4, "Afyonkarahisar"),
            new City(4, 3, "Ağrı"),
            new City(5, 7, "Amasya"),
            new City(6, 6, "Ankara"),
            new City(7, 2, "Antalya"),
            new City(8, 7, "Artvin"),
            new City(9, 4, "Aydın"),
            new City(10, 1, "Balıkesir"),
            new City(11, 1, "Bilecik"),
            new City(12, 3, "Bingöl"),
            new City(13, 3, "Bitlis"),
            new City(14, 7, "Bolu"),
            new City(15, 2, "Burdur"),
            new City(16, 1, "Bursa"),
            new City(17, 1, "Çanakkale"),
            new City(18, 6, "Çankırı"),
            new City(19, 7, "Çorum"),
            new City(20, 4, "Denizli"),
            new City(21, 5, "Diyarbakır"),
            new City(22, 1, "Edirne"),
            new City(23, 3, "Elazığ"),
            new City(24, 3, "Erzincan"),
            new City(25, 3, "Erzurum"),
            new City(26, 6, "Eskişehir"),
            new City(27, 5, "Gaziantep"),
            new City(28, 7, "Giresun"),
            new City(29, 7, "Gümüşhane"),
            new City(30, 3, "Hakkari"),
            new City(31, 2, "Hatay"),
            new City(32, 2, "Isparta"),
            new City(33, 2, "Mersin(İçel)"),
            new City(34, 1, "İstanbul"),
            new City(35, 4, "İzmir"),
            new City(36, 3, "Kars"),
            new City(37, 7, "Kastamonu"),
            new City(38, 6, "Kayseri"),
            new City(39, 1, "Kırklareli"),
            new City(40, 6, "Kırşehir"),
            new City(41, 1, "Kocaeli"),
            new City(42, 6, "Konya"),
            new City(43, 4, "Kütahya"),
            new City(44, 3, "Malatya"),
            new City(45, 4, "Manisa"),
            new City(46, 2, "Kahramanmaraş"),
            new City(47, 5, "Mardin"),
            new City(48, 4, "Muğla"),
            new City(49, 3, "Muş"),
            new City(50, 6, "Nevşehir"),
            new City(51, 6, "Niğde"),
            new City(52, 7, "Ordu"),
            new City(53, 7, "Rize"),
            new City(54, 1, "Sakarya"),
            new City(55, 7, "Samsun"),
            new City(56, 5, "Siirt"),
            new City(57, 7, "Sinop"),
            new City(58, 6, "Sivas"),
            new City(59, 1, "Tekirdağ"),
            new City(60, 7, "Tokat"),
            new City(61, 7, "Trabzon"),
            new City(62, 3, "Tunceli"),
            new City(63, 5, "Şanlıurfa"),
            new City(64, 4, "Uşak"),
            new City(65, 3, "Van"),
            new City(66, 6, "Yozgat"),
            new City(67, 7, "Zonguldak"),
            new City(68, 6, "Aksaray"),
            new City(69, 7, "Bayburt"),
            new City(70, 6, "Karaman"),
            new City(71, 6, "Kırıkkale"),
            new City(72, 5, "Batman"),
            new City(73, 5, "Şırnak"),
            new City(74, 7, "Bartın"),
            new City(75, 3, "Ardahan"),
            new City(76, 3, "Iğdır"),
            new City(77, 1, "Yalova"),
            new City(78, 7, "Karabük"),
            new City(79, 5, "Kilis"),
            new City(80, 2, "Osmaniye"),
            new City(81, 7, "Düzce")
    );

    public static List<City> filtered = new ArrayList<City>();

    public static City getFirst() {
        return list.get(0);
    }

    public static List<City> getAll() {
        return list.stream().sorted(Comparator.comparing(City::getName)).collect(Collectors.toList());
    }

    public static List<City> getByZone(Integer zoneId) {

        filtered.clear();
        for  (final City city : list) {
            if(city.getId()==0 || city.getZoneId().equals(zoneId)){
                filtered.add(city);
            }
        }

        return filtered.stream().sorted(Comparator.comparing(City::getName)).collect(Collectors.toList());
    }

    public static City getCity(Integer id){
        try {
            return list.get(id);
        }catch (Exception e){
            return new City(0,0,"-");
        }
    }

    public static String getCity(String id){
        return getCity(Integer.parseInt(id)).getName();
    }

    public static Integer getIdByString(String par){
        Integer result = 0;
        par = par.toUpperCase().trim();

        for  (final City city : list) {
            if( city.getName().equals(par)){
                return city.getId();
            }
        }

        return  result;
    }
}
