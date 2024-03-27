package it.mikyll.cluedo.model.localization;

import java.util.Map;

public class TextStrings {
    private static TextStrings instance = null;

    private Map<Language, String> strings;

    private TextStrings()
    {
        // load from file
    }

    public static synchronized TextStrings getInstance()
    {
        if (instance == null)
        {
            instance = new TextStrings();
        }
        return instance;
    }
}
