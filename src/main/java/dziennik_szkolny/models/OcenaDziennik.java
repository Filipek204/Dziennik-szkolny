package dziennik_szkolny.models;

public class OcenaDziennik {
    private int idOceny;
    private String wartosc;

    public OcenaDziennik(int idOceny, String wartosc) {
        this.idOceny = idOceny;
        this.wartosc = wartosc;
    }

    public int getIdOceny() {return idOceny;}
    public void setIdOceny(int idOceny) {this.idOceny = idOceny;}

    public String getWartosc() {return wartosc;}
    public void setWartosc(String wartosc) {this.wartosc = wartosc;}
}
