package app.cowordle.shared;

public class Message {
    public String message;
    public String username;
    public Message(String message, String username) {
        this.username = username;
        this.message = message;
    }
}