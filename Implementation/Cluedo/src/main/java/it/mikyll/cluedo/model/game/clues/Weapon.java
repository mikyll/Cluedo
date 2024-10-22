package it.mikyll.cluedo.model.game.clues;

public class Weapon extends Clue {
    public Weapon(String name) {
        super(name, ClueType.WEAPON);
    }

    public String toString()
    {
        // TODO
        return "Name: " + this.getName();
    }
}
