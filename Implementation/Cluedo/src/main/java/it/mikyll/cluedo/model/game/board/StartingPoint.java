package it.mikyll.cluedo.model.game.board;

/*
 * Square where the pawn of a character starts the game.
 */
public class StartingPoint {
    private int[] position;
    // Id of the character (see characters.json)
    private String character;

    public StartingPoint(int[] position, String character) {
        this.position = position;
        this.character = character;
    }

    public int[] getPosition() {return position;}
    public void setPosition(int[] position) {this.position = position;}
    public String getCharacter() {return character;}
    public void setCharacter(String character) {this.character = character;}
}
