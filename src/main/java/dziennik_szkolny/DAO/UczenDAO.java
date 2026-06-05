package dziennik_szkolny.DAO;

import dziennik_szkolny.models.Uczen;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UczenDAO {
    private static final String url =  "jdbc:sqlite:dziennik.db";

    public static boolean logowanieUczen(String email, String haslo){
        String sqlLog = "select haslo from uczen where email = ?";
        try(
                Connection con = DriverManager.getConnection(url);
                PreparedStatement pstmt = con.prepareStatement(sqlLog)
        ){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                String hashHaslo = rs.getString("haslo");
                return BCrypt.checkpw(haslo, hashHaslo);
            }
            return false;

        }catch(Exception e){
            System.out.println("Błąd połączenia z bazą: " + e.getMessage());
            return false;
        }
    }
    public static Uczen daneUcznia(String email){
        String sql = "select u.id_ucznia, u.imie, u.nazwisko, u.pesel, u.data_urodzenia, u.email, u.numer_telefonu, k.nazwa as klasa from uczen u join klasa k using(id_klasy) where u.email =?";
        try(
            Connection con = DriverManager.getConnection(url);
            PreparedStatement pstmt = con.prepareStatement(sql);
        ){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {

                return new Uczen(rs.getInt("id_ucznia"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("pesel"),
                        rs.getString("data_urodzenia"),
                        rs.getString("email"),
                        rs.getString("numer_telefonu"),
                        rs.getString("klasa")

                );
            }
        }catch(SQLException e){
            System.out.println("Błąd pobierania danych ucznia! " + e.getMessage());
        }
        return null;
    }
    public static List<Uczen> getKlasaWychowawcza( String email){
        List<Uczen> listauczniow = new ArrayList<>();
        String sql = "select u.id_ucznia, u.imie, u.nazwisko, u.pesel, u.data_urodzenia, u.email, u.numer_telefonu, k.nazwa as klasa from uczen u join klasa k using(id_klasy) join nauczyciel n using(id_nauczyciela) where n.email =? order by u.nazwisko, u.imie";
        try(
                Connection con = DriverManager.getConnection(url);
                PreparedStatement pstmt = con.prepareStatement(sql);
        ){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {

                listauczniow.add(new Uczen(rs.getInt("id_ucznia"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("pesel"),
                        rs.getString("data_urodzenia"),
                        rs.getString("email"),
                        rs.getString("numer_telefonu"),
                        rs.getString("klasa")

                ));
            }
        }catch(SQLException e){
            System.out.println("Błąd pobierania danych ucznia! " + e.getMessage());
        }
        return listauczniow;
    }
}
