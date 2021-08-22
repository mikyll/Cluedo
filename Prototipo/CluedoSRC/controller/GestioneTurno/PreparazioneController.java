package controller.GestioneTurno;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.partita.Giocatore;
import model.partita.Partita;
import model.partita.Personaggio;

public class PreparazioneController {
	
	private VBox vboxBase;
	private ListView<ImageView> listViewColori;
	
	@FXML private VBox vboxPreparazione;
	
	@FXML private TextField textFieldTurno;
	@FXML private ImageView imageViewPersonaggio;
	@FXML private Button buttonLeft;	// <-
	@FXML private Button buttonRight;	// ->
	@FXML private TextField textFieldPersonaggio;
	@FXML private TextField textFieldDescrizione;
	@FXML private Button buttonConfermaPersonaggio;
	
	@FXML private ListView<String> listViewCarteIniziali;
	
	private Partita partita;
	private Giocatore giocatore;
	private List<Personaggio> listaPersonaggi;
	private int counterPersonaggi = 0;
	
	public PreparazioneController() {}
	public void initialize() 
	{
		// NB: this.giocatore e this.listaPersonaggi SONO RIFERIMENTI! Quindi aggiornano anche quello in Partita.ISTANZA
		this.partita = Partita.getInstance(0, null);
		this.giocatore = this.partita.getGiocatoreByUsername("Giocatore");
		this.listaPersonaggi = this.partita.getListaPersonaggiDisponibili();
		
		// aggiorna il turno del giocatore nella ViewPreparazione
		this.textFieldTurno.setText(String.valueOf(this.partita.getTurnoGiocatore(this.giocatore)));
		
		// ========================================
		// preparazione AvversariVirtuali PRIMA (NON DEVONO SCEGLIERE IL BLU)
		int turnoGiocatore = this.giocatore.getTurno();
		for(int i = 0; i < turnoGiocatore - 1; i++)
		{
			Giocatore g = partita.getGiocatori().get(i);
			
			int pIndex;
			for(;;)
			{
				pIndex = ThreadLocalRandom.current().nextInt(0, partita.getListaPersonaggiDisponibili().size());
				if(partita.getListaPersonaggiDisponibili().get(pIndex).getColore().toString().equals("BLU"))
				{
					//System.out.println("NON PUO' SCEGLIERE IL BLU!!!");
					continue;
				}
				else
				{
					break;
				}
			}
			//int pIndex = ThreadLocalRandom.current().nextInt(0, p.getListaPersonaggiDisponibili().size());
			g.setPersonaggio(partita.getListaPersonaggiDisponibili().get(pIndex));
			partita.getListaPersonaggiDisponibili().remove(pIndex);
			System.out.println("[IA] " + g.getUsername() + " ha selezionato il personaggio " + g.getPersonaggio().getNome());
		}
		// ========================================
		
		// mostra il primo personaggio selezionabile
		this.imageViewPersonaggio.setImage(this.listaPersonaggi.get(this.counterPersonaggi).getImmaginePersonaggio());
		this.textFieldPersonaggio.setText(this.listaPersonaggi.get(this.counterPersonaggi).getNome());
		this.textFieldPersonaggio.setStyle("-fx-background-color: black; -fx-text-inner-color: white; -fx-border-width: 2px;"
				+ "-fx-border-color: " + this.listaPersonaggi.get(this.counterPersonaggi).getColoreEng() + ";");
		this.textFieldDescrizione.setText(this.listaPersonaggi.get(this.counterPersonaggi).getDescrizione());
	}
	
	@FXML public void scorriASinistra(ActionEvent event)
	{
		System.out.println("<-");
		
		if(this.counterPersonaggi > 0)
			this.counterPersonaggi--;
		else if(this.counterPersonaggi == 0)
			this.counterPersonaggi = this.listaPersonaggi.size() - 1;
		
		this.imageViewPersonaggio.setImage(this.listaPersonaggi.get(this.counterPersonaggi).getImmaginePersonaggio());
		this.textFieldPersonaggio.setText(this.listaPersonaggi.get(this.counterPersonaggi).getNome());
		this.textFieldPersonaggio.setStyle("-fx-background-color: black; -fx-text-inner-color: white; -fx-border-width: 2px;"
				+ "-fx-border-color: " + this.listaPersonaggi.get(this.counterPersonaggi).getColoreEng() + ";");
		this.textFieldDescrizione.setText(this.listaPersonaggi.get(this.counterPersonaggi).getDescrizione());
	}
	@FXML public void scorriADestra(ActionEvent event)
	{
		System.out.println("->");
		
		if(this.counterPersonaggi < this.listaPersonaggi.size() - 1)
			this.counterPersonaggi++;
		else if(this.counterPersonaggi == this.listaPersonaggi.size() - 1)
			this.counterPersonaggi = 0;
		
		this.imageViewPersonaggio.setImage(this.listaPersonaggi.get(this.counterPersonaggi).getImmaginePersonaggio());
		this.textFieldPersonaggio.setText(this.listaPersonaggi.get(this.counterPersonaggi).getNome());
		this.textFieldPersonaggio.setStyle("-fx-background-color: black; -fx-text-inner-color: white; -fx-border-width: 2px;"
				+ "-fx-border-color: " + this.listaPersonaggi.get(this.counterPersonaggi).getColoreEng() + ";");
		this.textFieldDescrizione.setText(this.listaPersonaggi.get(this.counterPersonaggi).getDescrizione());
	}
	@FXML public void confermaPersonaggio(ActionEvent event)
	{
		System.out.println("[Giocatore] " + this.giocatore.getUsername() + " ha selezionato il personaggio " + this.listaPersonaggi.get(this.counterPersonaggi).getNome());
		this.buttonConfermaPersonaggio.setDisable(true);
		this.buttonLeft.setDisable(true);
		this.buttonRight.setDisable(true);
		
		// ricavo la textFieldUsername
		this.vboxBase = (VBox) ((HBox) ((VBox) this.vboxPreparazione.getParent()).getParent()).getParent();
		//TextField textFieldUsername = (TextField) ((HBox) ((VBox) ((HBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getChildren().get(2);
		
		// imposta il proprio personaggio nel proprio oggetto Giocatore e lo rimuove dalla lista dei disponibili
		this.giocatore.setPersonaggio(this.partita.getListaPersonaggiDisponibili().get(this.counterPersonaggi));
		this.listaPersonaggi.remove(this.counterPersonaggi);
		
		// ottengo i componenti da aggiornare nella ViewGioco (essendo associata al controllerPartita non ci posso arrivare da qui)
		HBox hboxDatiGiocatore = ((HBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(0));
		TextField textFieldPersonaggioGiocatore = (TextField) ((HBox) ((VBox) hboxDatiGiocatore.getChildren().get(0)).getChildren().get(1)).getChildren().get(2);
		TextField textFieldTurnoGiocatore = (TextField) ((HBox) ((VBox) hboxDatiGiocatore.getChildren().get(0)).getChildren().get(2)).getChildren().get(2);
		TextField textFieldColoreGiocatore = (TextField) ((HBox) ((VBox) hboxDatiGiocatore.getChildren().get(1)).getChildren().get(0)).getChildren().get(1);
		ImageView imageViewColoreGiocatore = (ImageView) ((HBox) ((VBox) hboxDatiGiocatore.getChildren().get(1)).getChildren().get(0)).getChildren().get(2);
		ImageView imageViewPersonaggioGiocatore = (ImageView) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(1);
		
		// aggiorno i dati del giocatore nella view
		textFieldPersonaggioGiocatore.setText(this.giocatore.getPersonaggio().getNome());
		textFieldTurnoGiocatore.setText(String.valueOf(this.giocatore.getTurno()));
		textFieldColoreGiocatore.setText(this.giocatore.getPersonaggio().getColore().toString());
		imageViewColoreGiocatore.setImage(this.giocatore.getPersonaggio().getImmagineColore());
		imageViewPersonaggioGiocatore.setImage(this.giocatore.getPersonaggio().getImmaginePersonaggio());
		
		
		// ========================================
		// preparazione AvversariVirtuali DOPO
		int turnoGiocatore = this.giocatore.getTurno();
		for(int i = turnoGiocatore; i < partita.getGiocatori().size(); i++)
		{
			Giocatore g = partita.getGiocatori().get(i);
			
			int pIndex = ThreadLocalRandom.current().nextInt(0, partita.getListaPersonaggiDisponibili().size());
			g.setPersonaggio(partita.getListaPersonaggiDisponibili().get(pIndex));
			partita.getListaPersonaggiDisponibili().remove(pIndex);
			System.out.println("[IA] " + g.getUsername() + " ha selezionato il personaggio " + g.getPersonaggio().getNome());
		}
		// ========================================
		
		// selezione terna del delitto
		partita.estraiTernaDelDelitto();
		System.out.println("[Sistema] TernaDelDelitto estratta.");
		
		// assegnazione CarteIniziali + TIMER per aggiornare il taccuino
		partita.assegnaCarteIniziali();
		ComboBox<String> comboBoxCarteIniziali = (ComboBox<String>) ((VBox) ((HBox) this.vboxBase.getChildren().get(1)).getChildren().get(1)).getChildren().get(4);
		this.listViewCarteIniziali.setItems(FXCollections.observableArrayList(this.giocatore.getCarteInizialiAsString()));
		comboBoxCarteIniziali.setDisable(false);
		comboBoxCarteIniziali.setEditable(false);
		comboBoxCarteIniziali.setItems(FXCollections.observableArrayList(this.giocatore.getCarteInizialiAsString()));
		System.out.println("[Sistema] CarteIndizio iniziali assegnate.");
		
		
		// ottengo la listView dei colori dei giocatori
		this.listViewColori = (ListView<ImageView>) ((HBox) ((VBox) ((HBox) this.vboxBase.getChildren().get(1)).getChildren().get(1)).getChildren().get(1)).getChildren().get(0);
		List<ImageView> colori = new ArrayList<ImageView>(partita.getGiocatori().size());
		for(Giocatore g : partita.getGiocatori())
			colori.add(new ImageView(g.getPersonaggio().getImmagineColore()));
		
		listViewColori.setItems(FXCollections.observableArrayList(colori));
		
		
		// Preambolo
		//
		//
		//
		
		
		
		// Posizionamento giocatori e init pedine
		this.partita.oldPos = new String[6];
		this.partita.oldPos[0] = "1-19";
		this.partita.oldPos[1] = "10-25";
		this.partita.oldPos[2] = "15-25";
		this.partita.oldPos[3] = "24-8";
		this.partita.oldPos[4] = "17-1";
		this.partita.oldPos[5] = "1-6";
		
		this.partita.oldXY = new int[6][2];
		this.partita.oldXY[0][0] = 1;
		this.partita.oldXY[0][1] = 19;
		this.partita.oldXY[1][0] = 10;
		this.partita.oldXY[1][1] = 25;
		this.partita.oldXY[2][0] = 15;
		this.partita.oldXY[2][1] = 25;
		this.partita.oldXY[3][0] = 24;
		this.partita.oldXY[3][1] = 8;
		this.partita.oldXY[4][0] = 17;
		this.partita.oldXY[4][1] = 1;
		this.partita.oldXY[5][0] = 1;
		this.partita.oldXY[5][1] = 6;
		
		Pane paneMappa = (Pane) ((HBox) this.vboxBase.getChildren().get(1)).getChildren().get(2);
		//ImageView imageViewMappa = (ImageView) paneMappa.getChildren().get(0);
		ImageView imageViewPedinaBlu = (ImageView) paneMappa.getChildren().get(1);
		ImageView imageViewPedinaVerde = (ImageView) paneMappa.getChildren().get(2);
		ImageView imageViewPedinaBianca = (ImageView) paneMappa.getChildren().get(3);
		ImageView imageViewPedinaRossa = (ImageView) paneMappa.getChildren().get(4);
		ImageView imageViewPedinaGialla = (ImageView) paneMappa.getChildren().get(5);
		ImageView imageViewPedinaViola = (ImageView) paneMappa.getChildren().get(6);
		
		imageViewPedinaBlu.setY((double)18*25);
		
		imageViewPedinaVerde.setX((double)9*26);
		imageViewPedinaVerde.setY((double)24*25);
		
		imageViewPedinaBianca.setX((double)14*26);
		imageViewPedinaBianca.setY((double)24*25);
		
		imageViewPedinaGialla.setX((double)23*26);
		imageViewPedinaGialla.setY((double)7*25);

		imageViewPedinaRossa.setX((double)16*26);
				
		imageViewPedinaViola.setY((double)5*25);
		// abilita onClick sulla ImageView
		
		
		// terminazione partita
		this.terminaPreparazione();
	}
	public void mostraPreambolo()
	{
		
	}
	public void terminaPreparazione()
	{
		Button buttonMovimento = (Button) ((HBox) ((VBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(2)).getChildren().get(0)).getChildren().get(1);
		buttonMovimento.setDisable(false);
		Button buttonIndagine = (Button) ((HBox) ((VBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(2)).getChildren().get(0)).getChildren().get(3);
		buttonIndagine.setDisable(false);
		Button buttonAccusa = (Button) ((HBox) ((VBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(2)).getChildren().get(0)).getChildren().get(5);
		buttonAccusa.setDisable(false);
		Button buttonFineTurno = (Button) ((HBox) ((VBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(2)).getChildren().get(0)).getChildren().get(7);
		buttonFineTurno.setDisable(false);
		// enable Taccuino & BloccoNote
		Button buttonApriTaccuino = (Button) ((HBox) ((VBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(2)).getChildren().get(1)).getChildren().get(1);
		buttonApriTaccuino.setDisable(false);
		Button buttonApriBloccoNote = (Button) ((HBox) ((VBox) ((HBox) this.vboxBase.getChildren().get(2)).getChildren().get(2)).getChildren().get(1)).getChildren().get(3);
		buttonApriBloccoNote.setDisable(false);
		
		// timer
		//
		//
		//
		
		((VBox) this.vboxPreparazione.getParent()).getChildren().remove(1);
	}
}
