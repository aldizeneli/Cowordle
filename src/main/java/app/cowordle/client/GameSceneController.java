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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameSceneController implements Initializable {

    //region Constants
    private static final int WORD_LENGTH = 5;

    //endregion

    //region Properties
    private  CommunicationHandler communicationHandler;
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
    @FXML
    private ProgressIndicator prg_myTurn;
    @FXML
    private Label lbl_myTurn;

    //endregion

    //region Constructors

    public GameSceneController() {  }

    //endregion

    //region Public Methods

    public void initializeGameStage(String username) {
        try {
            Socket socket = new Socket("localhost", 1234);
            this.communicationHandler = new CommunicationHandler(socket, username, this);

        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_send.setDisable(true);
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

                if (inputText.length() == WORD_LENGTH) {
                    boolean isValidPattern = Pattern.compile("[A-Za-z]{5}").matcher(inputText).matches();
                    if(isValidPattern) {
                        communicationHandler.sendMessageToServer(inputText, ActionType.WORDGUESS);
                        tf_message.clear();
                    }
                    else
                        showInputValidationLabel("Please insert only a-z characters");
                } else {
                    showInputValidationLabel("Please insert a word with 5 letters");
                }
            }
        });
    }

    public void manageMyTurnIndicators(boolean isMyTurn) {
        Platform.runLater(new Runnable() {
            public void run()
            {
                prg_myTurn.setVisible(false);
                if(isMyTurn) {
                    lbl_myTurn.setText("It's your turn!");
                    lbl_myTurn.setTextFill(Color.color(0.3, 0.7, 0.1));
                    lbl_myTurn.setLayoutX(450.0);
                }
                else {
                    lbl_myTurn.setText("It's your opponents turn!");
                    lbl_myTurn.setTextFill(Color.color(0.9, 0.3, 0.3));
                    lbl_myTurn.setLayoutX(390.0);
                }

                button_send.setDisable(!isMyTurn);
            }
        });
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
                GameSceneController.this.vbox_messages.getChildren().add(hBox);
            }
        });
    }

    public void showPopUp(String dialogText, boolean goToScoreStage, String scores) {
        Platform.runLater(new Runnable() {
            public void run()
            {
                showDialogScene(dialogText, goToScoreStage, scores);
            }
        });
    }

    //endregion

    //region Private Methods

    private void showInputValidationLabel(String text) {
        Platform.runLater(new Runnable() {
            public void run()
            {
                GameSceneController.this.lbl_inputValidation.setText(text);
                GameSceneController.this.lbl_inputValidation.setVisible(true);
                GameSceneController.this.tf_message.clear();
            }
        });
    }

    private void loadScoreboardScene(String player1username, int player1score, String player2username, int player2score, String currentClientUsername)  {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scoreboard-view.fxml"));
            Parent root = loader.load();

            ScoreboardSceneController scoreboardController = loader.getController();
            scoreboardController.initializeScoreboardStage(player1username, player1score, player2username, player2score, currentClientUsername);

            Stage stage = (Stage) this.ap_main.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextFlow getLetterTileTextFlow(char answerChar, char resultChar) {
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

    private String getRgbColorStringFromChar(char answerChar) {
        String rgbValue = "";
        switch (answerChar) {
           case 'g':
               rgbValue = "25, 179, 33";
               break;
           case 'y':
               rgbValue = "255, 155, 16";
               break;
           case 'r':
               rgbValue = "224, 18, 66";
               break;
        }
        return "rgb(" + rgbValue + ")";
    }

    private void showDialogScene(String dialogText, boolean goToScoreStage, String scores) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Info");

        Button okButton = new Button("Ok");
        okButton.setMaxWidth(40.0);
        okButton.setMinWidth(40.0);

        registerDialogButtonActions(goToScoreStage, dialogStage, okButton, scores);

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

    private void registerDialogButtonActions(boolean goToScoreStage, Stage dialogStage, Button okButton, String scores) {
        if(goToScoreStage) {
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    dialogStage.close();
                    goToScoreStage(scores);
                }
            });

            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    dialogStage.close();
                    goToScoreStage(scores);
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
        this.vbox_messages.getChildren().clear();
        this.manageMyTurnIndicators(this.communicationHandler.getClientIsMyTurn());
    }

    private void goToScoreStage(String scores) {
        String[] scoreArray = scores.split(";");
        loadScoreboardScene(scoreArray[0], Integer.valueOf(scoreArray[1]), scoreArray[2], Integer.valueOf(scoreArray[3]), communicationHandler.getClientUsername());
    }

    //endregion
}
