package dziennik_szkolny.DAO;

import dziennik_szkolny.models.Przedmiot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PrzedmiotDAO {
    private static final String url = "jdbc:sqlite:dziennik.db";
    public static List<Przedmiot> getPrzedmioty(String email){
        List<Przedmiot> przedmioty = new ArrayList<>();
        String sql ="select p.nazwa as przedmiot, n.imie, n.nazwisko from uczen u join klasa using(id_klasy) join nauczyciel_przedmiot_klasa npk using(id_klasy) join przedmiot p using(id_przedmiotu) join nauczyciel n on n.id_nauczyciela = npk.id_nauczyciela  where u.email =? order by p.nazwa";


        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                przedmioty.add(new Przedmiot(
                        rs.getString("przedmiot"),
                        rs.getString("imie"),
                        rs.getString("nazwisko")
                ));
            }
        }catch(Exception e) {
            System.out.println("błąd podczas pobierania przedmiotów! " + e.getMessage());
        }
        return przedmioty;
    }
}
