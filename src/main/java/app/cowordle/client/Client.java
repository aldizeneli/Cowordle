package app.cowordle.client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

import app.cowordle.shared.ActionType;
import app.cowordle.shared.Message;
import com.google.gson.Gson;

public class Client {

    //region Properties

    private String guid;
    private String username;
    private boolean isMyTurn;

    //endregion

    //region Constructors

    public Client() {
        this.isMyTurn = false;
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

    //endregion

    //region Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setIsMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
    }

    //endregion
}
