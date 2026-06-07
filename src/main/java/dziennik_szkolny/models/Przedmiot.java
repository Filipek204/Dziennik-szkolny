package dziennik_szkolny.models;

public class Przedmiot {
    private String nazwaPrzedmiotu;
    private String imieNauczyciela;
    private String nazwiskoNauczyciela;
    private int id_przedmiotu;
    private int id_nauczyciela;

    public Przedmiot(String nazwaPrzedmiotu, String imieNauczyciela, String nazwiskoNauczyciela) {
        this.nazwaPrzedmiotu = nazwaPrzedmiotu;
        this.imieNauczyciela = imieNauczyciela;
        this.nazwiskoNauczyciela = nazwiskoNauczyciela;
    }

    public Przedmiot(String nazwaPrzedmiotu, String imieNauczyciela, String nazwiskoNauczyciela, int id_przedmiotu, int id_nauczyciela) {
        this.nazwaPrzedmiotu = nazwaPrzedmiotu;
        this.imieNauczyciela = imieNauczyciela;
        this.nazwiskoNauczyciela = nazwiskoNauczyciela;
        this.id_przedmiotu = id_przedmiotu;
        this.id_nauczyciela = id_nauczyciela;
    }

    public Przedmiot(int id_przedmiotu, String nazwaPrzedmiotu) {
        this.id_przedmiotu = id_przedmiotu;
        this.nazwaPrzedmiotu = nazwaPrzedmiotu;
    }

    public String getNazwaPrzedmiotu() {return nazwaPrzedmiotu;}

    public String getImieNauczyciela() {return imieNauczyciela;}

    public String getNazwiskoNauczyciela() {return nazwiskoNauczyciela;}

    public int getId_przedmiotu() {return id_przedmiotu;}

    public int getId_nauczyciela() {return id_nauczyciela;}
}
