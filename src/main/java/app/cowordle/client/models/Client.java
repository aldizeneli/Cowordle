package app.cowordle.client.models;

public class Client {

    //region Properties

    private String guid;
    private String username;
    private boolean isMyTurn;

    private int score;

    //endregion

    //region Constructors

    public Client() {
        this.isMyTurn = false;
        this.score = 0;
    }

    public Client(String username) {
        this();
        this.username = username;
    }

    //endregion

    //region Getters

    public String getUsername() {
        return this.username;
    }

    public String getGuid() {
        return this.guid;
    }

    public boolean getIsMyTurn() {
        return this.isMyTurn;
    }

    public int getScore() {
        return this.score;
    }

    public void increaseScore() {
        this.score++;
    }

    //endregion

    //region Setters

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setIsMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
    }

    //endregion
}
