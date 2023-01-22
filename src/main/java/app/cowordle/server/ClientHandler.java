package app.cowordle.server;

import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String guid;
    private String username;

    private Date lastHeartbeatDate;
    private int score;
    private boolean connectionClosed;


    public ClientHandler(Socket socket) {
        try {
            connectionClosed = false;
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
        //fist message from a client is its username
        Message messageFromServer = listenForMessage();
        this.username = messageFromServer.message;
        System.out.println(this.username);
    }

    public Message listenForMessage() {
        Gson gson = new Gson();
        String msgFromServer = null;
        try {
            //i/o is blocking operation
            msgFromServer = bufferedReader.readLine();

        } catch(IOException e) {
            closeEverything();
        }
        Message message = gson.fromJson(msgFromServer, Message.class);
        return message;
    }

    private void sendGuidToClient(String guid)  {
        sendMessage(guid, ActionType.CLIENTREGISTRATION, null);
    }

    public void sendMessage(String message, ActionType actionType, String additionalInfo)  {
        try {
            Gson gson = new Gson();
            Message messageObject = new Message(message, "server", actionType, additionalInfo);
            String jsonMessage = gson.toJson(messageObject);

            bufferedWriter.write(jsonMessage);
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

    public boolean isSocketOpen() {
        return this.socket.isConnected() && !this.connectionClosed;
    }

    public long getElapsedTimeFromLastHeartbeat() {
        return this.lastHeartbeatDate.getTime();
    }

    public void seLastHeartbeatDate(Date newDate) {
        this.lastHeartbeatDate = newDate;
    }

    public String getUsername() {
        return this.username;
    }

    public String getGuid() {
        return this.guid;
    }

    public void closeEverything() {
        try {
            this.connectionClosed = true;

            //releasing the socket releases threads blocked on i/o operations
            if(this.socket != null) {
                this.socket.close();
            }

            //if socket is not release before, these operations would be blocked
            //by threads busy with i/o operations on them
            if(this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if(this.bufferedWriter != null) {
                this.bufferedWriter.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
