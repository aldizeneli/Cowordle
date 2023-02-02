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
    exports app.cowordle.client.controllers;
    opens app.cowordle.client.controllers to javafx.fxml;
    exports app.cowordle.server.handlers;
    opens app.cowordle.server.handlers to javafx.fxml;
    exports app.cowordle.server.utility;
    opens app.cowordle.server.utility to javafx.fxml;
    exports app.cowordle.client.handlers;
    opens app.cowordle.client.handlers to javafx.fxml;
    exports app.cowordle.client.models;
    opens app.cowordle.client.models to javafx.fxml;
}