package model.partita;

import java.util.List;

public class AvversarioVirtuale extends Giocatore {

	private Difficolta difficolta;
	
	public AvversarioVirtuale(String username, List<CartaIndizio> carteIndizio, Difficolta difficolta) 
	{
		super(username, carteIndizio);
		this.difficolta = difficolta;
	}

	public Difficolta getDifficolta() {
		return difficolta;
	}

	public void setDifficolta(Difficolta difficolta) {
		this.difficolta = difficolta;
	}
	
	
}
