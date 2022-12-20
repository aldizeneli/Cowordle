package app.cowordle.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.cowordle.shared.ActionType;
import app.cowordle.shared.Vocabulary;
import com.google.gson.Gson;

import app.cowordle.shared.Message;

public class Server {
    private ServerSocket serverSocket;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String currentTurnClientGuid;
    private String currentWord;
    private boolean gameInProgress;
    private boolean waitingPlayers;
    private int currentTurnUserIndex;
    public static final int MAX_NUM_OF_PLAYERS = 2;
    public static final int HEARTBEAT_TOLERANCE_SECONDS = 10;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;

        //start monitoring client connections only after game start
        monitorClientsConnection();
    }

    public void startServer() {
        try {
            System.out.println("Server avviato...");
            waitingPlayers = true;
            gameInProgress = false;

            while(!serverSocket.isClosed() && clientHandlers.size() < MAX_NUM_OF_PLAYERS) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

                listenForMessage(clientHandler);

                broadcastMessage("SERVER: Si è collegato " + clientHandler.username +". Num di utenti: " + clientHandlers.size(), ActionType.SERVERINFO, null);
            }
            waitingPlayers = false;
            initializeNewGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeNewGame() {
        System.out.println("All ready, starting game...");
        broadcastMessage("SERVER: tutti gli utenti connessi. Iniziamo", ActionType.GAMESTART, null);

        this.gameInProgress = true;

        this.currentTurnUserIndex = MAX_NUM_OF_PLAYERS-1;
        setNextTurnPlayer();

        Vocabulary vocabulary = new Vocabulary();
        currentWord = vocabulary.getWord();

        System.out.println("word to guess: " + currentWord);
    }

    private void monitorClientsConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule( new TimerTask() {
                    @Override
                    public void run() {
                        ArrayList<ClientHandler> disconnectedClients = new ArrayList<>();
                        for (ClientHandler clientHandler:clientHandlers) {
                            long secondsSinceLastHeartbeat = (new Date().getTime() - clientHandler.lastHeartbeatDate.getTime()) / 1000;

                            if (secondsSinceLastHeartbeat > HEARTBEAT_TOLERANCE_SECONDS) {
                                System.out.println("client disconnected: " + clientHandler.username);
                                clientHandler.closeEverything();
                                disconnectedClients.add(clientHandler);
                            }
                        }
                        if(disconnectedClients.size() > 0) {
                            clientHandlers.removeAll(disconnectedClients);
                            if(gameInProgress)
                                manageEndGame(ActionType.PLAYERLEFT);
                        }
                    }
                }, 0, 2000);
            }
        }).start();
    }

    private void listenForMessage(ClientHandler clientHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromClient;
                Gson gson = new Gson();
                while(clientHandler.socket.isConnected() && !clientHandler.gameEnded) {
                    try {
                        msgFromClient = clientHandler.bufferedReader.readLine();
                        Message message = gson.fromJson(msgFromClient, Message.class);

                        //heartbeat management
                        if(message.action == ActionType.HEARTBEAT) {
                            //System.out.println("Heartbeat from: " + message.username);
                            clientHandler.lastHeartbeatDate = new Date();
                            continue;
                        }

                        if(gameInProgress) {
                            boolean isClientsTurn = message.guid.equals(currentTurnClientGuid);
                            if (isClientsTurn) {
                                String result = getAnswerEvaluation(message);
                                broadcastMessage(message.message, ActionType.WORDGUESSRESULT, result);

                                if (wordCorrectlyGuessed(result))
                                    manageWordGuessed(result, clientHandler);

                                setNextTurnPlayer();
                            } else
                                System.out.println("message refused from " + clientHandler.username);
                        }

                    } catch(IOException e) {
//                        e.printStackTrace();
                    }
                }
                System.out.println("ListenForMessage thread exit for " + clientHandler.username);
            }

            private boolean wordCorrectlyGuessed(String result) {
                return result.equals(("ggggg"));
            }
        }).start();
    }

    private void setNextTurnPlayer() {
        currentTurnUserIndex = currentTurnUserIndex == MAX_NUM_OF_PLAYERS - 1 ? 0 : currentTurnUserIndex + 1;
        currentTurnClientGuid = clientHandlers.get(currentTurnUserIndex).guid;
        broadcastMessage(currentTurnClientGuid, ActionType.TURNCHANGE, null);
    }

    private void manageWordGuessed(String result, ClientHandler clientHandler) {
        clientHandler.incrementScore();
        if(clientHandler.isWinner()) {
            manageEndGame(ActionType.GAMEEND);
        } else {
            broadcastMessage(result, ActionType.WORDGUESSED, null);
        }
    }

    private void manageEndGame(ActionType actionType) {
        broadcastMessage(null, actionType, null);
        endCurrentGame();
        startServer();
    }

    private static void endCurrentGame() {
        for (ClientHandler client: clientHandlers) {
            client.closeEverything();
        }
        clientHandlers.clear();
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
