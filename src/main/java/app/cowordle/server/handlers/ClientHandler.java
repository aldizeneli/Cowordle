package app.cowordle.server.handlers;

import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler {

    //region Properties

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String guid;
    private String username;
    private Date lastHeartbeatDate;
    private int score;
    private boolean connectionClosed;

    //endregion

    //region Constructors

    public ClientHandler(Socket socket) {
        try {
            connectionClosed = false;
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.guid = String.valueOf(java.util.UUID.randomUUID());
            this.updateLastHeartbeatDate();

            handshake();
        } catch(IOException e) {
            e.printStackTrace();
            //if we get an exception in this initial stage of the connection, we
            //free all resources
            closeEverything();
        }
    }

    //endregion

    //public Methods

    public void incrementScore() {
        this.score++;
    }

    public boolean isSocketOpen() {
        return this.socket.isConnected() && !this.connectionClosed;
    }

    public int getScore() {
        return this.score;
    }

    public long getElapsedTimeFromLastHeartbeat() {
        return this.lastHeartbeatDate.getTime();
    }

    public void updateLastHeartbeatDate() {
        this.lastHeartbeatDate = new Date();
    }

    public String getUsername() {
        return this.username;
    }

    public String getGuid() {
        return this.guid;
    }

    public Message listenForMessage() {
        Gson gson = new Gson();
        String msgFromServer = null;
        try {
            //i/o is blocking operation
            if(this.isSocketOpen())
                msgFromServer = bufferedReader.readLine();
        } catch(IOException e) {
            //e.printStackTrace();
            closeEverything();
        }
        Message message = gson.fromJson(msgFromServer, Message.class);
        return message;
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
            //e.printStackTrace();
            closeEverything();
        }
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

    //endregion

    //region Private Methods

    private void sendGuidToClient(String guid)  {
        sendMessage(guid, ActionType.CLIENTREGISTRATION, null);
    }

    private void handshake() {
        getUsernameFromClient();
        sendGuidToClient(this.guid);
    }

    private void getUsernameFromClient()  {
        //fist message from a client is its username
        Message messageFromServer = listenForMessage();
        this.username = messageFromServer.body;
    }

    //endregion
}
