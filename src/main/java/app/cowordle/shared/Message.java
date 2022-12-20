package app.cowordle.shared;

public class Message {
    public String message;
    public String additionalInfo;
    public String guid;
    public ActionType action;
    public Message(String message, String guid, ActionType action, String additionalInfo) {
        this.guid = guid;
        this.message = message;
        this.action = action;
        this.additionalInfo = additionalInfo;
    }

}