package model.partita;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// SINGLETON
public class Partita {
	private static Partita INSTANCE; // istanza partita (una sola istanza per Applicazione: Pattern Singleton)
	
	private int numGiocatori;
	
	private Map<Casella, Giocatore> mappa;
	private List<Giocatore> giocatori;
	private List<Personaggio> listPersonaggiTotali;
	private List<Personaggio> listaPersonaggiDisponibili;
	private List<CartaIndizio> carteIndizioTotali;
	private List<CartaIndizio> carteInizialiTotali; // CarteIndizio totali - TernaDelDelitto
	private TernaDelDelitto ternaDelDelitto;
	private int turnoCorrente;
	
	// ======================================================
	// ======================================================
	// ROBA LORENZO
	public String[] oldPos; // 0 - pedinaBlu    1 - pedinaVerde    2 - pedinaBianca    3 - pedinaGialla    4 - pedinaRossa    5 - pedinaViola
	public int[][] oldXY; // NON NECESSARIA (basta ricavare la corrente tramite i getX getY
	public int turno = 0;
	
	// ======================================================
	// ======================================================

	// Costruttore PRIVATO partita in GiocatoreSingolo
	private Partita(int numeroGiocatori, Difficolta difficolta)
	{
		this.numGiocatori = numeroGiocatori;
		giocatori = new ArrayList<Giocatore>(numeroGiocatori);
		
		// inizializzazione carteIndizioTotali (bisogna passarle ai giocatori per creare i vari Taccuini
		this.initCarteIndizioTotali();
		
		// inizilizzazione Giocatori (alla creazione hanno username e strumenti inizializzati, ma non hanno turno, personaggio né carteIniziali)
		giocatori = new ArrayList<Giocatore>(numeroGiocatori);
		giocatori.add(new Giocatore("Giocatore", carteIndizioTotali));	// aggiungo l'utente che gioca
		for(int i = 0; i < numeroGiocatori - 1; i++)
			giocatori.add(new AvversarioVirtuale("Avversario" + i, carteIndizioTotali, difficolta));	// aggiungo gli avversari virtuali
		
		
		// init
		this.assegnazioneTurni(); // vengono assegnati i vari turni ai Giocatori (mescolando la lista)
		this.initPersonaggi(); // vengono popolate le liste dei Personaggi
		
		// =============================================================
		// ROBA LORENZO
		
		// =============================================================
		
		initMappa();
		
		
		// TEST
		/*System.out.println(this.getTurnoGiocatore(this.getGiocatoreByUsername("Giocatore")));
		for(Giocatore g : this.giocatori)
			System.out.println("Nome: " + g.getUsername() + ", turno: " + g.getTurno());*/
	}
	// Pattern Singleton: metodo per ottenere l'istanza della Partita
	public static Partita getInstance(int numeroGiocatori, Difficolta difficolta)
	{
		if(Partita.INSTANCE == null)
		{
			// nuova partita
			Partita.INSTANCE = new Partita(numeroGiocatori, difficolta);
		}
		return Partita.INSTANCE;
	}
	public static void deleteInstance()
	{
		Partita.INSTANCE = null;
	}
	
	
	
	
	
	// getter & setter
	public Map<Casella, Giocatore> getMappa() {return mappa;}
	public List<Giocatore> getGiocatori() {return giocatori;}
	
	public List<Personaggio> getListaPersonaggiTotali() {return this.listPersonaggiTotali;}
	//public void setListaPersonaggiTotali(List<Personaggio> listaPersonaggi) {this.listaPersonaggiTotali = listaPersonaggi;}
	public List<Personaggio> getListaPersonaggiDisponibili() {return this.listaPersonaggiDisponibili;}
	//public void setListaPersonaggi(List<Personaggio> listaPersonaggi) {this.listaPersonaggiDisponibili = listaPersonaggi;}
	
	public List<CartaIndizio> getCarteIndizioTotali() {
		return carteIndizioTotali;
	}
	public void setCarteIndizioTotali(List<CartaIndizio> carteIndizioTotali) {
		this.carteIndizioTotali = carteIndizioTotali;
	}
	
	public TernaDelDelitto getTernaDelDelitto() {return this.ternaDelDelitto;}
	
	public int getTurnoCorrente() {return this.turnoCorrente;}
	public void setTurnoCorrente(int turnoCorrente) {this.turnoCorrente = turnoCorrente;}
	
	// utilities
	
	public Giocatore getGiocatoreByUsername(String nome)
	{
		for(Giocatore g : this.giocatori)
			if(g.getUsername().equalsIgnoreCase(nome))
				return g;
		System.out.println("ERRORE: giocatore non trovato");
		return null;
	}
	public int getTurnoGiocatore(Giocatore giocatore) {return this.giocatori.indexOf(giocatore) + 1;}
	//public int getTurnoGiocatore(String giocatore) {return this.giocatori.indexOf(giocatore) + 1;}
	
	public void incrementaTurno()
	{
		if(this.turnoCorrente == this.numGiocatori)
			this.turnoCorrente = 1;
		else this.turnoCorrente++;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// INIZIALIZZAZIONE
	private void assegnazioneTurni()
	{
		Collections.shuffle(this.giocatori);
		
		for(int i = 0; i < this.numGiocatori; i++)
			this.giocatori.get(i).setTurno(i + 1);
	}
	private void initPersonaggi() // poi sarà da cambiare inserendo il parser
	{
		this.listPersonaggiTotali = new ArrayList<Personaggio>(6);
		this.listPersonaggiTotali.add(new Personaggio("Kassandra Scarlet", Colore.ROSSO, "Donna seducente sempre al centro del gossip", "pg0 - Kassandra Scarlet (ROSSO).png", "colore-rosso.png"));
		this.listPersonaggiTotali.add(new Personaggio("Victor Plum", Colore.VIOLA, "Giovane informatico sviluppatore di videogames", "pg1 - Victor Plum (VIOLA).png", "colore-viola.png"));
		this.listPersonaggiTotali.add(new Personaggio("Jack Mustard", Colore.GIALLO, "Telecronista con un glorioso passato da calciatore", "pg2 - Jack Mustard (GIALLO).png", "colore-giallo.png"));
		this.listPersonaggiTotali.add(new Personaggio("Jacob Green", Colore.VERDE, "Uomo molto influente ma con il quale non è prudente restare in debito", "pg3 - Jacob Green (VERDE).png", "colore-verde.png"));
		this.listPersonaggiTotali.add(new Personaggio("Diane White", Colore.BIANCO, "Donna con infanzia d'attrice che cerca sempre di attirare l'attenzione", "pg4 - Diane White (BIANCO).png", "colore-bianco.png"));
		this.listPersonaggiTotali.add(new Personaggio("Eleanor Peacock", Colore.BLU, "Discendente di un'agiata famiglia di politici, fissata con la perfezione", "pg5 - Eleanor Peacock (BLU).png", "colore-blu.png"));
		
		this.listaPersonaggiDisponibili = new ArrayList<Personaggio>(6);
		this.listaPersonaggiDisponibili.add(new Personaggio("Kassandra Scarlet", Colore.ROSSO, "Donna seducente sempre al centro del gossip", "pg0 - Kassandra Scarlet (ROSSO).png", "colore-rosso.png"));
		this.listaPersonaggiDisponibili.add(new Personaggio("Victor Plum", Colore.VIOLA, "Giovane informatico sviluppatore di videogames", "pg1 - Victor Plum (VIOLA).png", "colore-viola.png"));
		this.listaPersonaggiDisponibili.add(new Personaggio("Jack Mustard", Colore.GIALLO, "Telecronista con un glorioso passato da calciatore", "pg2 - Jack Mustard (GIALLO).png", "colore-giallo.png"));
		this.listaPersonaggiDisponibili.add(new Personaggio("Jacob Green", Colore.VERDE, "Uomo molto influente ma con il quale non è prudente restare in debito", "pg3 - Jacob Green (VERDE).png", "colore-verde.png"));
		this.listaPersonaggiDisponibili.add(new Personaggio("Diane White", Colore.BIANCO, "Donna con infanzia d'attrice che cerca sempre di attirare l'attenzione", "pg4 - Diane White (BIANCO).png", "colore-bianco.png"));
		this.listaPersonaggiDisponibili.add(new Personaggio("Eleanor Peacock", Colore.BLU, "Discendente di un'agiata famiglia di politici, fissata con la perfezione", "pg5 - Eleanor Peacock (BLU).png", "colore-blu.png"));
	}
	private void initMappa()
	{
		mappa = new HashMap<Casella, Giocatore>();
		for(Casella c : mappa.keySet())
			mappa.put(c, null);
	}
	private void initCarteIndizioTotali()
	{
		int numCarte;
		if(this.numGiocatori == 4 || this.numGiocatori == 5)
			numCarte = 23;
		else numCarte = 21;
		
		carteIndizioTotali = new ArrayList<CartaIndizio>(numCarte);
		
		carteIndizioTotali.add(new CartaIndizio("Cucina", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Ingresso", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Libreria", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Sala da Ballo", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Sala da Pranzo", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Sala Giochi", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Salotto", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Serra", TipoCarta.STANZA));
		carteIndizioTotali.add(new CartaIndizio("Studio", TipoCarta.STANZA));
		
		carteIndizioTotali.add(new CartaIndizio("Candeliere", TipoCarta.ARMA));
		carteIndizioTotali.add(new CartaIndizio("Chiave inglese", TipoCarta.ARMA));
		carteIndizioTotali.add(new CartaIndizio("Corda", TipoCarta.ARMA));
		carteIndizioTotali.add(new CartaIndizio("Piede di porco", TipoCarta.ARMA));
		carteIndizioTotali.add(new CartaIndizio("Pistola", TipoCarta.ARMA));
		carteIndizioTotali.add(new CartaIndizio("Pugnale", TipoCarta.ARMA));
		if(numCarte == 23)
			carteIndizioTotali.add(new CartaIndizio("Veleno", TipoCarta.ARMA));
		
		carteIndizioTotali.add(new CartaIndizio("Kassandra Scarlet", TipoCarta.SOSPETTATO));
		carteIndizioTotali.add(new CartaIndizio("Victor Plum", TipoCarta.SOSPETTATO));
		carteIndizioTotali.add(new CartaIndizio("Jack Mustard", TipoCarta.SOSPETTATO));
		carteIndizioTotali.add(new CartaIndizio("Jacob Green", TipoCarta.SOSPETTATO));
		carteIndizioTotali.add(new CartaIndizio("Diane White", TipoCarta.SOSPETTATO));
		carteIndizioTotali.add(new CartaIndizio("Eleanor Peacock", TipoCarta.SOSPETTATO));
		if(numCarte == 23)
			carteIndizioTotali.add(new CartaIndizio("Dottoressa Orchid", TipoCarta.SOSPETTATO));
	}
	// estrae la terna del delitto dalle CarteIndizioTotali e aggiunge le rimanenti ad una nuova lista (CarteInizialiTotali)
	public void estraiTernaDelDelitto()
	{
		if(this.ternaDelDelitto != null)
			return;
		Collections.shuffle(carteIndizioTotali);
		
		carteInizialiTotali = new ArrayList<CartaIndizio>();
		CartaIndizio stanza = null, arma = null, sospettato = null;
		
		for(int i = 0; i < carteIndizioTotali.size(); i++)
		{
			if(stanza == null && carteIndizioTotali.get(i).getTipo().equals(TipoCarta.STANZA))
			{
				stanza = carteIndizioTotali.get(i);
				continue;
			}
			if(arma == null && carteIndizioTotali.get(i).getTipo().equals(TipoCarta.ARMA))
			{
				arma = carteIndizioTotali.get(i);
				continue;
			}
			if(sospettato == null && carteIndizioTotali.get(i).getTipo().equals(TipoCarta.SOSPETTATO))
			{
				sospettato = carteIndizioTotali.get(i);
				continue;
			}
			carteInizialiTotali.add(carteIndizioTotali.get(i));
		}
		
		ternaDelDelitto = new TernaDelDelitto(stanza, arma, sospettato);
	}
	public void assegnaCarteIniziali()
	{
		Collections.shuffle(this.carteInizialiTotali);
		
		int num_carte = (this.carteInizialiTotali.size() / this.numGiocatori);
		int i = 0;
		for(Giocatore g : this.giocatori)
		{
			List<CartaIndizio> c = new ArrayList<CartaIndizio>(num_carte);
			for(int j = 0; j < num_carte && i < this.carteInizialiTotali.size(); j++, i++)
			{
				c.add(this.carteInizialiTotali.get(i));
			}
			g.setCarteIniziali(c);
		}
		
		
		/*// TEST
		System.out.println("Size carteTOT: " + this.carteIndizioTotali.size());
		System.out.println("Size terna: " + this.ternaDelDelitto.getTerna().size());
		System.out.println("Size carteINI: " + this.carteInizialiTotali.size());
		for(Giocatore g : this.giocatori)
			System.out.println(g.getCarteIniziali().size());
		for(Giocatore g : this.giocatori)
		{
			System.out.print(g.getUsername() + " carte: ");
			for(CartaIndizio c : g.getCarteIniziali())
				System.out.print(c.getDescrizione() + ", ");
			System.out.print("\n");
		}*/
		
	}
}
