package app.cowordle.server;


import java.io.IOException;
import java.net.ServerSocket;

public class ServerApplication {
    public ServerApplication() {  }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(49152);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
