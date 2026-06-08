package dziennik_szkolny.controllers;
import dziennik_szkolny.DAO.NauczycielDAO;
import dziennik_szkolny.DAO.OcenaDAO;
import dziennik_szkolny.DAO.UczenDAO;
import dziennik_szkolny.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;


public class PanelNauczycielaController {
    @FXML
    private Label powitanieLabel;
    @FXML
    private VBox kontenerGlowny;
    @FXML
    private Button wylogujbtn;
    @FXML
    private Button ocenyKlasy;
    @FXML
    private Button nowaKlasa;
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
            stage.getScene().setRoot(root);
        }
        catch(Exception e){
            System.out.println("Błąd podczas przełączania scen! " + e.getMessage());
        }

    }
    public void zaloguj(String email){
        this.zalogowanyEmail = email;
        this.klasaWychowawcza = NauczycielDAO.getKlasaWychowawcy(zalogowanyEmail);
        Nauczyciel profil = NauczycielDAO.getProfil(zalogowanyEmail);
        pokazListeKlas();
        if(klasaWychowawcza != null){
            powitanieLabel.setText("Witaj, "+ profil.getImie() + " (Wychowawca klasy "+klasaWychowawcza+")");
            pokazMenuWychowawcy(true);
        }else{
            powitanieLabel.setText("Witaj, "+ profil.getImie());
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

        nowaKlasa.setVisible(!czyPokazac);
        nowaKlasa.setVisible(!czyPokazac);
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
            ocenabtn.setOnAction(e-> edytujOcene(ocena.getIdOceny(), ocena.getWartosc(), ocena.getWaga(), ocena.getOpis(), uczen.getImieNazwisko(), klasa, przedmiot));

            kontenerOcen.getChildren().add(ocenabtn);
        }
        Region przerwa  = new Region();
        HBox.setHgrow(przerwa, Priority.ALWAYS);
        Button dodajbtn = new Button("+ Dodaj");
        dodajbtn.getStyleClass().add("przycisk-dodaj-ocene");

        dodajbtn.setOnAction(e-> dodajOcene(uczen.getIdUcznia(), uczen.getImieNazwisko(), klasa, przedmiot));
        wiersz.getChildren().addAll(danelbl, kontenerOcen, przerwa, dodajbtn);
        return wiersz;
    }

    private void dodajOcene(int idUcznia, String daneUcznia, String klasa, String przedmiot){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nowa ocena");
        dialog.setHeaderText("Wystawiasz ocenę z przedmiotu: "+przedmiot+"\n Dla ucznia: "+daneUcznia);
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

    private void edytujOcene(int idOceny, String obecnaWartosc, int obecnaWaga, String obecnyOpis, String daneUcznia, String klasa,String przedmiot){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edycja oceny");
        dialog.setHeaderText("Edytujesz ocenę ucznia: " +daneUcznia+" \nZ przedmiotu: "+przedmiot);
        try{
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/okienka.css").toExternalForm());
        }catch(Exception e){
            System.out.println("Nie znaleziono styli dla dialogu! "+e.getMessage());
        }
        dialog.getDialogPane().getStyleClass().add("nowoczesny-dialog");

        ButtonType zapiszbtn = new ButtonType("Zapisz zmiany", ButtonBar.ButtonData.OK_DONE);
        ButtonType usunbtn = new ButtonType("Usuń ocenę", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(zapiszbtn, usunbtn                           , ButtonType.CANCEL);

        GridPane siatka = new GridPane();
        siatka.setHgap(10);
        siatka.setVgap(15);

        siatka.setPadding(new Insets(20,20,10,10));

        ComboBox<Integer> comboWartosci = new ComboBox<>();
        comboWartosci.getItems().addAll(1,2,3,4,5,6);
        comboWartosci.setValue(Integer.parseInt(obecnaWartosc));
        comboWartosci.getStyleClass().add("okno-input");

        ComboBox<Integer> comboWagi = new ComboBox<>();
        comboWagi.getItems().addAll(1,2,3);
        comboWagi.setValue(obecnaWaga);
        comboWagi.getStyleClass().add("okno-input");

        TextField tekstFieldOpis = new TextField();
        if(obecnyOpis!=null){
            tekstFieldOpis.setText(obecnyOpis);
        }else{
            tekstFieldOpis.setPromptText("np. Sprawdzian, Odpowiedź...");
        }
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

        if(wynik.isPresent()){
            if( wynik.get() == zapiszbtn){
                int wartosc = comboWartosci.getValue();
                int waga = comboWagi.getValue();
                String opis = tekstFieldOpis.getText();
                boolean sukces  = NauczycielDAO.edytujOcene(idOceny, wartosc, waga, opis);
                if(sukces){
                    otworzDziennik(klasa, przedmiot);
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(" Błąd bazy danych");
                    alert.setHeaderText("Nie udało się dodać oceny");
                    alert.showAndWait();
                }
            }
            else if(wynik.get() == usunbtn){
                Alert ostrzezenie = new Alert(Alert.AlertType.CONFIRMATION);
                ostrzezenie.setTitle("Uwaga!");
                ostrzezenie.setHeaderText("Czy na pewno chcesz usunąć tę ocenę?");

                if(ostrzezenie.showAndWait().orElse(ButtonType.CANCEL) ==ButtonType.OK){
                    boolean sukces = NauczycielDAO.usunOcene(idOceny);
                    if (sukces){
                        otworzDziennik(klasa, przedmiot);
                    }
                }
            }

        }
    }

    public void pokazUczniowKlasy(){
        powitanieLabel.setText("Podgląd ocen klasy wychowawczej");
        kontenerGlowny.getChildren().clear();
        List<Uczen> listaUczniow = UczenDAO.getKlasaWychowawcza(zalogowanyEmail);
        if(listaUczniow.isEmpty()){
            Label brak = new Label("Brak przypisanych uczniów w tej klasie");
            brak.getStyleClass().add("brak-lbl");
            kontenerGlowny.getChildren().add(brak);
        }
    for(Uczen u: listaUczniow) {
        HBox karta = new HBox(15);
        karta.getStyleClass().addAll("profil-karta", "karta-klasy");
        karta.setAlignment(Pos.CENTER_LEFT);

        Label danelbl = new Label(u.getImie() + " " + u.getNazwisko());
        danelbl.getStyleClass().add("karta-klasy-tytul");

        Region przerwa = new Region();
        HBox.setHgrow(przerwa, Priority.ALWAYS);

        Button zobaczOcenybtn = new Button("Zobacz wszystkie oceny ➔");
        zobaczOcenybtn.getStyleClass().add("przycisk-przedmiot");
        zobaczOcenybtn.setOnAction(e->pokazOcenyUczniow(u.getIdUcznia(),u.getImie(), u.getNazwisko(),u.getEmail() ));
        karta.setOnMouseClicked(e->zobaczOcenybtn.fire());

        karta.getChildren().addAll(danelbl,przerwa, zobaczOcenybtn);
        VBox.setMargin(karta, new Insets(0,0,10,0));
        kontenerGlowny.getChildren().add(karta);
        }
    }
    private void pokazOcenyUczniow(int idUcznia, String imie, String nazwisko, String email){
        powitanieLabel.setText("Oceny ucznia: "+ imie+" "+nazwisko);
        kontenerGlowny.getChildren().clear();

        Button wrocbtn = new Button("⬅ Wróć do listy uczniów");
        wrocbtn.getStyleClass().add("przycisk-wstecz");
        wrocbtn.setOnAction(e->pokazUczniowKlasy());
        kontenerGlowny.getChildren().addAll(wrocbtn);
        List<Ocena> wszystkieOceny = OcenaDAO.getOceny(email);
        if(wszystkieOceny.isEmpty()){
            Label brakOcen = new Label("Brak ocen do wyświetlenia.");
            brakOcen.getStyleClass().add("brak-lbl");
            kontenerGlowny.getChildren().add(brakOcen);
        }
        Map<String, List<Ocena>> przedmioty = new HashMap<>();
        for(Ocena o: wszystkieOceny){
            przedmioty.putIfAbsent(o.getNazwaPrzedmiotu(), new ArrayList<>());
            przedmioty.get(o.getNazwaPrzedmiotu()).add(o);
        }
        for(Map.Entry<String, List<Ocena>> wpis : przedmioty.entrySet()){
            String nazwaPrzedmiotu = wpis.getKey();
            List<Ocena> ocenyPrzedmiotu = wpis.getValue();

            kontenerGlowny.getChildren().add(wpisPrzedmiotu(nazwaPrzedmiotu, ocenyPrzedmiotu));
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
    public void pokazUczniow(){
        powitanieLabel.setText("Moja klasa");
        kontenerGlowny.getChildren().clear();

        List<Uczen> listaUczniow = UczenDAO.getKlasaWychowawcza(zalogowanyEmail);

        HBox panelGorny = new HBox();
        panelGorny.setAlignment(Pos.CENTER_RIGHT);
        panelGorny.setPadding(new Insets(0,0,20,0));

        Button dodajUczniabtn = new Button("+ Dodaj nowego ucznia");
        dodajUczniabtn.getStyleClass().add("przycisk-dodaj-ocene");
        dodajUczniabtn.setOnAction(e-> przypiszUcznia());

        panelGorny.getChildren().add(dodajUczniabtn);
        kontenerGlowny.getChildren().add(panelGorny);
        if(listaUczniow.isEmpty()){
            Label brak = new Label("Brak przypisanych uczniów w tej klasie");
            brak.getStyleClass().add("brak-lbl");
            kontenerGlowny.getChildren().add(brak);
        }
        for(Uczen u : listaUczniow){
            HBox karta = new HBox(10);
            karta.getStyleClass().add("wiersz-ucznia");

            HBox naglowek = new HBox();
            naglowek.setAlignment(Pos.CENTER_LEFT);

            Label danelbl = new Label(u.getImie()+" "+u.getNazwisko());
            danelbl.getStyleClass().add("wiersz-dane");
            danelbl.setPrefWidth(220);
            danelbl.setMinWidth(220);
            danelbl.setMaxWidth(220);
            Region przerwa = new Region();
            HBox.setHgrow(przerwa, Priority.ALWAYS);




            GridPane detale = new GridPane();
            detale.setHgap(40);
            detale.setVgap(8);

            Label emaillbl = new Label("Email: "+u.getEmail());
            emaillbl.getStyleClass().add("styl tekstu");

            Label telefonlbl = new Label("Telefon: "+u.getNumer_telefonu());
            telefonlbl.getStyleClass().add("styl tekstu");
            Label pesellbl = new Label("PESEL: "+u.getPesel());
            pesellbl.getStyleClass().add("styl tekstu");

            Label datalbl = new Label("Data ur.: "+u.getData_urodzenia());
            datalbl.getStyleClass().add("styl tekstu");
            detale.add(emaillbl, 0, 0);
            detale.add(telefonlbl, 0, 1);
            detale.add(pesellbl, 1, 0);
            detale.add(datalbl, 1, 1);


            naglowek.getChildren().addAll(danelbl, przerwa);

            VBox sekcjaAkcji = new VBox(10);
            sekcjaAkcji.setAlignment(Pos.CENTER_RIGHT);
            Button edytujbtn = new Button("Edytuj dane");
            edytujbtn.getStyleClass().add("edytuj-dane-przycisk");
            edytujbtn.setOnAction(e->edytujUcznia(u));

            Button usunbtn = new Button("Usuń ucznia");
            usunbtn.getStyleClass().add("usun-ucznia-przycisk");
            usunbtn.setOnAction(e->{
                Alert ostrzezenie = new Alert(Alert.AlertType.CONFIRMATION);
                ostrzezenie.setTitle("Uwaga!");
                ostrzezenie.setHeaderText("Czy na pewno chcesz usunąć tego ucznia z klasy?");

                if(ostrzezenie.showAndWait().orElse(ButtonType.CANCEL) ==ButtonType.OK){
                    boolean sukces = NauczycielDAO.usunUcznia(u.getIdUcznia());
                    if (sukces){
                        pokazUczniow();
                    }
                }
            });
            sekcjaAkcji.getChildren().addAll(edytujbtn, usunbtn);


            Region przerwa2 = new Region();
            HBox.setHgrow(przerwa2, Priority.ALWAYS);


            karta.getChildren().addAll(naglowek, detale,przerwa2, sekcjaAkcji);
            kontenerGlowny.getChildren().add(karta);
        }
    }
    private void przypiszUcznia(){
        int idKlasy = NauczycielDAO.getIdKlasyWychowawcy(zalogowanyEmail);
        List<UczenDziennik> wolniUczniowie = NauczycielDAO.getUczniowieBezKlasy();

        if(wolniUczniowie.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Brak wolnych uczniów w bazie!");
            alert.setHeaderText("Wszyscy zarejestrowani uczniowie mają już przypisaną klasę");
            alert.showAndWait();
        }
        ChoiceDialog<UczenDziennik> dialog = new ChoiceDialog<>(wolniUczniowie.get(0), wolniUczniowie);
        dialog.setTitle("Dodaj ucznia do klasy");
        dialog.setHeaderText("Wybierz ucznia z listy nieprzypisanych osób");
        dialog.setContentText("Uczeń");
        try{
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/okienka.css").toExternalForm());
        }catch(Exception e){
            System.out.println("Nie znaleziono styli dla dialogu! "+e.getMessage());
        }
        dialog.getDialogPane().getStyleClass().add("nowoczesny-dialog");

        dialog.showAndWait().ifPresent(wybrany->{
            if(NauczycielDAO.przypiszUcznia(wybrany.getIdUcznia(), idKlasy)){
                pokazUczniow();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Błąd!");
                alert.setHeaderText("Nie udało się przypisać ucznia do klasy");
                alert.showAndWait();
            }
        });
    }
    private void edytujUcznia(Uczen uczen){
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Rejestracja nowego ucznia");
        dialog.setHeaderText("Wprowadź dane nowego ucznia");
        try{
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/okienka.css").toExternalForm());
        }catch(Exception e){
            System.out.println("Nie znaleziono styli dla dialogu! "+e.getMessage());
        }
        dialog.getDialogPane().getStyleClass().add("nowoczesny-dialog");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);

        GridPane siatka = new GridPane();
        siatka.setHgap(10);
        siatka.setVgap(15);
        siatka.setPadding(new Insets(20, 20, 10, 10));

        TextField imieTekstField = new TextField();
        TextField nazwiskoTekstField = new TextField();
        TextField peselTekstField = new TextField();
        TextField dataTekstField = new TextField();
        TextField emailTekstField = new TextField();
        TextField telefonTekstField = new TextField();


        dialog.setTitle("Edycja danych ucznia");
        dialog.setHeaderText("Edytujesz ucznia: "+ uczen.getImie()+" "+uczen.getNazwisko());
        imieTekstField.setText(uczen.getImie());
        nazwiskoTekstField.setText(uczen.getNazwisko());
        peselTekstField.setText(uczen.getPesel());
        dataTekstField.setText(uczen.getData_urodzenia());
        emailTekstField.setText(uczen.getEmail());
        telefonTekstField.setText(uczen.getNumer_telefonu());

        Arrays.asList(imieTekstField,nazwiskoTekstField,peselTekstField, dataTekstField,  emailTekstField, telefonTekstField).forEach(tf->tf.getStyleClass().add("okno-input"));


        siatka.add(new Label("Imie: "), 0,0 );
        siatka.add(imieTekstField, 1, 0);
        siatka.add(new Label("Nazwisko: "), 0,1 );
        siatka.add(nazwiskoTekstField, 1, 1);
        siatka.add(new Label("PESEL: "), 0,2 );
        siatka.add(peselTekstField, 1, 2);
        siatka.add(new Label("Data urodz.: "), 0,3 );
        siatka.add(dataTekstField, 1, 3);
        siatka.add(new Label("E-mail: "), 0,4 );
        siatka.add(emailTekstField, 1, 4);
        siatka.add(new Label("Telefon: "), 0,5 );
        siatka.add(telefonTekstField, 1, 5);

        dialog.getDialogPane().setContent(siatka);
        dialog.showAndWait().ifPresent(wynik -> {
                    if (wynik == ButtonType.OK) {

                            boolean sukces = NauczycielDAO.edytujUcznia(uczen.getIdUcznia(), imieTekstField.getText(), nazwiskoTekstField.getText(), peselTekstField.getText(), dataTekstField.getText(), emailTekstField.getText(), telefonTekstField.getText());

                        if (sukces) {
                            pokazUczniow();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Błąd");
                            alert.setHeaderText("Operacja zakończona niepowodzeniem");
                            alert.setContentText("Sprawdź poprawność wprowadzonych danych");
                            alert.showAndWait();
                        }
                    }
        });
    }
    public void przypisNauczycieli(){
        powitanieLabel.setText("nauczyciele przypisani do mojej klasy");
        kontenerGlowny.getChildren().clear();
        int idKlasy = NauczycielDAO.getIdKlasyWychowawcy(zalogowanyEmail);
        if(idKlasy == 0){
            Label brak = new Label("Nie masz wychowawstwa w żadnej klasie");
            brak.getStyleClass().add("brak-lbl");
            kontenerGlowny.getChildren().add(brak);
        }
        List<Przedmiot> przedmioty = NauczycielDAO.getPrzedmiotyKlasy(idKlasy);
        if(przedmioty.isEmpty()){
            Label brak = new Label("Brak przedmiotów w systemie, skontaktuj sie z administratorem");
            brak.getStyleClass().add("brak-lbl");
            kontenerGlowny.getChildren().add(brak);
        }
        for(Przedmiot p: przedmioty){
            HBox karta = new HBox(20);
            karta.getStyleClass().add("wiersz-ucznia");
            karta.setAlignment(Pos.CENTER_LEFT);
            Label przedmiotlbl = new Label(p.getNazwaPrzedmiotu());
            przedmiotlbl.getStyleClass().add("wiersz-dane");
            przedmiotlbl.setPrefWidth(200);

            boolean czyBrakNauczyciela = p.getId_nauczyciela() == 0;
            Label nauczyciellbl = new Label(p.getImieNauczyciela()+" "+ p.getNazwiskoNauczyciela());
            nauczyciellbl.setStyle(czyBrakNauczyciela? "-fx-text-fill: #EF4444; -fx-font-style: italic;" : "-fx-text-fill: #6C757D; -fx-font-size: 15px;");

            Region przerwa = new Region();
            HBox.setHgrow(przerwa, Priority.ALWAYS);
            Button przypiszbtn = new Button(czyBrakNauczyciela ? "Przypisz nauczyciela": "Zmień nauczyciela");
            przypiszbtn.getStyleClass().add("przycisk-przedmiot");

            przypiszbtn.setOnAction(e->oknoWyboruNauczyciela(idKlasy, p.getId_przedmiotu(), p.getNazwaPrzedmiotu()));
            karta.getChildren().addAll(przedmiotlbl, nauczyciellbl, przerwa, przypiszbtn);
            kontenerGlowny.getChildren().add(karta);


        }
    }
    private void  oknoWyboruNauczyciela(int idKlasy, int id_przedmiotu, String nazwaPrzedmiotu){
        List nauczyciele = NauczycielDAO.getWszystcyNauczyciele();
        ChoiceDialog dialog = new ChoiceDialog<>();
        dialog.getItems().addAll(nauczyciele);
        if(!nauczyciele.isEmpty()){
            dialog.setSelectedItem(nauczyciele.get(0));
        }
        dialog.setTitle("Przypisz nauczyciela");
        dialog.setHeaderText("Wybierz osobę, która będzie uczyć przedmiotu "+ nazwaPrzedmiotu);
        dialog.setContentText("Nauczyciel:");
        try{
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/okienka.css").toExternalForm());
        }catch(Exception e){
            System.out.println("Nie znaleziono styli dla dialogu! "+e.getMessage());
        }
        dialog.getDialogPane().getStyleClass().add("nowoczesny-dialog");

        Optional<NauczycielSzkola> wynik  = dialog.showAndWait();
        wynik.ifPresent(wybranyNauczyciel ->{
            boolean sukces = NauczycielDAO.przypiszNauczyciela(idKlasy, id_przedmiotu, wybranyNauczyciel.getIdNauczyciela());
            if(sukces){
                przypisNauczycieli();
            } else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Nie udało się przypisać nauczyciela");
                alert.showAndWait();
            }
        });
    }
    public void pokazMojProfil(){
        powitanieLabel.setText("Mój Profil");
        kontenerGlowny.getChildren().clear();

        Nauczyciel profil = NauczycielDAO.getProfil(zalogowanyEmail);
        VBox kartaProfilu = new VBox(15);
        kartaProfilu.getStyleClass().add("wiersz-ucznia");
        kartaProfilu.setPadding(new Insets(30));
        kartaProfilu.setAlignment(Pos.CENTER_LEFT);

        Label imieNazwiskolbl = new Label(profil.getImie()+" "+profil.getNazwisko());
        imieNazwiskolbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2B3452;");
        Region linia = new Region();
        linia.setMinHeight(1);
        linia.setPrefHeight(1);
        linia.setMaxHeight(1);
        linia.setStyle("-fx-background-color: #E2E8F0;");
        VBox.setMargin(linia, new Insets(10, 0, 10, 0));
        GridPane detale = new GridPane();
        detale.setHgap(20);
        detale.setVgap(15);

        Label emaillblNapiss = new Label("Adres e-mail");
        emaillblNapiss.getStyleClass().add("profil-napis");
        Label emaillblWartosc = new Label(zalogowanyEmail);
        emaillblWartosc.getStyleClass().add("profil-wartosc");

        Label telefonlblNapis = new Label("Numer telefonu");
        telefonlblNapis.getStyleClass().add("profil-napis");
        Label telefonlblWartosc = new Label(profil.getNumer_telefonu());
        telefonlblWartosc.getStyleClass().add("profil-wartosc");


        detale.add(emaillblNapiss, 0,0);
        detale.add(emaillblWartosc, 1,0);
        detale.add(telefonlblNapis, 0,1);
        detale.add(telefonlblWartosc, 1,1);
        kartaProfilu.getChildren().addAll(imieNazwiskolbl, linia, detale);
        kontenerGlowny.getChildren().add(kartaProfilu);
    }
    public void DodajKlaseOkno(){
        Dialog<ButtonType> dialog = new Dialog();
        dialog.setTitle("Nowa klasa");
        dialog.setHeaderText("Zostań wychowawcą nowej klasy i wybierz jej przedmioty");
        try{
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/okienka.css").toExternalForm());
        }catch(Exception e){
            System.out.println("Nie znaleziono styli dla dialogu! "+e.getMessage());
        }
        dialog.getDialogPane().getStyleClass().add("nowoczesny-dialog");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox kontener = new VBox(15);
        kontener.setPadding(new Insets(20));
        TextField nazwaTextField = new TextField();
        nazwaTextField.setPromptText("Podaj nazwę klasy (mp. 1A, 2C");
        nazwaTextField.getStyleClass().add("okno-input");

        Label przedmiotylbl = new Label("Wybierz przedmioty nauczane w tej klasie");
        przedmiotylbl.setStyle("-fx-font-weight: bold;");

        List<Przedmiot> dostepnePrzedmioty = NauczycielDAO.wszystkiePrzedmioty();
        VBox listaPrzedmiotowBox = new VBox(8);
        listaPrzedmiotowBox.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 5;");

        Map<CheckBox, Integer> mapaCheckBoxow = new HashMap<>();
        for(Przedmiot p : dostepnePrzedmioty){
            CheckBox cb = new CheckBox(p.getNazwaPrzedmiotu());
            cb.setStyle("-fx-cursor: hand;");
            listaPrzedmiotowBox.getChildren().add(cb);
            mapaCheckBoxow.put(cb, p.getId_przedmiotu());
        }
        ScrollPane scroll = new ScrollPane(listaPrzedmiotowBox);
        scroll.setPrefHeight(200);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        kontener.getChildren().addAll(new Label("Nazwa klasy:"), nazwaTextField, przedmiotylbl, scroll);
        dialog.getDialogPane().setContent(kontener);



//        dialog.showAndWait().ifPresent(nazwa ->{
//            if(nazwa.trim().isEmpty()){
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Błąd");
//                alert.setHeaderText("Nazwa klasy nie może być pusta!");
//                alert.showAndWait();
//                return;
//            }
//            boolean sukces = NauczycielDAO.nowaKlasa(nazwa, zalogowanyEmail);
//            if(sukces){
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Sukces");
//                alert.setHeaderText("Klasa "+nazwa+ "została utworzona!");
//                alert.setContentText("Jesteś teraz jej wychowawcą. możesz zacząć dodawać uczniów");
//                alert.showAndWait();
//                pokazMenuWychowawcy(true);
//                pokazUczniow();
//            }else{
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Błąd");
//                alert.setHeaderText("Nie udało się utworzyć klasy");
//                alert.showAndWait();
//
//            }
//        });
    }
}
