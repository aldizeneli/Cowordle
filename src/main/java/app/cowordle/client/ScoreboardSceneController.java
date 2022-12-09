package app.cowordle.client;

import app.cowordle.shared.ActionType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ScoreboardSceneController implements Initializable {

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

    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent t) {
//                Platform.exit();
//                System.exit(0);
//            }
//        });

        this.btn_close.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void goToGameScene(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("board-view.fxml"));
            root = loader.load();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);

            ClientController gameSceneController = loader.getController();
            gameSceneController.initializeGameStage("aldi"); //TODO: FARSI ARRIVARE QUI L USERNAME CORRENTE E USARE QUELLO

//            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent t) {
//                    Platform.exit();
//                    System.exit(0);
//                }
//            });
            stage.show();
        } catch (IOException e) {
            //TODO: cant close socket etc.. because here not present, call Platform.exit(); System.exit(0);
        }
    }

    public void initializeScoreboardStage(String player1username, int player1score, String player2username, int player2score) {
        lbl_player1username.setText(player1username);
        lbl_player1score.setText(String.valueOf(player1score));

        lbl_player2username.setText(player2username);
        lbl_player2score.setText(String.valueOf(player2score));
    }
}

