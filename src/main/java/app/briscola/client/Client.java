package app.briscola.client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import app.briscola.shared.Message;
import com.google.gson.Gson;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private boolean isMyTurn;
    private ClientController controller;

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

                        Message messageObject = new Message(inputText, username);
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
                String msgFromGroupChat;

                while(socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);

                        if(msgFromGroupChat.equals("GO:"+username)) {
                            System.out.println("è il mio turno!");
                            isMyTurn = true;
                        }

                        if(msgFromGroupChat.equals("test"+username)) {
                            controller.test();
                        }

                    } catch(IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
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
