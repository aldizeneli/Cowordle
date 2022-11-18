package app.cowordle.client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

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
    private boolean gameStarted;

    public Client(Socket socket, String username, ClientController controller) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.username = username;
            this.controller = controller;

        } catch(IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    //mando il nome come primo messaggio e poi in loop mando ogni volta che client scrive
                    bufferedWriter.write(username);
                    bufferedWriter.newLine(); //serve xk il reader legge fino al new line e senza nn leggerebbe il mess mandato sopra
                    bufferedWriter.flush();

                    Scanner scanner = new Scanner(System.in); //per leggere input da console

                    while(socket.isConnected()) {
                        String inputText = scanner.nextLine();

                        if(!isMyTurn) {
                            System.out.println("Is not my turn! i cant send this message to the server");
                            continue;
                        }

                        Message messageObject = new Message(inputText, username, ActionType.CLIENTANSWER);
                        String message = gson.toJson(messageObject);
                        System.out.println(message);

                        bufferedWriter.write(message);
                        bufferedWriter.newLine(); //serve xk il reader legge fino al new line e senza nn leggerebbe il mess mandato sopra
                        bufferedWriter.flush();
                    }
                } catch(IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromServer;
                Gson gson = new Gson();
                while(socket.isConnected()) {
                    try {
                        msgFromServer = bufferedReader.readLine();
                        Message message = gson.fromJson(msgFromServer, Message.class);
                        System.out.println(message.message + "  " + message.action);

                        if(message.action == ActionType.TURNCHANGE) {
                            if(message.message.equals(username)) {
                                System.out.println("è il mio turno!");
                                isMyTurn = true;
                            } else
                                isMyTurn = false;

                            //TODO: unlock ui to insert values
                        } else if(message.action == ActionType.SERVERINFO) {
                            if(message.message.equals("SERVER:TURNEND")) {
                                //receive new card from server and update ui
                                //controller.test();
                            }
                        } else if(message.action == ActionType.GAMESTART) {
                            gameStarted = true;
                        } else if(message.action == ActionType.WORDGUESSRESULT) {
                            controller.addWordGuess(message.message);
                        } else if(message.action == ActionType.WORDGUESSED) {

                            controller.initializeNewTurn("TODO: get guessed word from message");
                        }

                    } catch(IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void sendMessageToServer(String messageInput) {
        try {
            Gson gson = new Gson();
            if(!isMyTurn) {
                System.out.println("Is not my turn! i cant send this message to the server");
                //TODO: visible in ui "is not your turn!"
                return;
            }

            Message messageObject = new Message(messageInput, username, ActionType.WORDGUESS);
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
}
