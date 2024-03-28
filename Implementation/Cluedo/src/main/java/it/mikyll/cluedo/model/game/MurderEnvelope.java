package it.mikyll.cluedo.model.game;

import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;

import java.util.Random;

public class MurderEnvelope {
    private final Character murderer;
    private final Weapon weapon;
    private final Room crimeScene;

    public MurderEnvelope()
    {
        Random random = new Random();

        this.murderer = Character.values()[random.nextInt(Character.values().length)];
        this.weapon = Weapon.values()[random.nextInt(Weapon.values().length)];
        this.crimeScene = Room.values()[random.nextInt(Room.values().length)];
    }

    public MurderEnvelope(Character murderer, Weapon weapon, Room crimeScene)
    {
        this.murderer = murderer;
        this.weapon = weapon;
        this.crimeScene = crimeScene;
    }

    public boolean makeAccusation(Character murderer, Weapon weapon, Room crimeScene)
    {
        return this.murderer.equals(murderer)
                && this.weapon.equals(weapon)
                && this.crimeScene.equals(crimeScene);
    }

    public String toString()
    {
        return "The murderer is " + this.murderer + ", who killed Samuel Black with a " + this.weapon + " in " + this.crimeScene;
    }
}
