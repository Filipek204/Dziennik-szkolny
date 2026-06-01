package models;

public class Uczen {
    private int id_ucznia;
    private String imie;
    private String nazwisko;
    private int id_klasy;
    private String pesel;
    private String data_urodzenia;
    private String email;
    private String haslo;
    private String numer_telefonu;

    public Uczen(int id_ucznia, String imie, String nazwisko, int id_klasy, String pesel, String data_urodzenia, String email, String haslo, String numer_telefonu) {
        this.id_ucznia = id_ucznia;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.id_klasy = id_klasy;
        this.pesel = pesel;
        this.data_urodzenia = data_urodzenia;
        this.email = email;
        this.haslo = haslo;
        this.numer_telefonu = numer_telefonu;
    }

    public int getId_ucznia() {return id_ucznia;}
    public void setId_ucznia(int id_ucznia){this.id_ucznia = id_ucznia;}

    public String getImie() {return imie;}
    public void setImie(String imie) {this.imie = imie;}

    public String getNazwisko() {return nazwisko;}
    public void setNazwisko(String nazwisko) {this.nazwisko = nazwisko;}

    public int getId_klasy() {return id_klasy;}
    public void setId_klasy(int id_klasy) {this.id_klasy = id_klasy;}

    public String getPesel() {return pesel;}
    public void setPesel(String pesel) {this.pesel = pesel;}

    public String getData_urodzenia() {return data_urodzenia;}
    public void setData_urodzenia(String data_urodzenia) {this.data_urodzenia = data_urodzenia;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getHaslo() {return haslo;}
    public void setHaslo(String haslo) {this.haslo = haslo;}

    public String getNumer_telefonu() {return numer_telefonu;}
    public void setNumer_telefonu(String numer_telefonu) {this.numer_telefonu = numer_telefonu;}

    @Override
    public String toString() {
        return "Uczen{" +
                "id_ucznia=" + id_ucznia +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", id_klasy=" + id_klasy +
                ", pesel='" + pesel + '\'' +
                ", data_urodzenia='" + data_urodzenia + '\'' +
                ", email='" + email + '\'' +
                ", haslo='" + haslo + '\'' +
                ", numer_telefonu='" + numer_telefonu + '\'' +
                '}';
    }
}
