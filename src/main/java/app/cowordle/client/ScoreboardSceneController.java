package app.cowordle.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ScoreboardSceneController {

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


    private Stage stage;
    private Scene scene;
    private Parent root;

    public void initializeScoreboardStage(String player1username, int player1score, String player2username, int player2score) {
        lbl_player1username.setText(player1username);
        lbl_player1score.setText(String.valueOf(player1score));

        lbl_player2username.setText(player2username);
        lbl_player2score.setText(String.valueOf(player2score));
    }
}

