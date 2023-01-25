package app.cowordle.server;


import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import app.cowordle.shared.Vocabulary;
import java.util.*;

public class GameHandler {

    //region Constants

    private static final int WORD_LENGTH = 5;
    private static final int MAX_SCORE = 2; //TODO: PUT BACK 5

    //endregion

    //region Properties

    private String currentTurnClientGuid;
    private String currentWord;
    private boolean gameInProgress;
    private int currentTurnUserIndex;
    private int maxNumOfPlayers;
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Server server;

    //endregion

    //region Constructors

    public GameHandler(Server server, int maxNumOfPlayers, ArrayList<ClientHandler> clientHandlers) {
       this.server = server;
        this.maxNumOfPlayers = maxNumOfPlayers;
        this.clientHandlers = clientHandlers;

        for (ClientHandler clientHandler: clientHandlers) {
            listenForMessage(clientHandler);
        }

        initializeNewGame();
    }

    //endregion

    //region Private Methods

    private void initializeNewGame() {
        this.gameInProgress = true;
        this.currentTurnUserIndex = this.maxNumOfPlayers - 1;
        setNextTurnPlayer();

        Vocabulary vocabulary = new Vocabulary();
        currentWord = vocabulary.getWord();

        System.out.println("word to guess: " + currentWord);
    }

    private void setNextTurnPlayer() {
        currentTurnUserIndex = currentTurnUserIndex == this.maxNumOfPlayers - 1 ? 0 : currentTurnUserIndex + 1;
        currentTurnClientGuid = clientHandlers.get(currentTurnUserIndex).getGuid();
        server.notifyNextTurnPlayer(currentTurnClientGuid);
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

    private void manageWordGuessed(String result, ClientHandler clientHandler) {
        clientHandler.incrementScore();
        if(clientHandler.getScore() == MAX_SCORE) {
            server.manageEndGame(ActionType.GAMEEND);
        } else {
            server.manageWordGuessed(result);
        }
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
                            String answerEvaluation = getAnswerEvaluation(msgFromClient);
                            server.manageWordGuess(msgFromClient.message, answerEvaluation);

                            if (wordCorrectlyGuessed(answerEvaluation))
                                manageWordGuessed(answerEvaluation, clientHandler);
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

    //endregion
}
