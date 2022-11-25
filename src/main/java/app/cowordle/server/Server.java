package app.cowordle.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import app.cowordle.shared.ActionType;
import app.cowordle.shared.Vocabulary;
import com.google.gson.Gson;

import app.cowordle.shared.Message;

public class Server {
    private ServerSocket serverSocket;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String currentTurnUsername;
    private int currentTurnUserIndex;
    private String currentWord;
    private boolean gameInProgress;

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

                broadcastMessage("SERVER: Si è collegato " + clientHandler.clientUsername +". Num di utenti: " + numOfClients, ActionType.SERVERINFO, null);
            }

            startGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        System.out.println("All ready, starting game...");
        broadcastMessage("SERVER: tutti gli utenti connessi. Iniziamo", ActionType.GAMESTART, null);

        this.gameInProgress = true;

        currentTurnUserIndex = 0;
        currentTurnUsername = clientHandlers.get(currentTurnUserIndex).clientUsername;

        broadcastMessage(currentTurnUsername, ActionType.TURNCHANGE, null);

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

                            if(gameInProgress) {
                                String result = getAnswerEvaluation(message);

                                System.out.println("1 - " + result + "  -  " + message.message);

                                if(result.equals(("ggggg"))) { //parola indovinata todo: migliorare
                                    clientHandler.incrementScore();
                                    broadcastMessage(result, ActionType.WORDGUESSED, null);

                                    if(clientHandler.isWinner()) {

                                        broadcastMessage(clientHandler.clientUsername, ActionType.GAMEEND, null);

                                        //TODO: operazioni di chiusura partita lato server (ricominciare partita se possibile,
                                        // cioè se i client sn ancora connessi dopo 30 sec (lo capisci dal heartbeat quando lo implementi))
                                    }
                                }
                                else {
                                    broadcastMessage(message.message, ActionType.WORDGUESSRESULT, result);
                                }
                            }

                            currentTurnUserIndex = currentTurnUserIndex == 0 ? 1 : 0;
                            currentTurnUsername = clientHandlers.get(currentTurnUserIndex).clientUsername;
                            broadcastMessage(currentTurnUsername, ActionType.TURNCHANGE, null);
                        }
                        else
                            System.out.println("messaggio rifiutato");


                    } catch(IOException e) {

                    }
                }
                //todo: make it work (implement a heartbeat system OR after x seconds without receivent messages from a client, consider it disconnected)
                System.out.println("A user disconnected");
            }
        }).start();
    }

    private String getAnswerEvaluation(Message message) {
        //TODO: if(answerArray.length != currentWord.lenght) => invalid

        StringBuilder answer = new StringBuilder();
        char[] currentWordArray = message.message.toLowerCase().toCharArray();
        for (int i = 0; i < currentWordArray.length; i++) {
            char currentChar = currentWordArray[i];
            if(currentChar == currentWord.charAt((i))) {
                answer.append('g');
            } else if(currentWord.contains(Character.toString(currentChar))) {
                answer.append('y');
            } else {
                answer.append('r');
            }
        }
        System.out.println("guess result: " + answer);
        return answer.toString();
    }

    private void broadcastMessage(String messageToSend, ActionType action, String additionalInfo) {
        Gson gson = new Gson();
        for(ClientHandler clientHandler : clientHandlers) {
            try {
                Message messageObject = new Message(messageToSend, "server", action, additionalInfo);
                String message = gson.toJson(messageObject);

                clientHandler.bufferedWriter.write(message);
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
