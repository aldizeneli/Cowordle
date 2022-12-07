package app.cowordle.client;


import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientApplication extends javafx.application.Application {

    static final double STAGE_HEIGHT=735.0;
    static final double STAGE_WIDTH=575.0;
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

//        stage.initStyle(StageStyle.TRANSPARENT);
//        scene.setFill(Color.TRANSPARENT);

        stage.show();
    }

    public static void main(String[] args) {
        launch(new String[0]);
    }
}
