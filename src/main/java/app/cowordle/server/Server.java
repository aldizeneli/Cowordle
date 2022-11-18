package app.cowordle.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import app.cowordle.shared.Vocabulary;
import com.google.gson.Gson;

import app.cowordle.shared.Message;

public class Server {
    private ServerSocket serverSocket;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String currentTurnUsername;
    private int currentTurnUserIndex;
    private String currentWord;

    public Server(ServerSocket serverSocket) {

        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            System.out.println("Server avviato...");
            int numOfClients = 0;

            while(!serverSocket.isClosed() && numOfClients < 2) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

//				Thread thread = new Thread(clientHandler);
//				thread.start();

                listenForMessage(clientHandler);
                numOfClients++;

                broadcastMessage("SERVER: Si è collegato " + clientHandler.clientUsername +". Num di utenti: " + numOfClients);
            }

            startGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        System.out.println("All ready, starting game...");
        broadcastMessage("SERVER: tutti gli utenti connessi. Iniziamo");

        currentTurnUserIndex = 0;
        currentTurnUsername = clientHandlers.get(currentTurnUserIndex).clientUsername;

        broadcastMessage("GO:" + currentTurnUsername);

        Vocabulary vocabulary = new Vocabulary();
        currentWord = vocabulary.getWord();

        System.out.println("word to guess: " + currentWord);
    }

    private void listenForMessage(ClientHandler clientHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromClient;
                Gson gson = new Gson();
                while(clientHandler.socket.isConnected()) {
                    try {
                        msgFromClient = clientHandler.bufferedReader.readLine();
                        Message message = gson.fromJson(msgFromClient, Message.class);

                        if(message.username.equals(currentTurnUsername)) {
                            System.out.println(message.message + " " + message.username);

                            currentTurnUserIndex = currentTurnUserIndex == 0 ? 1 : 0;
                            currentTurnUsername = clientHandlers.get(currentTurnUserIndex).clientUsername;
                            broadcastMessage("GO:" + currentTurnUsername);
                        }
                        else
                            System.out.println("messaggio rifiutato");

//                        broadcastMessage("GO:" + currentTurnUsername);
//                        broadcastMessage("test"+currentTurnUsername);

                    } catch(IOException e) {

                    }
                }
                //todo: make it work (implement a heartbeat system)
                System.out.println("A user disconnected");
            }
        }).start();
    }

    private void broadcastMessage(String messageToSend) {
        Gson gson = new Gson();
        for(ClientHandler clientHandler : clientHandlers) {
            try {
                //per nn mandare il messaggio a me stesso
//				if(!clientHandler.clientUsername.equals("test")) {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine(); //serve xk il reader legge fino al new line e senza nn leggerebbe il mess mandato sopra
                clientHandler.bufferedWriter.flush();
//				}
            } catch(IOException e) {
                //closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
