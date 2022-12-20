package app.cowordle.server;

import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler {
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String username;
    public Date lastHeartbeatDate;
    private int score;
    public boolean gameEnded;
    public String guid;

    public ClientHandler(Socket socket) {
        try {
            gameEnded = false;
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.lastHeartbeatDate = new Date();
            this.guid = String.valueOf(java.util.UUID.randomUUID());
            handshake();
        } catch(IOException e) {
            closeEverything();
        }
    }

    private void handshake() {
        getUsernameFromClient();
        sendGuidToClient(this.guid);
    }

    private void getUsernameFromClient()  {
        try {
            //fist message from a client is its username
            Gson gson = new Gson();
            String msgFromServer = bufferedReader.readLine();
            Message message = gson.fromJson(msgFromServer, Message.class);
            this.username = message.message;
        } catch(IOException e) {
            closeEverything();
        }
    }

    private void sendGuidToClient(String guid)  {
        try {
            Gson gson = new Gson();
            Message messageObject = new Message(guid, "server", ActionType.CLIENTREGISTRATION, null);
            String message = gson.toJson(messageObject);

            bufferedWriter.write(message);
            bufferedWriter.newLine(); //serve xk il reader legge fino al new line e senza nn leggerebbe il mess mandato sopra
            bufferedWriter.flush();
        } catch(IOException e) {
            closeEverything();
        }
    }

    public void incrementScore() {
        this.score++;
    }

    public int getScore() {
        return this.score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void closeEverything() {
        try {
            gameEnded = true;

            //releasing the socket releases threads blocked on i/o operations
            if(socket != null) {
                socket.close();
            }

            //if socket is not release before, these operations would be blocked
            //by threads busy with i/o operations on them
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
