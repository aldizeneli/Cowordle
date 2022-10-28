package app.briscola.client;


import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends javafx.application.Application {
    public ClientApplication() {
    }

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("board-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 478.0, 396.0);
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(new String[0]);


    }
}
