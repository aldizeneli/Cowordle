package app.cowordle.client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import com.google.gson.Gson;
import javafx.scene.control.Label;
import javafx.stage.Popup;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private boolean isMyTurn;
    private ClientController controller;
    private boolean gameInProgress;
    private boolean gameEnded;

    public Client(Socket socket, String username, ClientController controller) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.username = username;
            this.controller = controller;

            //avvio i thread (sarebbero operazioni bloccanti altrimenti)
            listenForMessage();
            //client.sendMessage();
            sendMessageToServer(username, ActionType.CLIENTREGISTRATION);
            startHeartbeatSystem();
        } catch(IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void startHeartbeatSystem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule( new TimerTask() {
                    @Override
                    public void run() {
                        if(!gameEnded)
                            sendMessageToServer(username, ActionType.HEARTBEAT);
                        else {
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }, 0, 3000);
            }
        }).start();
    }


    public void listenForMessage() {
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

                        System.out.println(message.message + "  " + message.action);

                        if(message.action == ActionType.TURNCHANGE) {
                            if(message.message.equals(username)) {
                                System.out.println("è il mio turno!");
                                isMyTurn = true;
                            } else
                                isMyTurn = false;

                            //TODO: unlock ui to insert values
                        } else if(message.action == ActionType.SERVERINFO) { //TODO: REMOVE?

                        } else if(message.action == ActionType.GAMESTART) {
                            gameInProgress = true;
                        } else if(message.action == ActionType.WORDGUESSRESULT) {
                            controller.addWordGuess(message.message, message.additionalInfo);
                        } else if(message.action == ActionType.WORDGUESSED) {
                            controller.showPopUp("Your opponent guessed right the word: " + message.additionalInfo, false);
                        }  else if(message.action == ActionType.GAMEEND) {
                            isMyTurn = false;
                            gameEnded = true;
                            controller.showPopUp("GAME OVER!", true);
                        } else if(message.action == ActionType.PLAYERLEFT) {
                            isMyTurn = false;
                            gameEnded = true;
                            controller.showPopUp("Your opponent left the game!", true);
                        }

                    } catch(IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
                System.out.println("client exit listenMessage thread: " + username);
            }
        }).start();
    }

    public void sendMessageToServer(String messageInput, ActionType actionType) {
        try {
            Gson gson = new Gson();
            if(!isMyTurn && (actionType != ActionType.CLIENTREGISTRATION && actionType != ActionType.HEARTBEAT)) {
                System.out.println("Is not my turn! i cant send this message to the server");
                //TODO: visible in ui "is not your turn!" or some other indicator for the user to know its their turn
                return;
            }

            Message messageObject = new Message(messageInput, username, actionType, null);
            String message = gson.toJson(messageObject);
            System.out.println(message);

            bufferedWriter.write(message);
            bufferedWriter.newLine(); //serve xk il reader legge fino al new line e senza nn leggerebbe il mess mandato sopra
            bufferedWriter.flush();
        } catch(IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
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

    public String getUsername() {
        return this.username;
    }
}
