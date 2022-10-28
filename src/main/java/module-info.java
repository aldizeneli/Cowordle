module app.briscola {
    requires javafx.controls;
    requires javafx.fxml;


    //opens app.briscola to javafx.fxml;

    exports app.briscola.server;
    exports app.briscola.client;
    opens app.briscola.server to javafx.fxml;
    opens app.briscola.client to javafx.fxml;
}