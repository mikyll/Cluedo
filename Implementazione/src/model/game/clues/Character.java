package model.game.clues;

/*
 * WHO
 */
public enum Character implements Clue {
	NONE(""),
	PURPLE("Victor Plum"),
	WHITE("Diane White"),
	RED("Kassandra Scarlet"),
	GREEN("Jacob Green"),
	YELLOW("Jack Mustard"),
	BLUE("Eleanor Peacock");
	
	private String name;
	
	Character(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
