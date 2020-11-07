package model.partita;

import java.util.ArrayList;
import java.util.List;

public class TernaDelDelitto {
		
	private List<CartaIndizio> terna;
	
	public TernaDelDelitto(CartaIndizio stanza, CartaIndizio arma, CartaIndizio sospettato) throws IllegalArgumentException
	{
		if(!stanza.getTipo().equals(TipoCarta.STANZA) || !arma.getTipo().equals(TipoCarta.ARMA) || !sospettato.getTipo().equals(TipoCarta.SOSPETTATO))
			throw new IllegalArgumentException("terna non valida");
		
		this.terna = new ArrayList<CartaIndizio>(3);
		this.terna.add(stanza);
		this.terna.add(arma);
		this.terna.add(sospettato);
	}

	public List<CartaIndizio> getTerna() {return terna;}
}
