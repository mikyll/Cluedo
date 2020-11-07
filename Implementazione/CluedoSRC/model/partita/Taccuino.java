package model.partita;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Taccuino {
	private Map<CartaIndizio, Boolean> taccuino;
	
	public Taccuino(List<CartaIndizio> carteIndizio)
	{
		this.taccuino = new HashMap<CartaIndizio, Boolean>();
		for(CartaIndizio c : carteIndizio)
		{
			this.taccuino.put(c, false);
		}
	}
	
	public Map<CartaIndizio, Boolean> getTaccuino() {return this.taccuino;}
	public void checkCarta(CartaIndizio cartaIndizio) {this.taccuino.put(cartaIndizio, !this.taccuino.get(cartaIndizio));}
}
