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
    public List<Uczen> wszyscyUczniowie(){
        List<Uczen> uczniowie = new ArrayList<>();
        String sql = "select * from uczen";
        try(
            Connection con = DriverManager.getConnection(url);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ){
            while(rs.next()) {
                int id = rs.getInt("id_ucznia");
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");
                int id_klasy = rs.getInt("id_klasy");
                String pesel = rs.getString("pesel");
                String data_urodzenia = rs.getString("data_urodzenia");
                String email = rs.getString("email");
                String haslo = rs.getString("haslo");
                String numer_telefonu = rs.getString("numer_telefonu");
                Uczen u = new Uczen(id, imie, nazwisko, id_klasy, pesel, data_urodzenia, email, haslo, numer_telefonu);
                uczniowie.add(u);
            }
        }catch(SQLException e){
            System.out.println("Błąd pobierania uczniów: " + e.getMessage());
        }
        return uczniowie;
    }
}
