package it.mikyll.cluedo.model.game.clues;

public class Character extends Clue {
    private String id;
    private String description;
    private String color;

    public Character(String id, String name, String description, String color) {
        super(name, ClueType.CHARACTER);
        this.id = id;
        this.description = description;
        this.color = color;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public String toString()
    {
        return "Name: " + this.getName() + "\n" +
                "Description: " + this.description + "\n" +
                "Color: " + this.color;
    }
}
