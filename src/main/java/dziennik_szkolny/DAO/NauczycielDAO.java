package dziennik_szkolny.DAO;

import dziennik_szkolny.models.Nauczyciel;
import dziennik_szkolny.models.OcenaDziennik;
import dziennik_szkolny.models.Przedmiot_Klasa;
import dziennik_szkolny.models.UczenDziennik;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.parallelPrefix;

public class NauczycielDAO {
    private static final String url =  "jdbc:sqlite:dziennik.db";

    public static boolean logowanieNauczyciel(String email, String haslo){
        String sqlLog = "select haslo from nauczyciel where email = ?";
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
    public static Nauczyciel getWychowawca(String email){
        String sqlWychowawca = "select n.imie, n.nazwisko, n.numer_telefonu, n.email, replace(group_concat(distinct p.nazwa), ',', ', ') as przedmioty from uczen u join klasa k using(id_klasy) join nauczyciel n using (id_nauczyciela) join nauczyciel_przedmiot_klasa npk on npk.id_nauczyciela = n.id_nauczyciela join przedmiot p on npk.id_przedmiotu = p.id_przedmiotu where u.email =?";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(sqlWychowawca)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return new Nauczyciel(
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("numer_telefonu"),
                        rs.getString("email"),
                        "Wychowawca klasy, "+rs.getString("przedmioty"));
            }

        }catch(Exception e){
            System.out.println("Błąd podczas pobierania wychowawcy klasy! "+e.getMessage());
        }
        return null;
    }
    public static List<Nauczyciel> getNauczyciele(String email){
        String sqlNauczyciele = "select n.imie, n.nazwisko, n.numer_telefonu, n.email, group_concat(p.nazwa, ', ') as przedmioty from uczen u join klasa k on u.id_klasy = k.id_klasy join nauczyciel_przedmiot_klasa npk on npk.id_klasy = k.id_klasy join nauczyciel n on n.id_nauczyciela = npk.id_nauczyciela join przedmiot p on npk.id_przedmiotu = p.id_przedmiotu where u.email =? group by n.id_nauczyciela";
        List<Nauczyciel> nauczyciele = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlNauczyciele)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                nauczyciele.add(new Nauczyciel(
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("numer_telefonu"),
                        rs.getString("email"),
                        rs.getString("przedmioty")));
            }

        }catch(Exception e){
            System.out.println("Błąd podczas pobierania nauczycieli klasy! "+e.getMessage());
        }
        return nauczyciele;
    }
    public static String getKlasaWychowawcy(String email){
        String sqlKlasaWychowawcy = "select k.nazwa as klasa from klasa k join nauczyciel n using (id_nauczyciela) where n.email =?";

        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(sqlKlasaWychowawcy)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getString("klasa");
            }
        }catch(Exception e){
            System.out.println("Błąd przy pobieraniu klasy wychowawcy! "+e.getMessage());
        }
        return null;
    }
    public static List<Przedmiot_Klasa> getPrzydzialy(String email){
        List<Przedmiot_Klasa> listaPrzydzialow = new ArrayList<>();
        String sqlPrzydzialy = "select k.nazwa as klasa, group_concat(distinct p.nazwa) as przedmioty from nauczyciel n join nauczyciel_przedmiot_klasa npk on npk.id_nauczyciela=n.id_nauczyciela join klasa k on npk.id_klasy = k.id_klasy join przedmiot p on npk.id_przedmiotu = p.id_przedmiotu where n.email =? group by k.id_klasy order by k.nazwa";

        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(sqlPrzydzialy)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){

                String[] tablicaPrzydzialow = rs.getString("przedmioty").split(",");
                listaPrzydzialow.add(new Przedmiot_Klasa(rs.getString("klasa"),asList(tablicaPrzydzialow)));
            }


        }catch(Exception e){
            System.out.println("Błąd podczas pobierania przydziałów klas! "+e.getMessage());
        }
        return listaPrzydzialow;
    }
    public static List<UczenDziennik> getDziennikOcen(String nazwaKlasy, String nazwaPrzedmiotu){
        List<UczenDziennik> listaUczniow = new ArrayList<>();

        String sqlUczniowie = "select u.id_ucznia, u.imie, u.nazwisko from uczen u join klasa k using(id_klasy) where k.nazwa =? order by u.nazwisko, u.imie";
        String sqlOceny = "select o.id_oceny, o.wartosc, o.waga, o.opis from ocena o join przedmiot p using(id_przedmiotu) where o.id_ucznia =? and p.nazwa =?";

        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmtUczniowie = conn.prepareStatement(sqlUczniowie);
        PreparedStatement pstmtOceny = conn.prepareStatement(sqlOceny)){
            pstmtUczniowie.setString(1,nazwaKlasy);
            ResultSet rsUczniowie = pstmtUczniowie.executeQuery();

            while(rsUczniowie.next()){
                int idUcznia = rsUczniowie.getInt("id_ucznia");
                String imieNazwisko = rsUczniowie.getString("imie")+" "+rsUczniowie.getString("nazwisko");

                List<OcenaDziennik> ocenyUcznia = new ArrayList<>();
                pstmtOceny.setInt(1, idUcznia);
                pstmtOceny.setString(2, nazwaPrzedmiotu);

                ResultSet rsOceny = pstmtOceny.executeQuery();

                while(rsOceny.next()){
                    ocenyUcznia.add(new OcenaDziennik(
                            rsOceny.getInt("id_oceny"),
                            rsOceny.getString("wartosc"),
                            rsOceny.getInt("waga"),
                            rsOceny.getString("opis")
                    ));
                }
                listaUczniow.add( new UczenDziennik(idUcznia, imieNazwisko, ocenyUcznia));
            }
        }catch (Exception e){
            System.out.println("Błąd podczas pobierania dziennika! "+e.getMessage());
        }
        return listaUczniow;
    }
    public static boolean dodajOcene(int idUcznia, String nazwaPrzedmiotu, String emailNauczyciela, int wartosc, int waga, String opis){
        String sqlDodajOcene = "insert into ocena( wartosc, id_ucznia, id_przedmiotu, id_nauczyciela, waga, opis) values (?, ?, (select id_przedmiotu from przedmiot where nazwa =?), (select id_nauczyciela from nauczyciel where email =?), ?, ?)";

        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(sqlDodajOcene)){
            pstmt.setInt(1, wartosc);
            pstmt.setInt(2, idUcznia);
            pstmt.setString(3,nazwaPrzedmiotu);
            pstmt.setString(4, emailNauczyciela);
            pstmt.setInt(5,waga);
            pstmt.setString(6, opis);

            int zmienioneWiersze = pstmt.executeUpdate();
            return zmienioneWiersze>0;
        }catch(Exception e){
            System.out.println("Błąd podczas dodawania oceny! "+e.getMessage());
            return false;
        }
    }
    public static boolean usunOcene(int idOceny){
        String sqlUsun = "delete from ocena where id_oceny =?";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(sqlUsun)){
        pstmt.setInt(1, idOceny);
        return pstmt.executeUpdate() >0;
        }catch(Exception e){
            System.out.println("Błąd podczas usuwania oceny! "+e.getMessage());
            return false;
        }
    }
    public static boolean edytujOcene(int idOceny, int nowaWartosc, int nowaWaga, String nowyOpis){
        String sqlUsun = "update ocena set wartosc=?, waga=?, opis=? where id_oceny =?";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlUsun)){
            pstmt.setInt(1, nowaWartosc);
            pstmt.setInt(2, nowaWaga);
            pstmt.setString(3, nowyOpis);
            pstmt.setInt(4, idOceny);

            return pstmt.executeUpdate() >0;
        }catch(Exception e){
            System.out.println("Błąd podczas edytowania oceny! "+e.getMessage());
            return false;
        }
    }

    public static boolean dodajUcznia(String emailWychowawcy, String imie, String nazwisko, String pesel, String dataUr, String email, String tel){
        String sqlDodajUcznia = "insert into uczen (imie, nazwisko, pesel, data_urodzenia, email, numer_telefonu, id_klasy, haslo) values (?,?,?,?,?,?, (select id_klasy from klasa where id_nauczyciela = (select id_nauczyciela from nauczyciel where email=?)), ?)";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(sqlDodajUcznia)){

        pstmt.setString(1, imie);
        pstmt.setString(2, nazwisko);
        pstmt.setString(3, pesel);
        pstmt.setString(4, dataUr);
        pstmt.setString(5, email);
        pstmt.setString(6, tel);
        pstmt.setString(7, emailWychowawcy);
            pstmt.setString(8, "$2a$10$55hvNXcgIjXMh98cflCQVuwo9hiJx.Zkl0SB1TmdFb71wGPoSTuGe");

        return pstmt.executeUpdate()> 0;

        }catch(Exception e){
            System.out.println("Błąd podczas dodawania ucznia: " + e.getMessage());
            return false;
        }
    }

    public static boolean edytujUcznia(int idUcznia, String imie, String nazwisko, String pesel, String dataUr, String email, String tel){
        String sqlEdytujUcznia = "update uczen set imie =?, nazwisko=?, pesel =?, data_urodzenia=?, email=?, numer_telefonu=? where id_ucznia = ?";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlEdytujUcznia)){

            pstmt.setString(1, imie);
            pstmt.setString(2, nazwisko);
            pstmt.setString(3, pesel);
            pstmt.setString(4, dataUr);
            pstmt.setString(5, email);
            pstmt.setString(6, tel);
            pstmt.setInt(7, idUcznia);

            return pstmt.executeUpdate()> 0;

        }catch(Exception e){
            System.out.println("Błąd podczas edytowania ucznia: " + e.getMessage());
            return false;
        }
    }
    public static boolean usunUcznia(int idUcznia){
        String sqlEdytujUcznia = "delete from uczen where id_ucznia = ?";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlEdytujUcznia)){

            pstmt.setInt(1, idUcznia);

            return pstmt.executeUpdate()> 0;

        }catch(Exception e){
            System.out.println("Błąd podczas usuwania ucznia: " + e.getMessage());
            return false;
        }
    }

}
