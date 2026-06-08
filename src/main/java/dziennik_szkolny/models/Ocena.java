package dziennik_szkolny.models;

public class Ocena {
    private int wartosc;
    private int waga;
    private String opis;
    private String dataWystawienia;
    private String nazwaPrzedmiotu;
    private String imieNauczyciela;
    private String nazwiskoNauczyciela;

    public Ocena(int wartosc,  int waga, String opis, String dataWystawienia, String nazwaPrzedmiotu, String imieNauczyciela, String nazwiskoNauczyciela) {
        this.wartosc = wartosc;
        this.waga = waga;
        this.opis = opis;
        this.dataWystawienia = dataWystawienia;
        this.nazwaPrzedmiotu = nazwaPrzedmiotu;
        this.imieNauczyciela = imieNauczyciela;
        this.nazwiskoNauczyciela = nazwiskoNauczyciela;
    }

    public int getWaga() {return waga;}
    public void setWaga(int waga) {this.waga = waga;}

    public String getOpis() {return opis;}
    public void setOpis(String opis) {this.opis = opis;}

    public String getDataWystawienia() {return dataWystawienia;}
    public void setDataWystawienia(String dataWystawienia) {this.dataWystawienia = dataWystawienia;}

    public String getNazwaPrzedmiotu() {return nazwaPrzedmiotu;}
    public void setNazwaPrzedmiotu(String nazwaPrzedmiotu) {this.nazwaPrzedmiotu = nazwaPrzedmiotu;}

    public String getImieNauczyciela() {return imieNauczyciela;}
    public void setImieNauczyciela(String imieNauczyciela) {this.imieNauczyciela = imieNauczyciela;}

    public String getNazwiskoNauczyciela() {return nazwiskoNauczyciela;}
    public void setNazwiskoNauczyciela(String nazwiskoNauczyciela) {this.nazwiskoNauczyciela = nazwiskoNauczyciela;}

    public int getWartosc() {return wartosc;}
    public void setWartosc(int wartosc) {this.wartosc = wartosc;}



}
