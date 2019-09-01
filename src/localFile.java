import javafx.scene.control.TableView;

public class localFile {

    String zone,city,branch,file_number,type_1,type_2;

    public localFile(String zone, String city, String branch, String file_number, String type_1, String type_2){
        this.branch = branch;
        this.city = city;
        this.zone = zone;
        this.file_number = file_number;
        this.type_1 = type_1;
        this.type_2 = type_2;
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
