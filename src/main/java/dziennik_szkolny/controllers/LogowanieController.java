package dziennik_szkolny.controllers;

import dziennik_szkolny.DAO.NauczycielDAO;
import dziennik_szkolny.DAO.UczenDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

                javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/widoki/panelUcznia.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();

                PanelUczniaController uczenController = fxmlLoader.getController();
                uczenController.zaloguj(email);
                javafx.stage.Stage stage = (javafx.stage.Stage) zalogujbtn.getScene().getWindow();

                stage.setScene(new javafx.scene.Scene(root));

            } catch (Exception e) {
                System.out.println("Ups! Błąd przy ładowaniu okna: " + e.getMessage());
                e.printStackTrace();
            }

        } else if (NauczycielDAO.logowanieNauczyciel(email, haslo)) {
            try {

                javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/widoki/panelNauczyciela.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                PanelNauczycielaController nauczycielController = fxmlLoader.getController();
                nauczycielController.zaloguj(email);
                javafx.stage.Stage stage = (javafx.stage.Stage) zalogujbtn.getScene().getWindow();

                stage.setScene(new javafx.scene.Scene(root));
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