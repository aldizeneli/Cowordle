package app.cowordle.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ScoreboardSceneController implements Initializable {

    //region Properties

    @FXML
    private Label lbl_player1username;
    @FXML
    private Label lbl_player1score;
    @FXML
    private Label lbl_player2username;
    @FXML
    private Label lbl_player2score;
    @FXML
    private Button btn_replay;
    @FXML
    private Button btn_close;
    private String username;

    //endregion

    //region Public Methods

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.btn_close.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void goToGameScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/board-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            GameSceneController gameSceneController = loader.getController();
            gameSceneController.initializeGameStage(this.username);

            stage.show();
        } catch (IOException e) {
            //TODO: cant close socket etc.. because here not present, call Platform.exit(); System.exit(0);
        }
    }

    public void initializeScoreboardStage(String player1username, int player1score, String player2username, int player2score, String currentClientUsername) {
        this.username = currentClientUsername;

        lbl_player1username.setText(player1username);
        lbl_player1score.setText(String.valueOf(player1score));

        lbl_player2username.setText(player2username);
        lbl_player2score.setText(String.valueOf(player2score));
    }

    //endregion
}

