package it.mikyll.cluedo.model.settings;

import it.mikyll.cluedo.model.localization.Language;

public class Settings {
    public static String RESOURCES_PATH = "/it/mikyll/cluedo/";

    private static Settings instance = null;

    private boolean fullscreen = false;
    private double windowWidth = 1060.0;
    private double windowHeight = 600.0;
    private Language language = Language.ENGLISH;
    private boolean musicEnabled = false;
    private double musicVolume = 50.0;
    private boolean soundEffectsEnabled = false;
    private double soundEffectsVolume = 50.0;

    private Settings() {}

    public static synchronized Settings getInstance()
    {
        if (instance == null)
        {
            instance = new Settings();
        }
        return instance;
    }

    public void setValues(Settings settings)
    {
        instance = settings;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
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

    public void setWindowSize(double windowWidth, double windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
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
