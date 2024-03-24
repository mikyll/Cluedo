package it.mikyll.cluedo.persistence;

import it.mikyll.cluedo.model.settings.Settings;

public class SettingsRepository {

    public static Settings loadSettings()
    {
        // TODO

        Settings sett = Settings.getInstance();
        return sett;
    }
    public static void saveSettings(Settings settings)
    {
        // TODO
    }
}
