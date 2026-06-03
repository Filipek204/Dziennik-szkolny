package dziennik_szkolny.models;

public class Przedmiot {
    private String nazwaPrzedmiotu;
    private String imieNauczyciela;
    private String nazwiskoNauczyciela;

    public Przedmiot(String nazwaPrzedmiotu, String imieNauczyciela, String nazwiskoNauczyciela) {
        this.nazwaPrzedmiotu = nazwaPrzedmiotu;
        this.imieNauczyciela = imieNauczyciela;
        this.nazwiskoNauczyciela = nazwiskoNauczyciela;
    }

    public String getNazwaPrzedmiotu() {return nazwaPrzedmiotu;}
    public void setNazwaPrzedmiotu(String nazwaPrzedmiotu) {this.nazwaPrzedmiotu = nazwaPrzedmiotu;}

    public String getImieNauczyciela() {return imieNauczyciela;}
    public void setImieNauczyciela(String imieNauczyciela) {this.imieNauczyciela = imieNauczyciela;}

    public String getNazwiskoNauczyciela() {return nazwiskoNauczyciela;}
    public void setNazwiskoNauczyciela(String nazwiskoNauczyciela) {this.nazwiskoNauczyciela = nazwiskoNauczyciela;}
}
