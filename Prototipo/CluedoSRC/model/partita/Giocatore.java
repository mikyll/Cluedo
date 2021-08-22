package model.partita;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.menu.Utente;

public class Giocatore extends Utente {
	
	private int turno;
	private String bloccoNote;
	private Taccuino taccuino;
	private Personaggio personaggio;
	private List<CartaIndizio> carteIniziali;
	
	private Map<Azione, Boolean> azioniDisponibili;
	private Casella posizioneAttuale;
	private int esitoDadi;
	private boolean dadiLanciati;
	
	// pattern state x ereditarietà multipla (?)
	
	public Giocatore(String username, List<CartaIndizio> carteIndizio)
	{
		super(username);
		this.esitoDadi = -1;
		this.dadiLanciati = false;
		
		// inizializzazione mappa azioni
		this.azioniDisponibili = new HashMap<Azione, Boolean>();
		this.initAzioni();
		
		// inizializzazione Taccuino e BloccoNote
		this.taccuino = new Taccuino(carteIndizio);// (NB: le carteIndizio TOTALI si possono prendere dal Taccuino)
		this.bloccoNote = "";
		
		this.carteIniziali = new ArrayList<CartaIndizio>();
	}
	
	public Map<Azione, Boolean> getAzioniDisponibili() {return this.azioniDisponibili;}
	
	
	
	// getters & setters
	public int getTurno() {return turno;}
	public void setTurno(int turno) {this.turno = turno;}
	public String getBloccoNote() {return bloccoNote;}
	public void setBloccoNote(String bloccoNote) {this.bloccoNote = bloccoNote;}
	public Personaggio getPersonaggio() {return personaggio;}
	public void setPersonaggio(Personaggio personaggio) {this.personaggio = personaggio;}
	public Casella getPosizioneAttuale() {return posizioneAttuale;}
	public void setPosizioneAttuale(Casella posizioneAttuale) {this.posizioneAttuale = posizioneAttuale;}
	public Taccuino getTaccuino() {return taccuino;}
	public List<CartaIndizio> getCarteIniziali() {return this.carteIniziali;}
	public void setCarteIniziali(List<CartaIndizio> carteIniziali) {this.carteIniziali = carteIniziali;}
	public List<String> getCarteInizialiAsString() 
	{
		List<String> result = new ArrayList<String>();
		for(CartaIndizio c : this.carteIniziali)
			result.add(c.toString());
		return result;
	}
	public int getEsitoDadi() {return esitoDadi;}	
	public void setEsitoDadi(int esitoDadi) {this.esitoDadi = esitoDadi;}
	public boolean isDadiLanciati() {return this.dadiLanciati;}	
	public void setDadiLanciati(boolean dadiLanciati) {this.dadiLanciati = dadiLanciati;}
	

	// metodo per aggiornare azioniDisponibili (get?)
	

	private void initAzioni()
	{
		this.azioniDisponibili.put(Azione.BLOCCONOTE, true);
		this.azioniDisponibili.put(Azione.TACCUINO, true);
		this.azioniDisponibili.put(Azione.MOVIMENTO, false);
		this.azioniDisponibili.put(Azione.INDAGINE, false);
		this.azioniDisponibili.put(Azione.ACCUSA, false);
	}
}
