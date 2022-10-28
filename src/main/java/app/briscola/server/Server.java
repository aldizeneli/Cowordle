package app.briscola.server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.scene.layout.VBox;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket) {
        try {
            this.serverSocket = serverSocket;
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException var3) {
            System.out.print("Error creating server.");
            var3.printStackTrace();
            this.closeEveryThing(this.socket, this.bufferedReader, this.bufferedWriter);
        }

    }

    public void sendMessageToClient(String messageToClient) {
        try {
            this.bufferedWriter.write(messageToClient);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException var3) {
            var3.printStackTrace();
            System.out.println("Error sending message to client");
            this.closeEveryThing(this.socket, this.bufferedReader, this.bufferedWriter);
        }

    }

    public void receiveMessageFromClient(final VBox vBox) {
        (new Thread(new Runnable() {
            public void run() {
                while(true) {
                    if (Server.this.socket.isConnected()) {
                        try {
                            String messageFromClient = Server.this.bufferedReader.readLine();
                            Controller.addLabel(messageFromClient, vBox);
                            continue;
                        } catch (IOException var2) {
                            var2.printStackTrace();
                            System.out.println("Error receiving message from client");
                            Server.this.closeEveryThing(Server.this.socket, Server.this.bufferedReader, Server.this.bufferedWriter);
                        }
                    }

                    return;
                }
            }
        })).start();
    }

    public void closeEveryThing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }
}
