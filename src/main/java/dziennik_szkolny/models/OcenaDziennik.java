package dziennik_szkolny.models;

public class OcenaDziennik {
    private int idOceny;
    private String wartosc;
    private int waga;
    private String opis;

    public OcenaDziennik(int idOceny, String wartosc, int waga, String opis) {
        this.idOceny = idOceny;
        this.wartosc = wartosc;
        this.waga = waga;
        this.opis = opis;
    }

    public int getIdOceny() {return idOceny;}
    public void setIdOceny(int idOceny) {this.idOceny = idOceny;}

    public String getWartosc() {return wartosc;}
    public void setWartosc(String wartosc) {this.wartosc = wartosc;}

    public int getWaga() {return waga;}
    public void setWaga(int waga) {this.waga = waga;}

    public String getOpis() {return opis;}
    public void setOpis(String opis) {this.opis = opis;}
}
