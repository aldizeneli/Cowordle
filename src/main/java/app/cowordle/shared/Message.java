package app.cowordle.shared;

public class Message {
    public String message;
    public String username;
    public ActionType action;
    public Message(String message, String username, ActionType action) {
        this.username = username;
        this.message = message;
        this.action = action;
    }

}