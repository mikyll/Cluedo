package it.mikyll.cluedo.model.game.clues;

import javafx.scene.paint.Color;

/*
 * WHO
 */
public enum Character implements Clue {
	//NONE("", null),
	PLUM("Victor Plum", Color.PURPLE),
	WHITE("Diane White", Color.WHITE),
	SCARLET("Kassandra Scarlet", Color.RED),
	GREEN("Jacob Green", Color.GREEN),
	MUSTARD("Jack Mustard", Color.YELLOW),
	PEACOCK("Eleanor Peacock", Color.BLUE);
	
	private String name;
	private Color color;
	
	Character(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	public Color getColor() {
		return this.color;
	}
	public String getColorHEX() {
		return "#" + this.color.toString().substring(2, 8);
	}
	public String getLastName() {
		return this.name.split(" ")[1];
	}
}
