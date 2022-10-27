module app.briscola {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.briscola to javafx.fxml;
    exports app.briscola;
}