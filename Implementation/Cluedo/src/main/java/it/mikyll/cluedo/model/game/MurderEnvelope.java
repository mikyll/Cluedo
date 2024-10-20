package it.mikyll.cluedo.model.game;

import it.mikyll.cluedo.model.game.clues.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MurderEnvelope {
    private Clue murderer;
    private Clue weapon;
    private Clue room;

    public MurderEnvelope(List<Clue> clueList)
    {
        Collections.shuffle(clueList);

        for (Clue c : clueList)
        {
            if (murderer == null && c.getType().equals(ClueType.CHARACTER))
                this.murderer = c;
            if (weapon == null && c.getType().equals(ClueType.WEAPON))
                this.weapon = c;
            if (room == null && c.getType().equals(ClueType.ROOM))
                this.room = c;
        }
    }

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
