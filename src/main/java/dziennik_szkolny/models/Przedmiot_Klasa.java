package dziennik_szkolny.models;

import java.util.List;

public class Przedmiot_Klasa {
    private String nazwaKlasy;
    private List<String> przedmioty;

    public Przedmiot_Klasa(String nazwaKlasy, List<String> przedmioty) {
        this.nazwaKlasy = nazwaKlasy;
        this.przedmioty = przedmioty;
    }

    public String getNazwaKlasy() {return nazwaKlasy;}
    public void setNazwaKlasy(String nazwaKlasy) {this.nazwaKlasy = nazwaKlasy;}

    public List<String> getPrzedmioty() {return przedmioty;}
    public void setPrzedmioty(List<String> przedmioty) {this.przedmioty = przedmioty;}
}
