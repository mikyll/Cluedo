package it.mikyll.cluedo.model.game;

import it.mikyll.cluedo.model.game.clues.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MurderEnvelope {
    private Clue murderer;
    private Clue weapon;
    private Clue room;

    /*
     * Picks one random clue per type and removes them from the list.
     */
    public MurderEnvelope(List<Clue> clueList, Random random)
    {
        Collections.shuffle(clueList, random);

        for (Clue c : clueList)
        {
            if (murderer == null && c.getType().equals(ClueType.CHARACTER))
                this.murderer = c;
            if (weapon == null && c.getType().equals(ClueType.WEAPON))
                this.weapon = c;
            if (room == null && c.getType().equals(ClueType.ROOM))
                this.room = c;
        }
        if (murderer == null || weapon == null || room == null)
            throw new IllegalArgumentException("The clue list must contain at least one character, one weapon and one room");

        clueList.remove(this.murderer);
        clueList.remove(this.weapon);
        clueList.remove(this.room);
    }

    public MurderEnvelope(Clue murderer, Clue weapon, Clue room)
    {
        this.murderer = murderer;
        this.weapon = weapon;
        this.room = room;
    }

    public Clue getMurderer() {return murderer;}
    public Clue getWeapon() {return weapon;}
    public Clue getRoom() {return room;}

    public boolean makeAccusation(Clue murderer, Clue weapon, Clue room)
    {
        return this.murderer.equals(murderer)
                && this.weapon.equals(weapon)
                && this.room.equals(room);
    }

    public String toString()
    {
        return "The murderer is " + this.murderer.getName() + ", who killed Samuel Black with a " + this.weapon.getName() + " in " + this.room.getName();
    }
}
