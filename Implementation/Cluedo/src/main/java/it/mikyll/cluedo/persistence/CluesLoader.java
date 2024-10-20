package it.mikyll.cluedo.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.player.Player;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CluesLoader {
    public final static String CHARACTERS_FILENAME = "../cards/characters.json";

    private void test()
    {
        List<Character> characters = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            CluesLoader.class.getClassLoader().getResourceAsStream(CHARACTERS_FILENAME)))) {
            Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .excludeFieldsWithModifiers(Modifier.FINAL)
                .create();
            Type characterListType = new TypeToken<List<Character>>() {}.getType();
            characters = gson.fromJson(reader, characterListType);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }


    }

    public static void main(String[] args)
    {
        CluesLoader cl = new CluesLoader();
        cl.test();
    }

    public static List<Character> loadCharacters() {
        List<Character> characters = new ArrayList<>();

        ClassLoader classLoader = CluesLoader.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(CHARACTERS_FILENAME);
        if (is == null)
        {
            System.out.println("file not found");
            //System.exit(2);
        }
        FileReader r = null;
        try {
            r = new FileReader("\\it\\mikyll\\cluedo\\cards\\characters.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                CluesLoader.class.getClassLoader().getResourceAsStream(CHARACTERS_FILENAME)))) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .excludeFieldsWithModifiers(Modifier.FINAL)
                    .create();
            Type characterListType = new TypeToken<List<Character>>() {}.getType();
            characters = gson.fromJson(reader, characterListType);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return characters;
    }
    public static List<Clue> load(String filename) {
        return null;
    }
}
