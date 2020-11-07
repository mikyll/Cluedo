package model.partita;

import javafx.scene.image.Image;

public class Personaggio {
	private String nome;
	private Colore colore;
	private String descrizione;
	private Image immaginePersonaggio;
	private Image immagineColore;
	
	private final String dirPersonaggi = "/resources/images/characters/";
	private final String dirColori = "/resources/images/colors/";
	
	public Personaggio(String nome, Colore colore, String descrizione, String URLImmagine, String URLColore)
	{
		this.nome = nome;
		this.colore = colore;
		this.descrizione = descrizione;
		this.immaginePersonaggio = new Image(dirPersonaggi + URLImmagine);
		this.immagineColore = new Image(dirColori + URLColore);
	}

	public String getNome() {return nome;}
	public Colore getColore() {return colore;}
	public String getColoreEng() 
	{
		if(colore.equals(Colore.ROSSO))
			return "red";
		else if(colore.equals(Colore.VIOLA))
			return "purple";
		else if(colore.equals(Colore.GIALLO))
			return "yellow";
		else if(colore.equals(Colore.VERDE))
			return "green";
		else if(colore.equals(Colore.BIANCO))
			return "white";
		else if(colore.equals(Colore.BLU))
			return "blue";
		return null;
	}
	public String getDescrizione() {return descrizione;}
	public Image getImmaginePersonaggio() {return immaginePersonaggio;}
	public Image getImmagineColore() {return this.immagineColore;}
	
	
}
