package dziennik_szkolny.models;

public class Uczen {
    private String imie;
    private String nazwisko;
    private String pesel;
    private String data_urodzenia;
    private String email;
    private String numer_telefonu;
    private String klasa;

    public Uczen( String imie, String nazwisko, String pesel, String data_urodzenia, String email, String numer_telefonu, String klasa) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = pesel;
        this.data_urodzenia = data_urodzenia;
        this.email = email;
        this.numer_telefonu = numer_telefonu;
        this.klasa = klasa;
    }


    public String getImie() {return imie;}
    public void setImie(String imie) {this.imie = imie;}

    public String getNazwisko() {return nazwisko;}
    public void setNazwisko(String nazwisko) {this.nazwisko = nazwisko;}

    public String getPesel() {return pesel;}
    public void setPesel(String pesel) {this.pesel = pesel;}

    public String getData_urodzenia() {return data_urodzenia;}
    public void setData_urodzenia(String data_urodzenia) {this.data_urodzenia = data_urodzenia;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getNumer_telefonu() {return numer_telefonu;}
    public void setNumer_telefonu(String numer_telefonu) {this.numer_telefonu = numer_telefonu;}

    public String getKlasa() {return klasa;}
    public void setKlasa(String klasa) {this.klasa = klasa;}
}
