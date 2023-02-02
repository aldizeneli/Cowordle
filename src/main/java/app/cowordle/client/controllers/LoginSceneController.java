package app.cowordle.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.util.regex.Pattern;

public class LoginSceneController {

    //region Properties

    @FXML
    private TextField tf_username;
    @FXML
    private Label lbl_usernameValidation;

    //endregion

    //region Public Methods

    public void goToGameScene(ActionEvent event) {
        try {
            lbl_usernameValidation.setText("Choose your username:");

            String inputUsername = this.tf_username.getText();

            boolean isEmptyUsername = inputUsername == null || inputUsername.isEmpty();
            boolean isInvalidUsername = !Pattern.compile("[a-zA-Z0-9]+").matcher(inputUsername).matches();

            if(isEmptyUsername) {
                lbl_usernameValidation.setText("The username can't be empty!");
                return;
            } else if (isInvalidUsername) {
                lbl_usernameValidation.setText("The username can't contain special characters!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/board-view.fxml"));
            Parent root = loader.load();

            GameSceneController gameSceneController = loader.getController();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion
}

