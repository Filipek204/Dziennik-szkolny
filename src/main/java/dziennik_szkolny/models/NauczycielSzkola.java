package dziennik_szkolny.models;

import java.util.StringTokenizer;

public class NauczycielSzkola {
    private int idNauczyciela;
    private String imie;
    private String nazwisko;

    public NauczycielSzkola(int idNauczyciela, String imie, String nazwisko) {
        this.idNauczyciela = idNauczyciela;
        this.imie = imie;
        this.nazwisko = nazwisko;
    }

    public int getIdNauczyciela() {return idNauczyciela;}
    public String getImie() {return imie;}
    public String getNazwisko() {return nazwisko;}

    @Override
    public String toString() {
        return getImie()+" "+getNazwisko();
    }
}
