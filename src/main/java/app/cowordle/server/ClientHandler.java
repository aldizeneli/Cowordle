package app.cowordle.server;

import app.cowordle.shared.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler {
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String clientUsername;
    public Date lastHeartbeatDate;
    private int score;
    public boolean gameEnded;
    public static final int MAX_SCORE = 1; //TODO: PUT BACK 5

    public ClientHandler(Socket socket) {
        try {
            gameEnded = false;
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.lastHeartbeatDate = new Date();
            getClientUsername();

        } catch(IOException e) {
            closeEverything();
        }
    }

    private void getClientUsername()  {
        try {
            //fist message from a client is its username
            Gson gson = new Gson();
            String msgFromServer = bufferedReader.readLine();
            Message message = gson.fromJson(msgFromServer, Message.class);
            this.clientUsername = message.message;
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

    public boolean isWinner() {
        return this.score >= MAX_SCORE;
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
