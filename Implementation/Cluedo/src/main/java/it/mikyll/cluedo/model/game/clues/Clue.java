package it.mikyll.cluedo.model.game.clues;

import java.util.List;

public abstract class Clue {
	private String name;
    private ClueType type;

    public Clue(String name, ClueType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClueType getType() {
        return type;
    }

    public void setType(ClueType type) {
        this.type = type;
    }

    public boolean equals(Clue other)
    {
        return this.type.equals(other.getType())
                && this.name.equalsIgnoreCase(other.getName());
    }
}
