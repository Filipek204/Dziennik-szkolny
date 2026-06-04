package dziennik_szkolny.models;

import java.util.List;

public class UczenDziennik {
    private int idUcznia;
    private String imieNazwisko;
    private List<OcenaDziennik> oceny;

    public UczenDziennik(int idUcznia, String imieNazwisko, List<OcenaDziennik> oceny) {
        this.idUcznia = idUcznia;
        this.imieNazwisko = imieNazwisko;
        this.oceny = oceny;
    }

    public int getIdUcznia() {return idUcznia;}
    public void setIdUcznia(int idUcznia) {this.idUcznia = idUcznia;}

    public String getImieNazwisko() {return imieNazwisko;}
    public void setImieNazwisko(String imieNazwisko) {this.imieNazwisko = imieNazwisko;}

    public List<OcenaDziennik> getOceny() {return oceny;}
    public void setOceny(List<OcenaDziennik> oceny) {this.oceny = oceny;}
}
