package dziennik_szkolny.controllers;

import dziennik_szkolny.DAO.NauczycielDAO;
import dziennik_szkolny.DAO.UczenDAO;
import dziennik_szkolny.models.OcenaDziennik;
import dziennik_szkolny.models.Przedmiot_Klasa;
import dziennik_szkolny.models.UczenDziennik;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.function.LongToDoubleFunction;


public class PanelNauczycielaController {
    @FXML
    private Label powitanieLabel;
    @FXML
    private VBox kontenerGlowny;
    @FXML
    private Button wylogujbtn;
    @FXML
    private Button Mojeprzedmioty;
    @FXML
    private Button ocenyKlasy;
    @FXML
    private Button uczniowieKlasy;
    @FXML
    private Button NauczycieleKlasy;

    private String zalogowanyEmail;
    private String klasaWychowawcza;
    public void wyloguj(){
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
        this.klasaWychowawcza = NauczycielDAO.getKlasaWychowawcy(zalogowanyEmail);

        if(klasaWychowawcza != null){
            powitanieLabel.setText("Witaj, "+ email + " (Wychowawca klasy "+klasaWychowawcza+")");
            pokazMenuWychowawcy(true);
        }else{
            powitanieLabel.setText("Witaj, "+ email);
            pokazMenuWychowawcy(false);
        }
    }
    private void pokazMenuWychowawcy(Boolean czyPokazac){
        ocenyKlasy.setVisible(czyPokazac);
        ocenyKlasy.setManaged(czyPokazac);

        uczniowieKlasy.setVisible(czyPokazac);
        uczniowieKlasy.setManaged(czyPokazac);

        NauczycieleKlasy.setVisible(czyPokazac);
        NauczycieleKlasy.setManaged(czyPokazac);
    }

    @FXML
    public void pokazListeKlas(){
        powitanieLabel.setText("Moje klasy i przedmioty");
        kontenerGlowny.getChildren().clear();

        List<Przedmiot_Klasa>  przydzialy  = NauczycielDAO.getPrzydzialy(zalogowanyEmail);
        if(przydzialy.isEmpty()){
            Label brak = new Label("Brak przypisanych klas w systemie");
            brak.getStyleClass().add("brak-lbl");
            kontenerGlowny.getChildren().add(brak);
        }
        for(Przedmiot_Klasa p : przydzialy){
            VBox karta = new VBox(15);
            karta.getStyleClass().add("profil-karta, karta-klasy");

            Label klasalbl = new Label("Klasa "+ p.getNazwaKlasy());
            klasalbl.getStyleClass().add("karta-klasy-tytul");

            HBox kontenerPrzedmiotow = new HBox(10);
            kontenerPrzedmiotow.setVisible(false);
            kontenerPrzedmiotow.setManaged(false);

            for (String przedmiot : p.getPrzedmioty()){
                Button przedmiotbtn = new Button(przedmiot);
                przedmiotbtn.getStyleClass().add("przycisk-przedmiot");

                przedmiotbtn.setOnAction(e ->otworzDziennik(p.getNazwaKlasy(), przedmiot));
                kontenerPrzedmiotow.getChildren().add(przedmiotbtn);
            }
            karta.setOnMouseClicked(e ->{
                boolean ukryte = !kontenerPrzedmiotow.isVisible();
                kontenerPrzedmiotow.setVisible(ukryte);
                kontenerPrzedmiotow.setManaged(ukryte);
            });
            karta.getChildren().addAll(klasalbl , kontenerPrzedmiotow);
            VBox.setMargin(karta, new Insets(0,0,15, 0));
            kontenerGlowny.getChildren().add(karta);
        }

    }
    private void otworzDziennik(String klasa, String przedmiot){
        powitanieLabel.setText("Dziennik: Klasa "+klasa+" | "+ przedmiot);
        kontenerGlowny.getChildren().clear();
        Button wrocbtn = new Button("⬅ Wróć do listy klas");
        wrocbtn.getStyleClass().add("przycisk-wstecz");
        wrocbtn.setOnAction(e->pokazListeKlas());
        kontenerGlowny.getChildren().addAll(wrocbtn);

        List<UczenDziennik> uczniowie = NauczycielDAO.getDziennikOcen(klasa, przedmiot);
        if(uczniowie.isEmpty()){
            Label brak = new Label("Brak przypisanych uczniów w tej klasie");
            brak.getStyleClass().add("brak-lbl");
            kontenerGlowny.getChildren().add(brak);
        }

        for (UczenDziennik uczen : uczniowie) {
            kontenerGlowny.getChildren().add(nowyWierszUcznia(uczen, klasa, przedmiot));
        }
    }
    private HBox nowyWierszUcznia(UczenDziennik uczen, String klasa, String przedmiot){
        HBox wiersz = new HBox(15);
        wiersz.getStyleClass().add("wiersz-ucznia");
        wiersz.setAlignment(Pos.CENTER_LEFT);

        Label danelbl = new Label(uczen.getImieNazwisko());
        danelbl.getStyleClass().add("wiersz-dane");
        danelbl.setPrefWidth(200);

        HBox kontenerOcen = new HBox(5);
        kontenerOcen.setAlignment(Pos.CENTER_LEFT);

        for(OcenaDziennik ocena : uczen.getOceny()){
            Button ocenabtn = new Button(ocena.getWartosc());
            ocenabtn.getStyleClass().add("kafelek-oceny");
            ocenabtn.setOnAction(e-> System.out.println("kliknieto ocene o id: " +ocena.getIdOceny()));

            kontenerOcen.getChildren().add(ocenabtn);
        }
        Region przerwa  = new Region();
        HBox.setHgrow(przerwa, Priority.ALWAYS);
        Button dodajbtn = new Button("+ Dodaj");
        dodajbtn.getStyleClass().add("przycisk-dodaj-ocene");

        dodajbtn.setOnAction(e-> pokazOknoDodawaniaOceny(uczen.getIdUcznia(), uczen.getImieNazwisko(), klasa, przedmiot));
        wiersz.getChildren().addAll(danelbl, kontenerOcen, przerwa, dodajbtn);
        return wiersz;
    }

    private void pokazOknoDodawaniaOceny(int idUcznia, String daneUcznia, String klasa, String przedmiot){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nowa ocena");
        dialog.setHeaderText("Wystawiasz ocene z przedmiotu: "+przedmiot+"\n Dla ucznia: "+daneUcznia);
        try{
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/okienka.css").toExternalForm());
        }catch(Exception e){
            System.out.println("Nie znaleziono styli dla dialogu! "+e.getMessage());
        }
        dialog.getDialogPane().getStyleClass().add("nowoczesny-dialog");



        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane siatka = new GridPane();
        siatka.setHgap(10);
        siatka.setVgap(10);

        siatka.setPadding(new Insets(20,20,10,10));

        ComboBox<Integer> comboWartosci = new ComboBox<>();
        comboWartosci.getItems().addAll(1,2,3,4,5,6);
        comboWartosci.getStyleClass().add("okno-input");

        ComboBox<Integer> comboWagi = new ComboBox<>();
        comboWagi.getItems().addAll(1,2,3);
        comboWagi.getStyleClass().add("okno-input");

        TextField tekstFieldOpis = new TextField();
        tekstFieldOpis.setPromptText("np. Sprawdzian, Odpowiedź...");
        tekstFieldOpis.setMinWidth(200);
        tekstFieldOpis.getStyleClass().add("okno-input");

        siatka.add(new Label("Ocena: "), 0, 0);
        siatka.add(comboWartosci, 1,0);

        siatka.add(new Label("Waga: "), 0, 1);
        siatka.add(comboWagi, 1,1);

        siatka.add(new Label("Opis (opcjonalny): "), 0, 2);
        siatka.add(tekstFieldOpis, 1,2);

        dialog.getDialogPane().setContent(siatka);

        Optional<ButtonType> wynik  = dialog.showAndWait();

        if(wynik.isPresent() && wynik.get() == ButtonType.OK){
            int wartosc = comboWartosci.getValue();
            int waga = comboWagi.getValue();
            String opis = tekstFieldOpis.getText();
            boolean sukces  = NauczycielDAO.dodajOcene(idUcznia, przedmiot, zalogowanyEmail, wartosc, waga, opis);
            if(sukces){
                otworzDziennik(klasa, przedmiot);
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(" Błąd bazy danych");
                alert.setHeaderText("Nie udało się dodać oceny");
                alert.showAndWait();
            }
        }
    }
}
