package dziennik_szkolny.DAO;

import dziennik_szkolny.models.Ocena;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OcenaDAO {

    private static final String url =  "jdbc:sqlite:dziennik.db";
    public static List<Ocena> getOceny(String email){
        List<Ocena> oceny = new ArrayList<>();
        String sql = "select o.wartosc, o.waga, o.opis, o.data_wystawienia, p.nazwa, n.imie, n.nazwisko from ocena o join uczen u using(id_ucznia) join przedmiot p using(id_przedmiotu) join nauczyciel n using(id_nauczyciela) where u.email =?";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                oceny.add(
                        new Ocena(
                                rs.getInt("wartosc"),
                                rs.getInt("waga"),
                                rs.getString("opis"),
                                rs.getString("data_wystawienia"),
                                rs.getString("nazwa"),
                                rs.getString("imie"),
                                rs.getString("nazwisko")
                        )
                );
            }

        }catch(Exception e){
            System.out.println("wystąpił błąd pobierania danych!"+ e.getMessage());

        }
        return oceny;
    }
}
