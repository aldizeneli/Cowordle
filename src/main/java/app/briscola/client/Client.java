package app.briscola.client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javafx.scene.layout.VBox;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException var3) {
            System.out.print("Error creating client.");
            var3.printStackTrace();
            this.closeEveryThing(socket, this.bufferedReader, this.bufferedWriter);
        }

    }

    public void sendMessageToServer(String messageToServer) {
        try {
            this.bufferedWriter.write(messageToServer);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException var3) {
            var3.printStackTrace();
            System.out.println("Error sending message to server");
            this.closeEveryThing(this.socket, this.bufferedReader, this.bufferedWriter);
        }

    }

    public void receiveMessageFromServer(final VBox vBox) {
        (new Thread(new Runnable() {
            public void run() {
                while(true) {
                    if (Client.this.socket.isConnected()) {
                        try {
                            String messageFromServer = Client.this.bufferedReader.readLine();
                            HelloController.addLabel(messageFromServer, vBox);
                            continue;
                        } catch (IOException var2) {
                            var2.printStackTrace();
                            System.out.println("Error receiving message from server");
                            Client.this.closeEveryThing(Client.this.socket, Client.this.bufferedReader, Client.this.bufferedWriter);
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
