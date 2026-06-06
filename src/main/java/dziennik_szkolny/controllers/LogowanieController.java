package dziennik_szkolny.controllers;

import dziennik_szkolny.DAO.NauczycielDAO;
import dziennik_szkolny.DAO.UczenDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LogowanieController {

    @FXML
    private TextField poleEmail;

    @FXML
    private PasswordField poleHaslo;

    @FXML
    private Button zalogujbtn;
    @FXML
    private void zaloguj() {
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
            System.out.println("BŁĄD! Niepoprawny email lub hasło.");
        }
    }

}