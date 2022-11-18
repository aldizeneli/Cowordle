module app.briscola {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    //opens app.briscola to javafx.fxml;

    exports app.cowordle.server;
    exports app.cowordle.client;
    exports app.cowordle.shared;
    opens app.cowordle.server to javafx.fxml;
    opens app.cowordle.client to javafx.fxml;
}