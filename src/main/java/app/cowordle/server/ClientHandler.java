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
    public static final int MAX_SCORE = 5;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.lastHeartbeatDate = new Date();
            getClientUsername();

        } catch(IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
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
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void incrementScore() {
        this.score++;
    }

    public boolean isWinner() {
        return this.score == MAX_SCORE;
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
