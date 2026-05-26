module com.example.dziennik_szkolny {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dziennik_szkolny to javafx.fxml;
    exports com.example.dziennik_szkolny;
}