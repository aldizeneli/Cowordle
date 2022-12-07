package app.cowordle.client;


import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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
//    @FXML
//    private ImageView imageView_logo;
    private Client client;

    private Stage stage;
    private Scene scene;
    private Parent root;


    public ClientController() {
    }


    public void initializeGameStage(String username) {

        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Enter your username for the game: ");
//            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234);
            this.client = new Client(socket, username, this);

            //avvio i thread (sarebbero operazioni bloccanti altrimenti)
            client.listenForMessage();
            client.sendMessage();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                ClientController.this.sp_main.setVvalue((Double)newVal);
            }
        });

        this.button_send.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                lbl_inputValidation.setVisible(false);
                String inputText = tf_message.getText();
                if (!inputText.isEmpty() && inputText.length() == 5) {
                    boolean isValidPattern = Pattern.compile("[A-Za-z]{5}").matcher(inputText).matches();
                    if(isValidPattern) {
                        client.sendMessageToServer(inputText);
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

    public void loadScoreboardScene(String player1username, int player1score, String player2username, int player2score)  {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scoreboard-view.fxml"));
            root = loader.load();

            ScoreboardSceneController scoreboardController = loader.getController();
            scoreboardController.initializeScoreboardStage(player1username, player1score, player2username, player2score);

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
        System.out.println(textFlow.getTextAlignment().name());
        return textFlow;
    }

    public String getRgbColorStringFromChar(char answerChar) {
        String rgbValue = "";
        System.out.println(answerChar);
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
        System.out.println(rgbValue);
        return "rgb(" + rgbValue + ")";
    }

    public void initializeNewTurn(String wordGuessed) {
        Platform.runLater(new Runnable() {
            public void run()
            {
                showInfoPopup(wordGuessed);
            }
        });
    }

    private void showInfoPopup(String wordGuessed) {
        //TODO: try to initialize popup only once during startup and here just do myDialog.show();
        Stage myDialog = new Stage();
        myDialog.initModality(Modality.WINDOW_MODAL);
        myDialog.setTitle("Info");

        Button okButton = new Button("Ok");
        okButton.setMaxWidth(40.0);
        okButton.setMinWidth(40.0);
        okButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                myDialog.close();
                ClientController.this.vbox_messages.getChildren().clear();
                button_send.setDisable(false);

                //TODO: da chiamare solo se partita finita, non ad ogni parola indovinata
                loadScoreboardScene("player 1", 5, "Player 2", 4 );
            }
        });

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20.0, 20.0, 20.0, 20.0));
        vbox.setSpacing(25);
        vbox.getChildren().add(new Text("Your opponent guessed right the word: " + wordGuessed));
        vbox.getChildren().add(okButton);

        Scene myDialogScene = new Scene(vbox);
        myDialog.setScene(myDialogScene);
        myDialog.show();
        button_send.setDisable(true);
    }
}
