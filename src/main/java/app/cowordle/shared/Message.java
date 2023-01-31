package app.cowordle.shared;

public class Message {

    //region Properties

    public String guid;
    public String body;
    public String additionalInfo;
    public ActionType action;

    //endregion

    //region Constructors

    public Message(String body, String guid, ActionType action, String additionalInfo) {
        this.guid = guid;
        this.body = body;
        this.action = action;
        this.additionalInfo = additionalInfo;
    }

    //endregion
}