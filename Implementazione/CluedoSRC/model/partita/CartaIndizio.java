package model.partita;

public class CartaIndizio {
	private String descrizione;
	private TipoCarta tipo;
	
	public CartaIndizio(String descrizione, TipoCarta tipo)
	{
		this.tipo = tipo;
		this.descrizione = descrizione;
	}
	
	public String getDescrizione() {return descrizione;}
	public TipoCarta getTipo() {return tipo;}
	
	public String toString() {return "[" + this.tipo.toString() + "] " + this.descrizione;}
}
