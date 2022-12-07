package app.cowordle.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler {

    //public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String clientUsername;
    private int score;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            //clientHandlers.add(this);
            //broadcastMessage("SERVER: " + clientUsername + " has entered the chat");
        } catch(IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void incrementScore() {
        this.score ++;
    }

    public boolean isWinner() {
        return this.score == 5;
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        //removeClientHandler();
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
