package app.cowordle.client;


import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.stage.Popup;
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
    private ImageView imageView_logo;
    private Client client;

    public ClientController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your username for the game: ");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234);
            this.client = new Client(socket, username, this);

            //avvio i thread (sarebbero operazioni bloccanti altrimenti)
            client.listenForMessage();
            client.sendMessage();
        }  catch (IOException e) {
            e.printStackTrace();
        }

        this.vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                ClientController.this.sp_main.setVvalue((Double)newVal);
            }
        });

        this.button_send.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                String inputText = ClientController.this.tf_message.getText();
                if (!inputText.isEmpty() && inputText.length() == 5) { //TODO: Support variable length
//                    HBox hBox = new HBox();
//                    hBox.setAlignment(Pos.CENTER_RIGHT);
//                    hBox.setPadding(new Insets(5.0, 5.0, 5.0, 10.0));
//                    Text text = new Text(messageToSend);
//                    TextFlow textFlow = new TextFlow(new Node[]{text});
//                    textFlow.setStyle("-fx-color: rgb(239, 242, 255); -fx-background-color: rgb(15, 125, 242); -fx-background-radius: 20px;");
//                    textFlow.setPadding(new Insets(5.0, 10.0, 5.0, 10.0));
//                    text.setFill(Color.color(0.934, 0.945, 0.996));
//                    hBox.getChildren().add(textFlow);
//                    ClientController.this.vbox_messages.getChildren().add(hBox);

                    ClientController.this.client.sendMessageToServer(inputText);
                    ClientController.this.tf_message.clear();
                } else {
                    //TODO: error message input word lenght invalid
                }
            }
        });

    }

    public void addWordGuess(String inputWord, String resultFromServer) {
        Platform.runLater(new Runnable() {
            public void run()
            {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setPadding(new Insets(10, 0, 0, 5));
                hBox.setSpacing(8);

                char[] inputWordArray = inputWord.toCharArray();
                char[] resultFromServerArray = resultFromServer.toCharArray();
                for (int i = 0; i < inputWordArray.length; i++) {
                    char inputChar = inputWordArray[i];
                    char resultChar = resultFromServerArray[i];

                    hBox.getChildren().add(getTextFlowFromChar(inputChar, resultChar));
                }

                ClientController.this.vbox_messages.getChildren().add(hBox);
            }
        });
    }

    public TextFlow getTextFlowFromChar(char answerChar, char resultChar) {
        Text text = new Text(String.valueOf(answerChar).toUpperCase());
        TextFlow textFlow = new TextFlow(new Node[]{text});
        textFlow.setStyle("-fx-color: rgb(239, 242, 255); -fx-background-color: " + getRgbColorStringFromChar(resultChar) +"; -fx-background-radius: 12px;");
        //textFlow.setPadding(new Insets(10.0, 5.0, 10.0, 5.0));
        text.setFill(Color.color(0.934, 0.945, 0.996));
        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 85));

        textFlow.setMinHeight(115.0);
        textFlow.setMinWidth(90.0);
        textFlow.setMaxHeight(115.0);
        textFlow.setMaxWidth(90.0);

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
