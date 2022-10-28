package app.briscola.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {

    //public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            //clientHandlers.add(this);
            //broadcastMessage("SERVER: " + clientUsername + " has entered the chat");
        } catch(IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

//	@Override
//	public void run() {
//		String messageToSend;
//		while(socket.isConnected()) {
//			try {
//				//waiting for a message is a blocking operation!! we would be stuck here with execution until a message is received
//				messageToSend = bufferedReader.readLine();
//				//broadcastMessage(messageFromClient);
//
//				//TODO: mando messaggio solo al server
//				if(!messageToSend.isEmpty() && messageToSend != null) {
//					//System.out.println("sending message to server..");
//					bufferedWriter.write("clientHandler: " + messageToSend);
//					bufferedWriter.newLine(); //serve xk il reader legge fino al new line e senza nn leggerebbe il mess mandato sopra
//					bufferedWriter.flush();
//				}
//
//			} catch(IOException e) {
//				closeEverything(socket, bufferedReader, bufferedWriter);
//				break; //senza di questo il while continuerebbe a girare
//			}
//		}
//	}

//	public void broadcastMessage(String messageToSend) {
//		for(ClientHandler clientHandler : clientHandlers) {
//			try {
//				//per nn mandare il messaggio a me stesso
//				if(!clientHandler.clientUsername.equals(clientUsername)) {
//					clientHandler.bufferedWriter.write(messageToSend);
//					clientHandler.bufferedWriter.newLine(); //serve xk il reader legge fino al new line e senza nn leggerebbe il mess mandato sopra
//					clientHandler.bufferedWriter.flush();
//				}
//			} catch(IOException e) {
//				closeEverything(socket, bufferedReader, bufferedWriter);
//			}
//		}
//	}

//	public void removeClientHandler() {
//		clientHandlers.remove(this);
//		broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
//	}

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        //removeClientHandler();
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
