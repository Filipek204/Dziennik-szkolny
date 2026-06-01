package DAO;

import models.Uczen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UczenDAO {
    private static final String url =  "jdbc:sqlite:dziennik.db";
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
