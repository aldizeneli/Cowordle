package app.cowordle.shared;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Vocabulary {
    public static final String JSON_PATH = "src/main/resources/app/cowordle/server/vocabulary.json";
    public ArrayList<Word> vocabulary = new ArrayList<Word>();

    public Vocabulary() {
        try {
            loadVocabulary();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadVocabulary() throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader br =  new BufferedReader(new FileReader(JSON_PATH));
        Type type = new TypeToken<List<Word>>(){}.getType();
        vocabulary = gson.fromJson(br, type);
    }

    public String getWord() {
        int randomIndex = (int)(Math.random() * vocabulary.toArray().length);
        System.out.println(randomIndex);
        System.out.println(vocabulary.toArray().length );
        System.out.println(vocabulary.get(0).value);
        Word wordToGuess = vocabulary.get(randomIndex);
        vocabulary.remove(randomIndex);
        return wordToGuess.value;
    }
}
