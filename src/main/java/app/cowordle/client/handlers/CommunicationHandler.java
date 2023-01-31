package app.cowordle.client.handlers;


import app.cowordle.client.Client;
import app.cowordle.client.controllers.GameSceneController;
import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class CommunicationHandler {

    //region Properties

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private boolean gameEnded;
    private GameSceneController controller;
    private Client client;

    //endregion

    //region Constructors

    public CommunicationHandler(Socket socket, String clientUsername, GameSceneController controller) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.controller = controller;
            this.client = new Client(clientUsername);
            this.gameEnded = false;

            //running threads for I/O operations
            listenForMessage();
            sendMessageToServer(client.getUsername(), ActionType.CLIENTREGISTRATION);
            startHeartbeatSystem();
        } catch(IOException e) {
            e.printStackTrace();
            //closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    //endregion

    //region Public Methods

    public void sendMessageToServer(String messageInput, ActionType actionType) {
        try {
            Gson gson = new Gson();
            if(!client.getIsMyTurn() && (actionType != ActionType.CLIENTREGISTRATION && actionType != ActionType.HEARTBEAT)) {
                System.out.println("Is not my turn! i cant send this message to the server");
                return;
            }

            Message messageObject = new Message(messageInput, client.getGuid(), actionType, null);
            String message = gson.toJson(messageObject);
            //System.out.println(message);

            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch(IOException e) {
            e.printStackTrace();
            //closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public String getClientUsername() {
        return this.client.getUsername();
    }

    public Boolean getClientIsMyTurn() {
        return this.client.getIsMyTurn();
    }

    //endregion

    //region Private Methods

    private void startHeartbeatSystem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!gameEnded) {
                        sendMessageToServer(client.getUsername(), ActionType.HEARTBEAT);
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean keepListening = true;
                String msgFromServer;
                Gson gson = new Gson();

                while(keepListening) {
                    try {
                        msgFromServer = bufferedReader.readLine();
                        Message message = gson.fromJson(msgFromServer, Message.class);

                        if(message == null)
                            continue;

                        if(message.action == ActionType.TURNCHANGE) {
                            client.setIsMyTurn(message.body.equals(client.getGuid()));

                            if (client.getIsMyTurn())
                                System.out.println("It's my turn");

                            controller.manageMyTurnIndicators(client.getIsMyTurn());
                        } else if(message.action == ActionType.CLIENTREGISTRATION) {
                            client.setGuid(message.body);
                        } else if(message.action == ActionType.GAMESTART) {
                            System.out.println("Game started");
                        } else if(message.action == ActionType.SERVERINFO) {
                            System.out.println(message.body);
                        } else if(message.action == ActionType.WORDGUESSRESULT) {
                            controller.addWordGuess(message.body, message.additionalInfo);
                        } else if(message.action == ActionType.WORDGUESSED) {
                             if(message.body.equals(client.getGuid())) {
                                 client.increaseScore();
                                 controller.updateScore(client.getScore());
                             }
                            controller.showPopUp("Word guessed!", false, null);
                        }  else if(message.action == ActionType.GAMEEND || message.action == ActionType.PLAYERLEFT) {
                            gameEnded = true;
                            String popupText = message.action == ActionType.GAMEEND ? "GAME OVER!" : "Your opponent left the game!";
                            controller.showPopUp(popupText, true, message.body);
                        }

                        keepListening = socket.isConnected() && !gameEnded;

                    } catch(IOException e) {
                        e.printStackTrace();
                        //closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
                System.out.println("client exit listenMessage thread: " + client.getUsername());
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    //endregion
}
