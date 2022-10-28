package app.briscola.client;


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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ClientController implements Initializable {
    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;
    private Client client;

    public ClientController() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your username for the game: ");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket, username);

            //avvio i thread (sarebbero operazioni bloccanti altrimenti)
            client.listenForMessage();
            client.sendMessage();
        }  catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            this.client = new Client(new Socket("localhost", 1234));
//            System.out.println("Connected to server");
//        } catch (IOException var4) {
//            var4.printStackTrace();
//        }
//
//        this.vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
//                ClientController.this.sp_main.setVvalue((Double)newVal);
//            }
//        });
//        this.client.receiveMessageFromServer(this.vbox_messages);
//        this.button_send.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent actionEvent) {
//                String messageToSend = ClientController.this.tf_message.getText();
//                if (!messageToSend.isEmpty()) {
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
//                    ClientController.this.client.sendMessageToServer(messageToSend);
//                    ClientController.this.tf_message.clear();
//                }
//
//            }
//        });
    }

    public static void addLabel(String msgFromServer, final VBox vBox) {
        final HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5.0, 5.0, 5.0, 10.0));
        Text text = new Text(msgFromServer);
        TextFlow textFlow = new TextFlow(new Node[]{text});
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235); -fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5.0, 10.0, 5.0, 10.0));
        hBox.getChildren().add(textFlow);
        Platform.runLater(new Runnable() {
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }
}
