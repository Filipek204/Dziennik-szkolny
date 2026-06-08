package dziennik_szkolny.controllers;

import dziennik_szkolny.DAO.NauczycielDAO;
import dziennik_szkolny.DAO.UczenDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogowanieController {

    @FXML private VBox panelLogowaniaBox;
    @FXML private TextField poleEmail;
    @FXML private PasswordField poleHaslo;
    @FXML private Button zalogujbtn;
    @FXML private ComboBox<String> comboRola;
    @FXML public void initialize(){
        if(comboRola!= null){
            comboRola.getItems().addAll("Uczeń", "Nauczyciel");
            comboRola.setValue("Uczeń");
            comboRola.setOnAction(e->wybranaRola());
        }
        Platform.runLater(()->{
            if(panelLogowaniaBox!=null){
                panelLogowaniaBox.requestFocus();
            }
        });
    }
    @FXML private void panelRejestracji(){
        panelLogowaniaBox.setManaged(false);
        panelLogowaniaBox.setVisible(false);

        panelRejestracjiBox.setManaged(true);
        panelRejestracjiBox.setVisible(true);
    }
    @FXML private void panelLogowania(){
        panelRejestracjiBox.setManaged(false);
        panelRejestracjiBox.setVisible(false);

        panelLogowaniaBox.setManaged(true);
        panelLogowaniaBox.setVisible(true);
    }
    @FXML private void wybranaRola(){
        boolean czyUczen = "Uczeń".equals(comboRola.getValue());
        daneUcznia.setManaged(czyUczen);
        daneUcznia.setVisible(czyUczen);
    }
    @FXML  private void zaloguj() {
        String email = poleEmail.getText();
        String haslo = poleHaslo.getText();
        if (UczenDAO.logowanieUczen(email, haslo)) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/widoki/panelUcznia.fxml"));
                Parent root = fxmlLoader.load();

                PanelUczniaController uczenController = fxmlLoader.getController();
                uczenController.zaloguj(email);
                Stage stage = (Stage) zalogujbtn.getScene().getWindow();
                stage.getScene().setRoot(root);
                stage.setTitle("Dziennik Szkolny - Panel Ucznia");


            } catch (Exception e) {
                System.out.println("Ups! Błąd przy ładowaniu okna: " + e.getMessage());
                e.printStackTrace();
            }

        } else if (NauczycielDAO.logowanieNauczyciel(email, haslo)) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/widoki/panelNauczyciela.fxml"));
                Parent root = fxmlLoader.load();
                PanelNauczycielaController nauczycielController = fxmlLoader.getController();
                nauczycielController.zaloguj(email);
                Stage stage = (Stage) zalogujbtn.getScene().getWindow();
                stage.getScene().setRoot(root);

                stage.setTitle("Dziennik Szkolny - Panel Nauczyciela");

            } catch (Exception e) {
                System.out.println("Ups! Błąd przy ładowaniu okna: " + e.getMessage());
                e.printStackTrace();
            }

        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd logowania");
            alert.setContentText("Nie udało się zalogować");
            alert.showAndWait();
        }
    }

    @FXML private VBox panelRejestracjiBox;

    @FXML private TextField imie;
    @FXML private TextField nazwisko;
    @FXML private TextField email;
    @FXML private TextField telefon;
    @FXML private PasswordField haslo;

    @FXML private HBox daneUcznia;
    @FXML private TextField pesel;
    @FXML private TextField data;

    @FXML private void zarejestruj(){
        String rola = comboRola.getValue();
        String imieTekst = imie.getText();
        String nazwiskoTekst = nazwisko.getText();
        String emailTekst = email.getText();
        String hasloTekst = haslo.getText();
        String telefonTekst = telefon.getText();
        if(imieTekst.isEmpty()|| nazwiskoTekst.isEmpty()||emailTekst.isEmpty()||hasloTekst.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Braki w danych");
            alert.setHeaderText(null);
            alert.setContentText("Imię, Nazwisko, E-mail oraz Hasło są obowiązkowe!");
            alert.showAndWait();
        }
        boolean sukces;
        if("Uczeń".equals(rola)) {
            String peselTekst = pesel.getText();
            String dataTekst = data.getText();
            sukces = UczenDAO.zarejestrujUcznia(imieTekst, nazwiskoTekst, peselTekst, dataTekst, emailTekst, telefonTekst, hasloTekst);

        }else{
            sukces = NauczycielDAO.zarejestrujNauczyciel(imieTekst, nazwiskoTekst, emailTekst, telefonTekst, hasloTekst);
        }
        if(sukces){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukces");
            alert.setHeaderText("Rejestracja zakończona pomyślnie");
            alert.setContentText("Twoje konto zostało utworzone. Możesz się teraz zalogować");
            alert.showAndWait();

            imie.clear();
            nazwisko.clear();
            email.clear();
            haslo.clear();
            telefon.clear();
            pesel.clear();
            data.clear();
            panelLogowania();

            poleEmail.setText(emailTekst);

        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd rejestracji");
            alert.setContentText("Nie udało się utworzyć konta");
            alert.showAndWait();
        }
    }

}