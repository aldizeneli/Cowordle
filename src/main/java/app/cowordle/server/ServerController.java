package app.cowordle.server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ServerController implements Initializable {
    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;
    private Server server;

    public ServerController() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {


//        this.vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
//                ServerController.this.sp_main.setVvalue((Double)newVal);
//            }
//        });
//        this.server.receiveMessageFromClient(this.vbox_messages);
//        this.button_send.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent actionEvent) {
//                String messageToSend = ServerController.this.tf_message.getText();
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
//                    ServerController.this.vbox_messages.getChildren().add(hBox);
//                    ServerController.this.server.sendMessageToClient(messageToSend);
//                    ServerController.this.tf_message.clear();
//                }
//
//            }
//        });
    }

    public static void addLabel(String messageFromClient, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5.0, 5.0, 5.0, 10.0));
        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(new Node[]{text});
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235); -fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5.0, 10.0, 5.0, 10.0));
        hBox.getChildren().add(textFlow);
        Platform.runLater(new Runnable() {
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });
    }
}
