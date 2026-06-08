package dziennik_szkolny.controllers;

import dziennik_szkolny.DAO.NauczycielDAO;
import dziennik_szkolny.DAO.OcenaDAO;
import dziennik_szkolny.DAO.PrzedmiotDAO;
import dziennik_szkolny.DAO.UczenDAO;
import dziennik_szkolny.models.Nauczyciel;
import dziennik_szkolny.models.Ocena;
import dziennik_szkolny.models.Przedmiot;
import dziennik_szkolny.models.Uczen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PanelUczniaController {
    private String zalogowanyEmail;
    @FXML
    private Label powitanieLabel;
    @FXML
    private VBox kontenerOcen;
    @FXML
    private Button wylogujbtn;
    @FXML
    private Button oceny;
    @FXML
    private Button przedmioty;
    @FXML
    private Button nauczyciele;
    @FXML
    private Button mojeDane;
    @FXML
    private void wyloguj(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/widoki/panelLogowania.fxml"));
            Stage stage = (Stage) wylogujbtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        }
        catch(Exception e){
            System.out.println("Błąd podczas przełączania scen! " + e.getMessage());
        }

    }
    public void zaloguj(String email){
        this.zalogowanyEmail = email;
        pokazOceny();
        Uczen uczen = UczenDAO.daneUcznia(zalogowanyEmail);
        powitanieLabel.setText("Witaj "+uczen.getImie()+"!");
    }

    @FXML
    public void pokazOceny(){

        powitanieLabel.setText("Moje oceny");
        oceny.getStyleClass().add("active-nav");
        przedmioty.getStyleClass().removeAll("active-nav");
        nauczyciele.getStyleClass().removeAll("active-nav");
        mojeDane.getStyleClass().removeAll("active-nav");

        kontenerOcen.getChildren().clear();

        List<Ocena> wszystkieOceny = OcenaDAO.getOceny(zalogowanyEmail);
        if(wszystkieOceny.isEmpty()){
            Label brakOcen = new Label("Brak ocen do wyświetlenia.");
            brakOcen.getStyleClass().add("brak-lbl");
            kontenerOcen.getChildren().add(brakOcen);
        }
        Map<String, List<Ocena>> przedmioty = new HashMap<>();
        for(Ocena o: wszystkieOceny){
            przedmioty.putIfAbsent(o.getNazwaPrzedmiotu(), new ArrayList<>());
            przedmioty.get(o.getNazwaPrzedmiotu()).add(o);
        }
        for(Map.Entry<String, List<Ocena>> wpis : przedmioty.entrySet()){
            String nazwaPrzedmiotu = wpis.getKey();
            List<Ocena> ocenyPrzedmiotu = wpis.getValue();

            kontenerOcen.getChildren().add(wpisPrzedmiotu(nazwaPrzedmiotu, ocenyPrzedmiotu));
        }
    }
    private VBox wpisPrzedmiotu(String nazwaPrzedmiotu, List<Ocena> oceny){
        double sumaWazona = 0;
        int sumaWag = 0;
        double sredniaOcen = 0;
        StringBuilder listaOcen = new StringBuilder();
        for(Ocena o : oceny){
            sumaWazona += o.getWartosc() *o.getWaga();
            sumaWag += o.getWaga();
            listaOcen.append(o.getWartosc()).append(", ");
        }
        if(sumaWag != 0) {
            sredniaOcen = sumaWazona / sumaWag;
        }
        String sredniaToTekst = String.format("%.2f", sredniaOcen);
        VBox kartaOceny = new VBox();
        kartaOceny.getStyleClass().add("karta-oceny");

        HBox naglowek = new HBox();
        naglowek.setAlignment(Pos.CENTER_LEFT);
        naglowek.setSpacing(20);
        naglowek.getStyleClass().add("ocena-naglowek");

        Label przedmiotlbl = new Label(nazwaPrzedmiotu);
        przedmiotlbl.getStyleClass().add("przedmiot-lbl");

        Label ocenylbl = new Label("Oceny: "+ listaOcen);
        ocenylbl.getStyleClass().add("oceny-lbl");

        Region odstep = new Region();
        HBox.setHgrow(odstep, Priority.ALWAYS);

        Label srednialbl = new Label("Średnia: "+sredniaToTekst);
        srednialbl.getStyleClass().add("srednia-lbl");
        naglowek.getChildren().addAll(przedmiotlbl, ocenylbl, odstep, srednialbl);

        VBox szczegoly = new VBox(8);
        szczegoly.setVisible(false);
        szczegoly.setManaged(false);
        szczegoly.getStyleClass().add("szczegoly");
        for(Ocena o : oceny){
            String tekstSzczegoly = String.format("Ocena: %d (waga: %d) | Dnia: %s | Nauczyciel: %s %s\nOpis: %s",
                    o.getWartosc(), o.getWaga(), o.getDataWystawienia(), o.getImieNauczyciela() , o.getNazwiskoNauczyciela(), o.getOpis());
            Label detalelbl = new Label(tekstSzczegoly);
            detalelbl.getStyleClass().add("detale-lbl");
            szczegoly.getChildren().add(detalelbl);
        }
        naglowek.setOnMouseClicked(event -> {
            boolean rozwiniete = szczegoly.isVisible();
            szczegoly.setVisible(!rozwiniete);
            szczegoly.setManaged(!rozwiniete);
        });
        kartaOceny.getChildren().addAll(naglowek,szczegoly);
        VBox.setMargin(szczegoly, new Insets(10,0,0,0));

        return kartaOceny;
    }

    public void pokazPrzedmioty(){
        oceny.getStyleClass().removeAll("active-nav");
        przedmioty.getStyleClass().add("active-nav");
        nauczyciele.getStyleClass().removeAll("active-nav");
        mojeDane.getStyleClass().removeAll("active-nav");
        powitanieLabel.setText("Twoje przedmioty");
        kontenerOcen.getChildren().clear();
        List<Przedmiot> przedmioty = PrzedmiotDAO.getPrzedmioty(zalogowanyEmail);
        if(przedmioty.isEmpty()){
            Label brakPrzedmiotow = new Label("Brak przypisanych przedmiotów");
            brakPrzedmiotow.getStyleClass().add("brak-lbl");
            kontenerOcen.getChildren().add(brakPrzedmiotow);
        }
        for(Przedmiot p : przedmioty){
            HBox karta = new HBox();
            karta.setAlignment(Pos.CENTER_LEFT);
            karta.getStyleClass().add("karta-oceny");

            Label przedmiotlbl = new Label(p.getNazwaPrzedmiotu());
            przedmiotlbl.getStyleClass().add("przedmiot-lbl");

            Region odstep = new Region();
            HBox.setHgrow(odstep, Priority.ALWAYS);

            Label nauczyciellbl = new Label("Prowadzący: "+ p.getImieNauczyciela()+" "+ p.getNazwiskoNauczyciela());
            nauczyciellbl.getStyleClass().add("brak-lbl");
            karta.getChildren().addAll(przedmiotlbl,odstep, nauczyciellbl);
            kontenerOcen.getChildren().add(karta);

        }

    }

    public void pokazNauczycieli(){
        powitanieLabel.setText("Twoi nauczyciele");
        kontenerOcen.getChildren().clear();

        oceny.getStyleClass().removeAll("active-nav");
        przedmioty.getStyleClass().removeAll("active-nav");
        nauczyciele.getStyleClass().add("active-nav");
        mojeDane.getStyleClass().removeAll("active-nav");
        Nauczyciel wychowawca = NauczycielDAO.getWychowawca(zalogowanyEmail);

        if(wychowawca != null){
            kontenerOcen.getChildren().add(wpisNauczyciela(wychowawca, true));
        }
        List<Nauczyciel> nauczyciele = NauczycielDAO.getNauczyciele(zalogowanyEmail);
        for(Nauczyciel n : nauczyciele){
            if(wychowawca != null && n.getEmail().equals(wychowawca.getEmail()))continue;
            kontenerOcen.getChildren().add(wpisNauczyciela(n, false));
        }
    }
    public VBox wpisNauczyciela(Nauczyciel nauczyciel, Boolean czyWychowawca){
        VBox karta = new VBox(5);
        karta.getStyleClass().add("karta-oceny");

        if (czyWychowawca){
            karta.getStyleClass().add("wychowawca");
        }
        Label nauczyciellbl = new Label(nauczyciel.getImie()+" "+ nauczyciel.getNazwisko());
        nauczyciellbl.getStyleClass().add("header-text");
        nauczyciellbl.setStyle("-fx-font-size: 16px;");

        Label przedmiotlbl = new Label(nauczyciel.getPrzedmiot());
        nauczyciellbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #6C757D; -fx-font-weight: bold;");

        Label kontaktlbl = new Label("Email: "+ nauczyciel.getEmail()+" | Tel: "+nauczyciel.getNumer_telefonu());
        nauczyciellbl.getStyleClass().add("oceny-lbl");
        karta.getChildren().addAll(nauczyciellbl, przedmiotlbl, kontaktlbl);
        return karta;
    }

    public void pokazMojeDane(){
        powitanieLabel.setText("Mój profil");
        kontenerOcen.getChildren().clear();

        oceny.getStyleClass().removeAll("active-nav");
        przedmioty.getStyleClass().removeAll("active-nav");
        nauczyciele.getStyleClass().removeAll("active-nav");
        mojeDane.getStyleClass().add("active-nav");

        Uczen profil = UczenDAO.daneUcznia(zalogowanyEmail);

        if(profil == null){
            Label blad = new Label("Nie udało się załadować profilu");
            blad.getStyleClass().add("brak-lbl");
            kontenerOcen.getChildren().add(blad);
        }
        VBox kartaProfilu = new VBox(30);
        kartaProfilu.getStyleClass().add("profil-karta");
        kartaProfilu.setAlignment(Pos.TOP_LEFT);

        VBox naglowek = new VBox(10);
        naglowek.setAlignment(Pos.TOP_LEFT);

        Label uczenlbl = new Label(profil.getImie()+" "+profil.getNazwisko());
        uczenlbl.getStyleClass().add("profil-imie");

        Label klasalbl = new Label("Uczeń klasy: "+profil.getKlasa());
        klasalbl.getStyleClass().add("profil-klasa");

        naglowek.getChildren().addAll(uczenlbl,klasalbl);

        Region linia = new Region();
        linia.setMinHeight(1);
        linia.setPrefHeight(1);
        linia.setMaxHeight(1);
        linia.setStyle("-fx-background-color: #E2E8F0;");
        VBox.setMargin(linia, new Insets(10, 0, 10, 0));

        HBox szczegoly = new HBox();
        szczegoly.setAlignment(Pos.CENTER_LEFT);

        VBox kolumnaLewa = new VBox(15);
        kolumnaLewa.getChildren().addAll(
                wierszDanych("PESEL:", profil.getPesel()),
                wierszDanych("Data Urodzenia:", profil.getData_urodzenia()));

        VBox kolumnaPrawa = new VBox(15);
        kolumnaPrawa.getChildren().addAll(
                wierszDanych("Email:", profil.getEmail()),
                wierszDanych("Telefon:", profil.getNumer_telefonu()));

        Region odstep = new Region();
        HBox.setHgrow(odstep, Priority.ALWAYS);
        odstep.setMinWidth(30);
        szczegoly.getChildren().addAll(kolumnaLewa,odstep,kolumnaPrawa);

        kartaProfilu.getChildren().addAll(naglowek,linia,szczegoly);

        VBox wysrodkuj = new VBox(kartaProfilu);
        wysrodkuj.setAlignment(Pos.TOP_LEFT);

        VBox.setMargin(kartaProfilu , new Insets(30,0,0,0));
        kontenerOcen.getChildren().add(wysrodkuj);
    }
    private HBox wierszDanych(String napis, String wartosc){
        Label napislbl = new Label(napis);
        napislbl.getStyleClass().add("profil-napis");

        Label wartosclbl = new Label(wartosc);
        wartosclbl.getStyleClass().add("profil-wartosc");

        return new HBox(10, napislbl, wartosclbl);
    }


}
