package dziennik_szkolny.models;

public class Nauczyciel {
    private String imie;
    private String nazwisko;
    private String numer_telefonu;
    private String email;
    private String przedmiot;

    public Nauczyciel(String imie, String nazwisko, String numer_telefonu, String email, String przedmiot) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.numer_telefonu = numer_telefonu;
        this.email = email;
        this.przedmiot = przedmiot;
    }

    public String getImie() {return imie;}
    public void setImie(String imie) {this.imie = imie;}

    public String getNazwisko() {return nazwisko;}
    public void setNazwisko(String nazwisko) {this.nazwisko = nazwisko;}

    public String getNumer_telefonu() {return numer_telefonu;}
    public void setNumer_telefonu(String numer_telefonu) {this.numer_telefonu = numer_telefonu;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPrzedmiot() {return przedmiot;}
    public void setPrzedmiot(String przedmiot) {this.przedmiot = przedmiot;}
}
