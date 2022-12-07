package app.cowordle.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UsernameSceneController {

    @FXML
    private TextField tf_username;
    @FXML
    private Button button_sendUsername;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void login(ActionEvent event) throws IOException { //TODO: rename login to something else

        String inputUsername = this.tf_username.getText();
        if(inputUsername == null || inputUsername.isEmpty())
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("board-view.fxml"));
        root = loader.load();

        ClientController scene2Controller = loader.getController();

        scene2Controller.initializeGameStage(inputUsername);

        //root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

