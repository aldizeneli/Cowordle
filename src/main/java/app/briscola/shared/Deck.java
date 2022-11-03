package app.briscola.shared;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Deck {
    public static final String JSON_PATH = "src/main/resources/app/briscola/server/cards.json";
    public ArrayList<Card> cards = new ArrayList<Card>();
    public Card briscola;

    public Deck() {
        try {
            loadCards();
            briscola = getBriscola();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBriscolaCode() {
        return briscola.type;
    }

    private void loadCards() throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(JSON_PATH));
        Type type = new TypeToken<List<Card>>(){}.getType();
        cards = gson.fromJson(br, type);
    }

    private Card getBriscola() {
        int randomIndex = (int)(Math.random() * 50 + 1);
        Card briscola = cards.get(randomIndex);
        cards.remove(randomIndex); //moving to last card
        cards.add(briscola);
        return briscola;
    }

    public void draw() {
        //pesca prossima carta
    }
}
