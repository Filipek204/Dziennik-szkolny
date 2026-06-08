package dziennik_szkolny.DAO;

import dziennik_szkolny.models.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String sqlEdytujUcznia = "update uczen set id_klasy =null where id_ucznia = ?";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlEdytujUcznia)){

            pstmt.setInt(1, idUcznia);

            return pstmt.executeUpdate()> 0;

        }catch(Exception e){
            System.out.println("Błąd podczas usuwania ucznia z klasy: " + e.getMessage());
            return false;
        }
    }
    public static boolean przypiszUcznia(int idUcznia, int idKlasy){
        String sqlEdytujUcznia = "update uczen set id_klasy =? where id_ucznia = ?";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlEdytujUcznia)){

            pstmt.setInt(1, idKlasy);
            pstmt.setInt(2, idUcznia);

            return pstmt.executeUpdate()> 0;

        }catch(Exception e){
            System.out.println("Błąd podczas przypisywania ucznia do klasy: " + e.getMessage());
            return false;
        }
    }
    public static List<UczenDziennik> getUczniowieBezKlasy(){
        List<UczenDziennik> listaUczniow = new ArrayList<>();

        String sqlUczniowieBezKlasy = "select u.id_ucznia, u.imie, u.nazwisko from uczen u where u.id_klasy is null order by u.nazwisko, u.imie";

        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlUczniowieBezKlasy)){
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
            listaUczniow.add(new UczenDziennik(
                    rs.getInt("id_ucznia"),
                    (rs.getString("imie")+" "+rs.getString("nazwisko"))));
            }
        }catch (Exception e){
            System.out.println("Błąd podczas pobierania uczniów! "+e.getMessage());
        }
        return listaUczniow;
    }
    public static int getIdKlasyWychowawcy(String email){
        String sqlIdKlasy = "select id_klasy from klasa where id_nauczyciela = (select id_nauczyciela from nauczyciel where email =?)";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(sqlIdKlasy)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt("id_klasy");
            }
        }catch(Exception e){
            System.out.println("Błąd podczas pobierania id klasy! "+ e.getMessage());

        }
        return -1;
    }
    public static List<Przedmiot> getPrzedmiotyKlasy(int idKlasy){
        String sqlprzedmioty = "select p.id_przedmiotu, p.nazwa as nazwa_przedmiotu, n.id_nauczyciela, n.imie , n.nazwisko from przedmiot p left join main.nauczyciel_przedmiot_klasa npk on npk.id_przedmiotu = p.id_przedmiotu left join nauczyciel n on n.id_nauczyciela = npk.id_nauczyciela where npk.id_klasy =? order by p.nazwa";
        List<Przedmiot> przedmioty = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlprzedmioty)){
            pstmt.setInt(1, idKlasy);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int idPrzedmiotu = rs.getInt("id_przedmiotu");
                String imie;
                String nazwisko;
                if (rs.getInt("id_nauczyciela")>0) {
                    imie = rs.getString("imie");
                    nazwisko = rs.getString("nazwisko");
                }else{
                    imie = "brak";
                    nazwisko = "brak";
                }
                przedmioty.add(new Przedmiot(rs.getString("nazwa_przedmiotu"), imie, nazwisko, rs.getInt("id_przedmiotu"), rs.getInt("id_nauczyciela")) );



            }
        }catch(Exception e){
            System.out.println("Błąd podczas pobierania przedmiotów! "+ e.getMessage());

        }
        return przedmioty;
    }
    public static List<NauczycielSzkola>getWszystcyNauczyciele(){
        List nauczyciele = new ArrayList();
        String sqlNauczyciele = "select id_nauczyciela, imie, nazwisko from nauczyciel order by nazwisko, imie";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlNauczyciele)) {
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                nauczyciele.add(new NauczycielSzkola(
                        rs.getInt("id_nauczyciela"),
                        rs.getString("imie"),
                        rs.getString("nazwisko")
                       )
                );
            }
        }catch(Exception e){
            System.out.println("Błąd podczas pobierania listy nauczycieli");
            }
        return nauczyciele;
    }
    public static boolean przypiszNauczyciela(int id_klasy, int id_przedmiotu, int id_nauczyciela){
        String sqlInstert = "insert into nauczyciel_przedmiot_klasa (id_nauczyciela, id_klasy, id_przedmiotu) values (?, ?, ?)";
        String sqlUpdate = "update nauczyciel_przedmiot_klasa set id_nauczyciela =? where id_klasy=? and id_przedmiotu=?";

        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
            pstmtUpdate.setInt(1, id_nauczyciela);
            pstmtUpdate.setInt(2, id_klasy);
            pstmtUpdate.setInt(3, id_przedmiotu);
            if (pstmtUpdate.executeUpdate()==0){
                try(PreparedStatement pstmtInsert = conn.prepareStatement(sqlInstert)){
                    pstmtUpdate.setInt(1, id_nauczyciela);
                    pstmtUpdate.setInt(2, id_klasy);
                    pstmtUpdate.setInt(3, id_przedmiotu);
                    pstmtInsert.executeUpdate();
                }
            }
            return true;
        }catch(Exception e){
            System.out.println("Błąd podczas aktualizowania przypisanego nauczyciela! "+ e.getMessage());
        }
        return false;
    }
    public static Nauczyciel getProfil(String email){
        String sqlGetProfil = "select imie, nazwisko, numer_telefonu from nauczyciel where email =?";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlGetProfil)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new Nauczyciel(
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("numer_telefonu")
                );
            }
        }catch (Exception e){
            System.out.println("Błąd podczas pobierania danych nauczyciela! "+ e.getMessage());
        }
        return null;
    }
    public static boolean zarejestrujNauczyciel(String imie, String nazwisko, String email, String tel, String haslo){
        String sqlDodajUcznia = "insert into nauczyciel (imie, nazwisko, email, numer_telefonu, haslo) values (?,?,?,?,?)";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlDodajUcznia)){

            String zahashowaneHaslo = org.mindrot.jbcrypt.BCrypt.hashpw(haslo, org.mindrot.jbcrypt.BCrypt.gensalt());
            pstmt.setString(1, imie);
            pstmt.setString(2, nazwisko);
            pstmt.setString(3, email);
            pstmt.setString(4, tel);
            pstmt.setString(5, zahashowaneHaslo);

            return pstmt.executeUpdate()> 0;

        }catch(Exception e){
            System.out.println("Błąd podczas dodawania ucznia: " + e.getMessage());
            return false;
        }
    }
    public static List<Przedmiot> wszystkiePrzedmioty(){
        List<Przedmiot> listaPrzedmiotow = new ArrayList<>();
        String sqlGetPrzedmioy = "select id_przedmiotu, nazwa from przedmiot";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlGetPrzedmioy)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                listaPrzedmiotow.add(new Przedmiot(
                        rs.getInt("id_przedmiotu"),
                        rs.getString("nazwa")
                ));
            }
        }catch (Exception e){
            System.out.println("Błąd podczas pobierania przedmiotow! "+ e.getMessage());
        }
        return null;
    }

    public static boolean nowaKlasa(String nazwaKlasy, String emailWychowawcy, List<Integer> idPrzedmiotow) {
        String sqlDodajKlase = "insert into klasa(nazwa, id_nauczyciela) values(?, (select id_nauczyciela from nauczyciel where email = ?))";
        String sqlDodajPrzedmioty = "insert into nauczyciel_przedmiot_klasa (id_klasy, id_przedmiotu) values (?, ?)";

        try (Connection conn = DriverManager.getConnection(url)){
            conn.setAutoCommit(false);
            int idKlasy = -1;
            try(PreparedStatement pstmtDodajKlase = conn.prepareStatement(sqlDodajKlase, Statement.RETURN_GENERATED_KEYS)){
                pstmtDodajKlase.setString(1, nazwaKlasy);
                pstmtDodajKlase.setString(2, emailWychowawcy);
                pstmtDodajKlase.executeUpdate();
                try (ResultSet kluczeGlowne = pstmtDodajKlase.getGeneratedKeys()) {
                    if (kluczeGlowne.next()) {
                        idKlasy = kluczeGlowne.getInt(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            try (PreparedStatement pstmtDodajPrzedmioty = conn.prepareStatement(sqlDodajPrzedmioty)) {
                for(Integer idPrzedmiotu : idPrzedmiotow){
                    pstmtDodajPrzedmioty.setInt(1, idKlasy);
                    pstmtDodajPrzedmioty.setInt(2, idPrzedmiotu);
                    pstmtDodajPrzedmioty.addBatch();
                }
                pstmtDodajPrzedmioty.executeBatch();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Błąd podczas dodawania nowej klasy z przedmiotami");
            return false;
        }
    }
}
