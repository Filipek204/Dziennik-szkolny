package dziennik_szkolny.controllers;

import dziennik_szkolny.DAO.OcenaDAO;
import dziennik_szkolny.DAO.PrzedmiotDAO;
import dziennik_szkolny.models.Ocena;
import dziennik_szkolny.models.Przedmiot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
    private void wyloguj(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/widoki/panelLogowania.fxml"));
            Stage stage = (Stage) wylogujbtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
        catch(Exception e){
            System.out.println("Błąd podczas przełączania scen! " + e.getMessage());
        }

    }
    public void zaloguj(String email){
        this.zalogowanyEmail = email;
        pokazOceny();
    }

    @FXML
    public void pokazOceny(){
        powitanieLabel.setText("Witaj "+zalogowanyEmail+"!");

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


}
