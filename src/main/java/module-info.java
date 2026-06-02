module com.example.dziennik_szkolny {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;


    opens dziennik_szkolny to javafx.fxml;
    exports dziennik_szkolny;
    opens dziennik_szkolny.controllers to javafx.fxml;
    exports dziennik_szkolny.controllers;
}