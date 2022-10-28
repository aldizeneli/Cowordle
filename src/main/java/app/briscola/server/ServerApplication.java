package app.briscola.server;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

import javafx.scene.Parent;

public class ServerApplication extends javafx.application.Application {
    public ServerApplication() {
    }

    public void start(Stage stage) throws IOException{
//        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("sample.fxml"));
//        Scene scene = new Scene((Parent)fxmlLoader.load(), 478.0, 396.0);
//        stage.setTitle("Server");
//        stage.setScene(scene);
//        stage.show();
    }

    public static void main(String[] args) {

//        launch(new String[0]); //server non ha interfaccia

        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            server.startServer();

//			server.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
