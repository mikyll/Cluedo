package it.mikyll.cluedo.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import it.mikyll.cluedo.model.settings.Settings;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class SettingsManager {
    public final static String SETTINGS_FILENAME = ".settings.json";

    public static Settings loadSettings(String filename)
    {
        Settings settings;
        try (FileReader reader = new FileReader(filename)) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .excludeFieldsWithModifiers(Modifier.FINAL)
                    .create();

            Settings.setInstance(gson.fromJson(reader, Settings.class));
            settings = Settings.getInstance();
        } catch (Exception e) {
            System.out.println("Settings file corrupted or missing, it will be recreated with default settings.");

            settings = Settings.getInstance();
            saveSettings(settings, filename);
        }
        return settings;
    }

    public static void saveSettings(Settings settings, String filename)
    {
        try(FileWriter writer = new FileWriter(filename)) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .excludeFieldsWithModifiers(Modifier.FINAL)
                    .setPrettyPrinting()
                    .create();
            gson.toJson(settings, writer);
        } catch (JsonIOException | IOException e) {
            System.out.println("Errore nel salvataggio del file delle impostazioni. Verrà ripristinato.");
            e.printStackTrace();
        }
    }
    public static void saveSettings(String filename) {
        Settings settings = Settings.getInstance();
        saveSettings(settings, filename);
    }
}
