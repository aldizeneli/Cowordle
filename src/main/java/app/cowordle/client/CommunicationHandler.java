package app.cowordle.client;


import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

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
            closeEverything(socket, bufferedReader, bufferedWriter);
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
            closeEverything(socket, bufferedReader, bufferedWriter);
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
                Timer timer = new Timer();
                timer.schedule( new TimerTask() {
                    @Override
                    public void run() {
                        if(!gameEnded)
                            sendMessageToServer(client.getUsername(), ActionType.HEARTBEAT);
                        else {
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }, 0, 3000);
            }
        }).start();
    }

    private void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromServer;
                Gson gson = new Gson();

                while(socket.isConnected() && !gameEnded) {
                    try {
                        msgFromServer = bufferedReader.readLine();
                        Message message = gson.fromJson(msgFromServer, Message.class);

                        if(message == null)
                            continue;

                        //System.out.println(message.message + "  " + message.action);

                        if(message.action == ActionType.TURNCHANGE) {
                            client.setIsMyTurn(message.message.equals(client.getGuid()));

                            //TODO: remove
                            if (client.getIsMyTurn())
                                System.out.println("è il mio turno!");

                            controller.manageMyTurnIndicators(client.getIsMyTurn());
                        }
                         else if(message.action == ActionType.CLIENTREGISTRATION) {
                            client.setGuid(message.message);
                        } else if(message.action == ActionType.GAMESTART) {
                            //could be useful
                        }
                         else if(message.action == ActionType.WORDGUESSRESULT) {
                            controller.addWordGuess(message.message, message.additionalInfo);
                        } else if(message.action == ActionType.WORDGUESSED) {
                            controller.showPopUp("Word guessed!", false, null);
                        }  else if(message.action == ActionType.GAMEEND || message.action == ActionType.PLAYERLEFT) {
                            String popupText = message.action == ActionType.GAMEEND ? "GAME OVER!" : "Your opponent left the game!";
                            gameEnded = true;
                            controller.showPopUp(popupText, true, message.message);
                        }

                    } catch(IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
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