package app.cowordle.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import app.cowordle.server.handlers.ClientHandler;
import app.cowordle.server.handlers.GameHandler;
import app.cowordle.shared.ActionType;

public class Server {

    //region Constants

    private static final int MAX_NUM_OF_PLAYERS = 2;
    private static final int HEARTBEAT_TOLERANCE_SECONDS = 7;

    //endregion

    //region Properties

    private ServerSocket serverSocket;
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private GameHandler gameHandler;

    //endregion

    //region Constructors

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    //endregion

    //region Public Methods

    public void startServer() {
        try {
            System.out.println("Server avviato...");

            while(!serverSocket.isClosed() && clientHandlers.size() < MAX_NUM_OF_PLAYERS) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

                broadcastMessage("SERVER: new client connected " + clientHandler.getUsername() +". Num of clients: " + clientHandlers.size(), ActionType.SERVERINFO, null);
            }

            initializeNewGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void manageEndGame(ActionType actionType) {
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
        disconnectClients();
        startServer();
    }

    public void manageWordGuess(String clientAnswer, String answerEvaluation) {
        broadcastMessage(clientAnswer, ActionType.WORDGUESSRESULT, answerEvaluation);
    }

    public void manageWordGuessed(String clientGuid) {
        broadcastMessage(clientGuid, ActionType.WORDGUESSED, null);
    }

    public void notifyNextTurnPlayer(String clientGuid) {
        broadcastMessage(clientGuid, ActionType.TURNCHANGE, null);
    }

    //endregion

    //region Private Methods

    private void broadcastMessage(String messageToSend, ActionType action, String additionalInfo) {
        for(ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(messageToSend, action, additionalInfo);
        }
    }

    private void initializeNewGame() {
        System.out.println("All ready, starting game...");
        broadcastMessage("", ActionType.GAMESTART, null);

        this.gameHandler = new GameHandler(this, MAX_NUM_OF_PLAYERS, clientHandlers);
        initializeHeartbeatSystem();
    }

    private void initializeHeartbeatSystem() {
        for(ClientHandler clientHandler : clientHandlers) {
            clientHandler.updateLastHeartbeatDate();
        }
        monitorClientsConnection();
    }

    private void monitorClientsConnection() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    while (gameHandler.gameInProgress()) {
                        ArrayList<ClientHandler> disconnectedClients = new ArrayList<>();
                        for (ClientHandler clientHandler : clientHandlers) {
                            long secondsSinceLastHeartbeat = (new Date().getTime() - clientHandler.getElapsedTimeFromLastHeartbeat()) / 1000;
                            if (secondsSinceLastHeartbeat >= HEARTBEAT_TOLERANCE_SECONDS) {
                                System.out.println("client disconnected: " + clientHandler.getUsername());
                                clientHandler.closeEverything();
                                disconnectedClients.add(clientHandler);
                            }
                        }

                        if (disconnectedClients.size() > 0) {
                            manageEndGame(ActionType.PLAYERLEFT);
                        }
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void disconnectClients() {
        for (ClientHandler client: clientHandlers) {
            client.closeEverything();
        }
        clientHandlers.clear();
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

    //endregion
}
