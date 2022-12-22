package app.cowordle.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.util.regex.Pattern;

public class UsernameSceneController {
    @FXML
    private TextField tf_username;

    public void goToGameScene(ActionEvent event) throws IOException {

        String inputUsername = this.tf_username.getText();
        if(inputUsername == null || inputUsername.isEmpty() || !Pattern.compile("[A-Za-z]+").matcher(inputUsername).matches())
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("board-view.fxml"));
        Parent root = loader.load();

        ClientController gameSceneController = loader.getController();

        gameSceneController.initializeGameStage(inputUsername);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        stage.show();
    }
}

