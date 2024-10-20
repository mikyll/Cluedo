package it.mikyll.cluedo.model.settings;

import it.mikyll.cluedo.model.localization.Language;

public class Settings {
    public final static String APP_TITLE = "Clueless";
    public final static String APP_VERSION = "v1.0-alpha";
    public final static String RESOURCES_PATH = "/it/mikyll/cluedo/";

    private static Settings instance = null;

    private transient double windowWidth = 1060.0;
    private transient double windowHeight = 600.0;

    // Persistent fields
    private boolean chatEnabled = true;
    private Language language = Language.ENGLISH;
    private boolean musicEnabled = false;
    private double musicVolume = 50.0;
    private boolean soundEffectsEnabled = false;
    private double soundEffectsVolume = 50.0;
    private String timestampFormat = ""; // TODO

    private Settings() {}

    public static synchronized Settings getInstance()
    {
        if (instance == null)
        {
            instance = new Settings();
        }
        return instance;
    }

    public static synchronized void setInstance(Settings settings)
    {
        instance = settings;
    }

    public void setValues(Settings settings)
    {
        instance = settings;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean value) {
        musicEnabled = value;
    }

    public double getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(double value) {
        musicVolume = value;
    }

    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }

    public void setSoundEffectsEnabled(boolean value) {
        soundEffectsEnabled = value;
    }

    public double getSoundEffectsVolume() {
        return soundEffectsVolume;
    }

    public void setSoundEffectsVolume(double value) {
        soundEffectsVolume = value;
    }
}

