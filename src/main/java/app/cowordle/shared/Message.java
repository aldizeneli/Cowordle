package app.cowordle.shared;

public class Message {
    public String message;
    public String additionalInfo;
    public String username;
    public ActionType action;
    public Message(String message, String username, ActionType action, String additionalInfo) {
        this.username = username;
        this.message = message;
        this.action = action;
        this.additionalInfo = additionalInfo;
    }

}