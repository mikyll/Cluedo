package it.mikyll.cluedo.model.game.clues;

/*
 * WHO
 */
public enum Character implements Clue {
	NONE(""),
	PLUM("Victor Plum"),
	WHITE("Diane White"),
	SCARLET("Kassandra Scarlet"),
	GREEN("Jacob Green"),
	MUSTARD("Jack Mustard"),
	PEACOCK("Eleanor Peacock");
	
	private String name;
	
	Character(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
