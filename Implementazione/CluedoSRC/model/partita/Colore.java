package model.partita;

public enum Colore {
	ROSSO ("/resources/images/colors/colore-rosso.png"), 
	VIOLA ("/resources/images/colors/colore-viola.png"), 
	GIALLO ("/resources/images/colors/colore-giallo.png"), 
	VERDE ("/resources/images/colors/colore-verde.png"), 
	BIANCO ("/resources/images/colors/colore-bianco.png"), 
	BLU ("/resources/images/colors/colore-blu.png");

	private final String url;
	
	Colore(String url) 
	{
		this.url = url;
	}

	public String getUrl() {return url;}
}
