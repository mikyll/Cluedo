package it.mikyll.cluedo.model.game.clues;

import java.util.Locale;
import java.util.Objects;

public abstract class Clue {
	private String name;
    private ClueType type;

    public Clue(ClueType type) {
        this.type = type;
    }

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

    // Two clues are the same card if they have the same type and name (case insensitive).
    // NB: this must override equals(Object), otherwise List.remove/contains would compare references.
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof Clue))
            return false;
        Clue other = (Clue) o;
        return this.type == other.getType()
                && this.name != null
                && this.name.equalsIgnoreCase(other.getName());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, name == null ? null : name.toLowerCase(Locale.ROOT));
    }
}
