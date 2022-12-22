package app.cowordle.client;


import java.io.IOException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ClientApplication extends javafx.application.Application {

    static final double STAGE_HEIGHT=739.0;
    static final double STAGE_WIDTH=581.0;
    public ClientApplication() { }

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("username-view.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), STAGE_HEIGHT, STAGE_WIDTH);
        stage.setTitle("Cowordle!");
        stage.setScene(scene);
        stage.getIcons().add(new Image(("file:app-logo.png"))); //TODO: make it work

        stage.setMaxHeight(STAGE_HEIGHT);
        stage.setMaxWidth(STAGE_WIDTH);
        stage.setMinHeight(STAGE_HEIGHT);
        stage.setMinWidth(STAGE_WIDTH);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(new String[0]);
    }
}
