package app.cowordle.client;


import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import app.cowordle.shared.ActionType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientController implements Initializable {
    @FXML
    private AnchorPane ap_main;
    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private Label lbl_inputValidation;
    private Client client;

    //TODO: REMOVE
    private Stage stage;
    private Scene scene;
    private Parent root;


    public ClientController() { //TODO: rename GameSceneController
    }


    public void initializeGameStage(String username) {
        try {
            Socket socket = new Socket("localhost", 1234);
            this.client = new Client(socket, username, this);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                sp_main.setVvalue((Double)newVal);
            }
        });

        this.button_send.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                lbl_inputValidation.setVisible(false);
                String inputText = tf_message.getText();
                if (inputText.length() == 5) {
                    boolean isValidPattern = Pattern.compile("[A-Za-z]{5}").matcher(inputText).matches();
                    if(isValidPattern) {
                        client.sendMessageToServer(inputText, ActionType.WORDGUESS);
                        tf_message.clear();
                    } else {
                        tf_message.clear();
                        showInputValidationLabel("Please insert only a-z characters");
                    }
                } else {
                    showInputValidationLabel("Please insert a word with 5 letters");
                }
            }
        });
    }

    public void showInputValidationLabel(String text) { //TODO: SEEMS IS NOT WORKING, DOESNT UPDATE UI
        Platform.runLater(new Runnable() {
            public void run()
            {
                ClientController.this.lbl_inputValidation.setText(text);
                ClientController.this.lbl_inputValidation.setVisible(true);
                ClientController.this.tf_message.clear();
            }
        });
    }

    public void loadScoreboardScene(String player1username, int player1score, String player2username, int player2score, String username)  {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scoreboard-view.fxml"));
            root = loader.load();

            ScoreboardSceneController scoreboardController = loader.getController();
            scoreboardController.initializeScoreboardStage(player1username, player1score, player2username, player2score, username);

            //root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
            stage = (Stage) this.ap_main.getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWordGuess(String inputWord, String resultFromServer) {
        Platform.runLater(new Runnable() {
            public void run()
            {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setPadding(new Insets(10, 0, 0, 2));
                hBox.setSpacing(8);

                char[] inputWordArray = inputWord.toCharArray();
                char[] resultFromServerArray = resultFromServer.toCharArray();
                for (int i = 0; i < inputWordArray.length; i++) {
                    char inputChar = inputWordArray[i];
                    char resultChar = resultFromServerArray[i];

                    hBox.getChildren().add(getLetterTileTextFlow(inputChar, resultChar));
                }
                ClientController.this.vbox_messages.getChildren().add(hBox);
            }
        });
    }

    public TextFlow getLetterTileTextFlow(char answerChar, char resultChar) {
        Text text = new Text(String.valueOf(answerChar).toUpperCase());
        TextFlow textFlow = new TextFlow(new Node[]{text});
        textFlow.setStyle("-fx-color: rgb(239, 242, 255); -fx-background-color: " + getRgbColorStringFromChar(resultChar) +"; -fx-background-radius: 12px;");
        //textFlow.setPadding(new Insets(10.0, 5.0, 10.0, 5.0));
        text.setFill(Color.color(0.934, 0.945, 0.996));
        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 85));

        textFlow.setMinHeight(115.0);
        textFlow.setMinWidth(93.0);
        textFlow.setMaxHeight(115.0);
        textFlow.setMaxWidth(93.0);

        textFlow.setTextAlignment(TextAlignment.CENTER);
        return textFlow;
    }

    public String getRgbColorStringFromChar(char answerChar) {
        String rgbValue = "";
        switch (answerChar) {
           case 'g':
               rgbValue = "25, 179, 33";
               break;
           case 'y':
               rgbValue = "235, 206, 16";
               break;
           case 'r':
               rgbValue = "224, 18, 66";
               break;
        }
        return "rgb(" + rgbValue + ")";
    }

    public void showPopUp(String dialogText, boolean goToScoreStage) {
        Platform.runLater(new Runnable() {
            //TODO: now both players see the message even if only the one that didnt guess the word should, make it generic
            public void run()
            {
                showDialogScene(dialogText, goToScoreStage);
            }
        });
    }

    private void showDialogScene(String dialogText, boolean goToScoreStage) {
        //TODO: try to initialize popup only once during startup and here just do myDialog.show();
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Info");

        Button okButton = new Button("Ok");
        okButton.setMaxWidth(40.0);
        okButton.setMinWidth(40.0);

        registerDialogButtonActions(goToScoreStage, dialogStage, okButton);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20.0, 20.0, 20.0, 20.0));
        vbox.setSpacing(25);
        vbox.getChildren().add(new Text(dialogText));
        vbox.getChildren().add(okButton);

        Scene myDialogScene = new Scene(vbox);
        dialogStage.setScene(myDialogScene);
        dialogStage.show();
        button_send.setDisable(true);
    }

    private void registerDialogButtonActions(boolean goToScoreStage, Stage dialogStage, Button okButton) {
        if(goToScoreStage) {
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    dialogStage.close();
                    goToScoreStage(client.getUsername());
                }
            });

            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    dialogStage.close();
                    goToScoreStage(client.getUsername());
                }
            });
        }
        else {
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    dialogStage.close();
                    clearBoard();
                }
            });

            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    dialogStage.close();
                    clearBoard();
                }
            });
        }
    }

    private void clearBoard() {
        ClientController.this.vbox_messages.getChildren().clear();
        button_send.setDisable(false);
    }

    private void goToScoreStage(String username) {
        loadScoreboardScene("player 1", 5, "Player 2", 4, username);
    }
}
