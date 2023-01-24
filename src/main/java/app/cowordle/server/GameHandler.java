package app.cowordle.server;


import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import app.cowordle.shared.Vocabulary;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameHandler {
    private static final int MAX_NUM_OF_PLAYERS = 2;
    private static final int HEARTBEAT_TOLERANCE_SECONDS = 10;
    private static final int WORD_LENGTH = 5;
    private static final int MAX_SCORE = 2; //TODO: PUT BACK 5

    private ServerSocket serverSocket;
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String currentTurnClientGuid;
    private String currentWord;
    private boolean gameInProgress;
    private boolean waitingPlayers;
    private int currentTurnUserIndex;

    public GameHandler(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
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

                broadcastMessage("SERVER: Si è collegato " + clientHandler.getUsername() +". Num di utenti: " + clientHandlers.size(), ActionType.SERVERINFO, null);
            }
            waitingPlayers = false;
            initializeNewGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeNewGame() {
        System.out.println("All ready, starting game...");
        broadcastMessage("", ActionType.GAMESTART, null);

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
                            long secondsSinceLastHeartbeat = (new Date().getTime() - clientHandler.getElapsedTimeFromLastHeartbeat()) / 1000;
                            if (secondsSinceLastHeartbeat >= HEARTBEAT_TOLERANCE_SECONDS) {
                                System.out.println("client disconnected: " + clientHandler.getUsername());
                                clientHandler.closeEverything();
                                disconnectedClients.add(clientHandler);
                            }
                        }
                        if(disconnectedClients.size() > 0) {
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
                Message msgFromClient;
                while(clientHandler.isSocketOpen()) {
                    msgFromClient = clientHandler.listenForMessage();

                    if(msgFromClient == null)
                        continue;

                    //heartbeat management
                    if(msgFromClient.action == ActionType.HEARTBEAT) {
                        //System.out.println("Heartbeat from: " + message.username);
                        clientHandler.seLastHeartbeatDate(new Date());
                        continue;
                    }

                    if(gameInProgress) {
                        boolean isClientsTurn = msgFromClient.guid.equals(currentTurnClientGuid);
                        if (isClientsTurn) {
                            String result = getAnswerEvaluation(msgFromClient);
                            broadcastMessage(msgFromClient.message, ActionType.WORDGUESSRESULT, result);

                            if (wordCorrectlyGuessed(result))
                                manageWordGuessed(result, clientHandler);
                            else
                                setNextTurnPlayer();
                        } else
                            System.out.println("message refused from " + clientHandler.getUsername());
                    }
                }
                System.out.println("ListenForMessage thread exit for " + clientHandler.getUsername());
            }

            private boolean wordCorrectlyGuessed(String result) {
                return result.equals(("ggggg"));
            }
        }).start();
    }

    private void setNextTurnPlayer() {
        currentTurnUserIndex = currentTurnUserIndex == MAX_NUM_OF_PLAYERS - 1 ? 0 : currentTurnUserIndex + 1;
        currentTurnClientGuid = clientHandlers.get(currentTurnUserIndex).getGuid();
        broadcastMessage(currentTurnClientGuid, ActionType.TURNCHANGE, null);
    }

    private void manageWordGuessed(String result, ClientHandler clientHandler) {
        clientHandler.incrementScore();
        if(clientHandler.getScore() == MAX_SCORE) {
            manageEndGame(ActionType.GAMEEND);
        } else {
            broadcastMessage(result, ActionType.WORDGUESSED, null);
        }
    }

    private void manageEndGame(ActionType actionType) {
        StringBuilder stringBuilder = new StringBuilder();

        Comparator<ClientHandler> scoreComparator = (a,b) -> Integer.compare(b.getScore(), a.getScore());
        clientHandlers.sort(scoreComparator);

        for(ClientHandler clientHandler : clientHandlers) {
            stringBuilder.append(clientHandler.getUsername());
            stringBuilder.append(";");
            stringBuilder.append(clientHandler.getScore());
            stringBuilder.append(";");
        }

        broadcastMessage(stringBuilder.toString(), actionType, null);
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
        StringBuilder answer = new StringBuilder();
        char[] currentWordArray = message.message.toLowerCase().toCharArray();

        for (int i = 0; i < WORD_LENGTH; i++) {
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
        for(ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(messageToSend, action, additionalInfo);
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
