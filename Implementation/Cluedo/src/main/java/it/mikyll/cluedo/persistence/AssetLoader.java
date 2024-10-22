package it.mikyll.cluedo.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AssetLoader {
    public final static String CHARACTERS_FILENAME = "characters.json";
    public final static String WEAPONS_FILENAME = "weapons.json";
    public final static String ROOMS_FILENAME = "rooms.json";
    public final static String BOARD_FILENAME = "board.json";

    public static void main(String[] args)
    {
        List<Character> characters = loadCharacters();
        for(Character c : characters)
            System.out.println(c.toString());
        List<Weapon> weapons = loadWeapons();
        for(Weapon w : weapons)
            System.out.println(w.toString());

        List<Room> rooms = loadRooms();
        for(Room r : rooms)
            System.out.println(r.toString());
        //Board board = loadBoard();
        //System.out.println(board.toString());
    }

    public static List<Character> loadCharacters() {
        List<Character> characters = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                AssetLoader.class.getClassLoader().getResourceAsStream(CHARACTERS_FILENAME)))) {
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
    public static List<Weapon> loadWeapons() {
        List<Weapon> weapons = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                AssetLoader.class.getClassLoader().getResourceAsStream(WEAPONS_FILENAME)))) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .excludeFieldsWithModifiers(Modifier.FINAL)
                    .create();
            Type weaponListType = new TypeToken<List<Weapon>>() {}.getType();
            weapons = gson.fromJson(reader, weaponListType);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return weapons;
    }
    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                AssetLoader.class.getClassLoader().getResourceAsStream(ROOMS_FILENAME)))) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .excludeFieldsWithModifiers(Modifier.FINAL)
                    .create();
            Type roomListType = new TypeToken<List<Room>>() {}.getType();
            rooms = gson.fromJson(reader, roomListType);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return rooms;
    }
    public static Board loadBoard() {
        Board board = new Board();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                AssetLoader.class.getClassLoader().getResourceAsStream(BOARD_FILENAME)))) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .excludeFieldsWithModifiers(Modifier.FINAL)
                    .create();
            Type boardType = new TypeToken<Board>() {}.getType();
            board = gson.fromJson(reader, boardType);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return board;
    }
}
