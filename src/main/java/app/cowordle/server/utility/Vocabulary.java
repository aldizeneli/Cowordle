package app.cowordle.server.utility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Vocabulary {

    //region Constants
    public static final String JSON_PATH = "src/main/java/app/cowordle/server/resources/vocabulary.json";
    //endregion

    //region Properties
    public ArrayList<Word> vocabulary = new ArrayList<Word>();
    //endregion

    //region Constructors
    public Vocabulary() {
        try {
            loadVocabulary();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Public Methods

    public String getWord() {
        int randomIndex = (int)(Math.random() * vocabulary.toArray().length);
        Word wordToGuess = vocabulary.get(randomIndex);
        vocabulary.remove(randomIndex);
        return wordToGuess.value.toLowerCase();
    }

    //endregion


    //region Private Methods

    private void loadVocabulary() throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader br =  new BufferedReader(new FileReader(JSON_PATH));
        Type type = new TypeToken<List<Word>>(){}.getType();
        vocabulary = gson.fromJson(br, type);
    }

    //endregion
}
