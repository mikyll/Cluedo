package controller.GestionePartita;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import _clientApplicazione.ClientApplicazione;
import controller.GestioneTurno.PreparazioneController;
import controller.GestioneTurno.StrumentiController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.impostazioni.Impostazioni;
import model.impostazioni.MusicPlayer;
import model.partita.AvversarioVirtuale;
import model.partita.CartaIndizio;
import model.partita.Difficolta;
import model.partita.Giocatore;
import model.partita.Partita;
import model.partita.TipoCarta;

public class GestionePartitaController implements IGestionePartita{
	// CONTROLLERS
	@FXML private StrumentiController strumentiController;
	
	private Stage stage;
	// TOP
	@FXML private MenuItem menuItemEsci;
	@FXML private MenuItem menuItemFullScreen;
	// CENTER-LEFT
	@FXML private ListView<String> listViewGiocatori;
	@FXML private ListView<ImageView> listViewColori;
	// CENTER_RIGHT
	@FXML private HBox hboxCentrale;
	@FXML private VBox vboxFase;
	
	// BOTTOM
	@FXML private TextField textFieldUsername;
	@FXML private TextField textFieldPersonaggio;
	@FXML private TextField textFieldNumeroTurno;
	@FXML private TextField textFieldColore;
	@FXML private ImageView imageViewColore;
	
	@FXML private Button buttonMovimento;
	@FXML private Button buttonIndagine;
	@FXML private Button buttonAccusa;
	@FXML private Button buttonFineTurno;
	@FXML private Button buttonApriTaccuino;
	@FXML private Button buttonApriBloccoNote;
	
	// BLOCCO NOTE
	@FXML private TextArea textAreaBloccoNote;
	@FXML private Button buttonSalva;
	@FXML private Button buttonChiudiSenzaSalvare;
	
	// =========================================================
	// =================================================
	// ROBA LORENZO
	// CENTER LEFT
	@FXML private VBox vboxLeft;
	@FXML private HBox selezioneSpostamento;
	@FXML private TextField posizione;
	@FXML private Button sposta;
	// CENTER
	@FXML private Pane paneMappa;
	@FXML private ImageView imageViewMappa;
	//@FXML private ListView<ImageView> listViewPedine; 
	// private List<ImageView> listaPedine;
	@FXML private ImageView imageViewPedinaBlu;
	@FXML private ImageView imageViewPedinaVerde;
	@FXML private ImageView imageViewPedinaBianca;
	@FXML private ImageView imageViewPedinaRossa;
	@FXML private ImageView imageViewPedinaGialla;
	@FXML private ImageView imageViewPedinaViola;
	// VALORI DI SUPPORTO CALCOLI
	//private String[] oldPos; // 0 - pedinaBlu    1 - pedinaVerde    2 - pedinaBianca    3 - pedinaGialla    4 - pedinaRossa    5 - pedinaViola
	//private int[][] oldXY; // NON NECESSARIA (basta ricavare la corrente tramite i getX getY
	
	// private int turno = 0;
	
	private int esitoDadi = 0;
	private int counterStudio = 0;
	private int counterIngresso = 0;
	private int counterSalotto = 0;
	private int counterLibreria = 0;
	private int counterSalaDaPranzo = 0;
	private int counterSalaGiochi = 0;
	private int counterSerra = 0;
	private int counterSalaDaBallo = 0;
	private int counterCucina = 0;

	private String coordinateCorrentiMouse;
	private int xMouse;
	private int yMouse;
	
	@FXML private HBox hboxMovimento;
	@FXML private TextField textFieldDadi;
	@FXML private Button buttonLancioDadi;
	@FXML private Button buttonConfermaSpostamento;
	@FXML private ComboBox<String> comboBoxGiocatori;
	@FXML private TextField textFieldStanza;
	@FXML private ComboBox<String> comboBoxArmi;
	@FXML private ComboBox<String> comboBoxSospettati;
	@FXML private Button buttonConfermaIndagine;
	@FXML private TextField textFieldRisultatoIndagine1;
	@FXML private TextField textFieldRisultatoIndagine2;
	@FXML private TextField textFieldRisultatoIndagine3;
	
	@FXML private ComboBox<String> comboBoxAccusaStanze;
	@FXML private ComboBox<String> comboBoxAccusaArmi;
	@FXML private ComboBox<String> comboBoxAccusaSospettati;
	
	@FXML private TextField textFieldRisultatoAccusa;
	
	@FXML private Button buttonConfermaAccusa;
	@FXML private Button buttonEsci;
	private boolean godMode;
	// =================================================
	// =========================================================

	private Impostazioni impostazioni;
	private NumberFormat formatterNoFractionDigits;	// per percentuali (nessun decimale)
	private MusicPlayer musicPlayer;
	
	private Partita partita;
	
	// NB: ci sono due costruttori: uno con parametri, invocato una volta (da MenuController), che inizializza musica ed oggetto Partita
	public GestionePartitaController(Impostazioni impostazioni, Stage stage, int numeroGiocatori, String difficolta)
	{
		
		this.impostazioni = impostazioni;
		this.stage = stage;
		
		this.formatterNoFractionDigits = NumberFormat.getInstance();
		this.formatterNoFractionDigits.setMaximumFractionDigits(0);
		
		String musicTitle = "George Arkomanis - Relaxation In Mystery (Game).mp3";
		this.musicPlayer = MusicPlayer.getIstanza(musicTitle); // NB: ritornerà l'istanza già creata nel metodo "avviaPartita"
		this.musicPlayer.getMediaPlayer().play();
		this.musicPlayer.getMediaPlayer().setVolume(this.impostazioni.getVolumeMusica() / 100);
		this.musicPlayer.getMediaPlayer().setCycleCount(Integer.MAX_VALUE); // per ripetere la traccia ad oltranza
		if(this.impostazioni.isEnabledMusica())
		{
			this.musicPlayer.getMediaPlayer().play();
			System.out.println("Music playing (" + this.formatterNoFractionDigits.format(this.impostazioni.getVolumeMusica()) + "%): " + musicTitle);
		}
		else this.musicPlayer.getMediaPlayer().stop();
		
		
		// Creazione oggetto Partita		
		//partita = new Partita(numeroGiocatori, Difficolta.FACILE); // test
		this.partita = Partita.getInstance(numeroGiocatori, Difficolta.FACILE);
	}
	
	public GestionePartitaController() {}
	
	@FXML public void initialize() 
	{
		this.textFieldUsername.setText("Giocatore");
		
		// partita = Partita.getIstanza(numeroGiocatori, Difficolta.valueOf(difficolta));
		try {this.avviaPreparazione();} catch (IOException e) {e.printStackTrace();}
		
		// creo una lista di Stringhe contentente gli username dei giocatori
		List<String> giocatori = new ArrayList<String>(this.partita.getGiocatori().size());
		// popolo la lista appena creata
		for(Giocatore g : this.partita.getGiocatori())
			giocatori.add(g.getUsername());
		// la aggiungo alla listView mostrata
		this.listViewGiocatori.setItems(FXCollections.observableArrayList(giocatori));
		this.listViewGiocatori.setFixedCellSize(30);
		this.listViewColori.setFixedCellSize(30);
		
		// =========================================================
		// =================================================
		
		// =========================================================
		// ==================================================================
		
	}
	
// MENU
	@FXML public void close(ActionEvent event)
	{
		Platform.exit();
		System.exit(0);
	}
	@FXML public void toggleFullScreen(ActionEvent event)
	{
		this.stage.setFullScreen(!this.stage.isFullScreen());
	}
	@FXML public void prova(ActionEvent event)
	{
		this.listViewGiocatori.getItems().add((String) "MarioMarioMario4");
	}
	@FXML public void terminaPartita(ActionEvent event) throws IOException
	{
		System.out.println("[Sistema] Partita terminata.");
		
		// ELIMINARE L'ISTANZA DELLA PARTITA (altrimenti rimane per le partite future!)
		Partita.deleteInstance();
		
		this.musicPlayer.getMediaPlayer().stop();
		this.musicPlayer = null;
		MusicPlayer.createNewMusicPlayer("David Fesliyan - Unfolding Revelation (Menu).mp3");
		
		// mostra HomeUtente
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/HomeUtente.fxml"));
		//Stage stage = (Stage) buttonApriTaccuino.getScene().getWindow();
		// passare music player a gestioneMenu?
		AnchorPane viewTutorial = (AnchorPane) loader.load();
		Scene scene = new Scene(viewTutorial);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		this.stage.setScene(scene);
		this.stage.setFullScreen(false); // non serve ma lo lascio per sicurezza
		
		// imposta la finestra al centro dello schermo
		javafx.geometry.Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
		System.out.println("Dimensioni finestra: " + stage.getWidth() + "x" + stage.getHeight());
	}
	
// PREPARAZIONE
	private void avviaPreparazione() throws IOException
	{
		System.out.println("[Sistema] fase di preparazione.");
		
		
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaPartita/ViewPreparazione.fxml"));
		loader.setController(new PreparazioneController());
		VBox viewPreparazione = (VBox) loader.load();
		
		this.vboxFase.getChildren().add(viewPreparazione);
	}
	
// MOVIMENTO
	public void selezionaMovimento(ActionEvent event)
	{		
		//((AnchorPane) this.posizione.getParent().getParent()).getChildren().remove(1);
		if(this.vboxFase.getChildren().size() > 1)
		{
			this.vboxFase.getChildren().remove(1);
		}
		
		this.posizione.setText("1-19");
		//this.vboxFase.getChildren().add(vbox);
		
		// creazione dinamica ViewMovimento
		VBox vboxMovimento = new VBox();
		vboxMovimento.setAlignment(Pos.TOP_CENTER);
		Label labelMovimento = new Label("Movimento");
		labelMovimento.setTextFill(Color.web("white"));
		labelMovimento.setFont(new Font("System", 25));
		labelMovimento.setStyle("-fx-font-weight: bold");
		HBox hboxLancioDadi = new HBox();
		hboxLancioDadi.setAlignment(Pos.CENTER);
		hboxLancioDadi.setPrefWidth(600);
		this.buttonLancioDadi = new Button("Lancia i dadi");
		buttonLancioDadi.setOnAction(this::lanciaDadi);
		Label labelDadi = new Label("Risultato: ");
		labelDadi.setTextFill(Color.web("white"));
		labelDadi.setFont(new Font("System", 18));
		this.textFieldDadi = new TextField();
		textFieldDadi.setStyle("-fx-background-color: black; -fx-text-inner-color: white;");
		textFieldDadi.setEditable(false);
		textFieldDadi.setFont(Font.font("System", FontWeight.BOLD, 18));
				
		Separator sepOrr1 = new Separator();
		sepOrr1.setOrientation(Orientation.HORIZONTAL);
		sepOrr1.setPrefHeight(50);
		sepOrr1.setVisible(false);
		Separator sepOrr2 = new Separator();
		sepOrr2.setOrientation(Orientation.HORIZONTAL);
		sepOrr2.setPrefHeight(100);
		sepOrr2.setVisible(false);
		Separator sepOrr3 = new Separator();
		sepOrr3.setOrientation(Orientation.HORIZONTAL);
		sepOrr3.setPrefHeight(100);
		sepOrr3.setVisible(false);
		Separator sepVer1 = new Separator();
		sepVer1.setOrientation(Orientation.VERTICAL);
		sepVer1.setPrefWidth(50);
		sepVer1.setVisible(false);
		Separator sepVer2 = new Separator();
		sepVer2.setOrientation(Orientation.VERTICAL);
		sepVer2.setPrefWidth(50);
		sepVer2.setVisible(false);
		this.buttonConfermaSpostamento = new Button("Conferma spostamento");
		this.buttonConfermaSpostamento.setOnAction(this::spostaPedinaBlu);
		this.buttonConfermaSpostamento.setDisable(true);
		
		vboxMovimento.getChildren().add(sepOrr1);
		vboxMovimento.getChildren().add(labelMovimento);
		vboxMovimento.getChildren().add(sepOrr2);
		hboxLancioDadi.getChildren().add(buttonLancioDadi);
		hboxLancioDadi.getChildren().add(sepVer1);
		hboxLancioDadi.getChildren().add(labelDadi);
		hboxLancioDadi.getChildren().add(sepVer2);
		hboxLancioDadi.getChildren().add(this.textFieldDadi);
		vboxMovimento.getChildren().add(hboxLancioDadi);
		vboxMovimento.getChildren().add(sepOrr3);
		vboxMovimento.getChildren().add(this.buttonConfermaSpostamento);
		this.vboxFase.getChildren().add(vboxMovimento);
		
		this.imageViewMappa.setOnMouseClicked(e -> {
			int x = (int) ((e.getX()-12.0)/26.0) + 1;
			int y = (int) ((e.getY()-12.0)/25.0) + 1;
			this.coordinateCorrentiMouse = x + "-" + y;
			xMouse = x;
			yMouse = y;
			if((x > 0 && x < 8 && y > 0 && y < 5) && !(x == 7 && y == 1))
			{
				this.posizione.setText("studio");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if(x > 9 && x < 16 && y > 1 && y < 8)
			{
				this.posizione.setText("ingresso");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if((x > 17 && x < 25 && y > 0 && y < 7) && !(x == 18 && y == 1))
			{
				this.posizione.setText("salotto");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if((x > 0 && x < 8 && y > 6 && y < 12) && !(x == 1 && y == 7) && !(x == 7 && y == 7) && !(x == 1 && y == 11) && !(x ==7 && y == 11))
			{
				this.posizione.setText("libreria");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if((x > 16 && x < 25 && y > 9 && y < 17) && !(x > 16 && x < 20 && y == 16))
			{
				this.posizione.setText("sala da pranzo");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if(x > 0 && x < 7 && y > 12 && y < 18)
			{
				this.posizione.setText("sala giochi");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if((x > 0 && x < 7 && y > 19 && y < 25) && !(x == 1 && y == 20) && !(x == 6 && y == 20))
			{
				this.posizione.setText("serra");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if((x > 8 && x < 17 && y > 17 && y < 24) || (x > 10 && x < 15 && y == 24))
			{
				this.posizione.setText("sala da ballo");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else if((x > 18 && x < 25 && y > 18 && y < 25) && !(x == 24 && y == 19))
			{
				this.posizione.setText("cucina");
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
			else
			{
				this.posizione.setText(coordinateCorrentiMouse);
			}
            checkLegal();
        });
	}
	@FXML public void lanciaDadi(ActionEvent event)
	{
		int result = ThreadLocalRandom.current().nextInt(2, 13);
		this.partita.getGiocatoreByUsername("Giocatore").setEsitoDadi(result);
		this.textFieldDadi.setText(String.valueOf(result));
		this.buttonLancioDadi.setDisable(true);
		this.esitoDadi = result;
	}
	/*public void selezionaMovimento(ActionEvent event) throws IOException
	{
		System.out.println("[Sistema] Giocatore ha selezionato Movimento.");
		
		if(this.vboxFase.getChildren().size() > 0)
			this.vboxFase.getChildren().remove(0);
		
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaPartita/ViewMovimento.fxml"));
		VBox viewMovimento = (VBox) loader.load();
		this.vboxFase.getChildren().add(viewMovimento);
		
		// disabilitare bottoni
		this.buttonMovimento.setDisable(true);
		this.buttonIndagine.setDisable(true);
		this.buttonAccusa.setDisable(true);
		this.buttonApriTaccuino.setDisable(true);
		this.buttonApriBloccoNote.setDisable(true);
		
		// abilità il click della mappa
		
	}*/
	// =========================================================
	// =========================================================
	private void checkLegal() // DETERMINA SE LA CASELLA SELEZIONATA E' VALIDA
	{
		if(!isRaggiungibile())
		{
			this.sposta.setDisable(true);
			this.buttonConfermaSpostamento.setDisable(true);
			return;
		}
		if(!(this.posizione.getText().equalsIgnoreCase("studio") || this.posizione.getText().equalsIgnoreCase("ingresso") || this.posizione.getText().equalsIgnoreCase("salotto") ||
				this.posizione.getText().equalsIgnoreCase("libreria") || this.posizione.getText().equalsIgnoreCase("sala giochi") || this.posizione.getText().equalsIgnoreCase("sala da pranzo") ||
				this.posizione.getText().equalsIgnoreCase("serra") || this.posizione.getText().equalsIgnoreCase("sala da ballo") || this.posizione.getText().equalsIgnoreCase("cucina")))
		{
			String[] coordinate = this.posizione.getText().split("-");
			int x = Integer.parseInt(coordinate[0]);
			int y = Integer.parseInt(coordinate[1]);
			if(x < 1 || x > 24)
			{
				this.sposta.setDisable(true);
				this.buttonConfermaSpostamento.setDisable(true);
			}
			else if(y < 1 || y > 25)
			{
				this.sposta.setDisable(true);
				this.buttonConfermaSpostamento.setDisable(true);
			}
			else if((x > 9 && x < 15) && (y > 8 && y < 16))
			{
				this.sposta.setDisable(true);
				this.buttonConfermaSpostamento.setDisable(true);
			}
			else if(y == 25 && (x != 10 && x != 15))
			{
				this.sposta.setDisable(true);
				this.buttonConfermaSpostamento.setDisable(true);
			}
			else if(y == 1 && (x == 7 || (x > 8 && x < 17) || x == 18))
			{
				this.sposta.setDisable(true);
				this.buttonConfermaSpostamento.setDisable(true);
			}
			else if(x == 1 && (y == 5 || y == 7 || y == 11 || y == 12 || y == 18 || y == 20))
			{
				this.sposta.setDisable(true);
				this.buttonConfermaSpostamento.setDisable(true);
			}
			else if(x == 24 && (y == 7 || y == 9 || y == 17 || y == 19))
			{
				this.sposta.setDisable(true);
				this.buttonConfermaSpostamento.setDisable(true);
			}
			else{
				this.sposta.setDisable(false);
				this.buttonConfermaSpostamento.setDisable(false);
			}
		}
		if(this.posizione.getText().equalsIgnoreCase(this.partita.oldPos[this.partita.turno])) // CHECK CHE LA NUOVA SELEZIONE E LA VECCHIA POSIZIONE SIANO DIVERSE
		{
			this.sposta.setDisable(true);
			this.buttonConfermaSpostamento.setDisable(true);
		}
	}
	
	/*private boolean casellaIsAttraversabile() // DA IMPLEMENTARE
	{
		return true;
	}*/
	private boolean isRaggiungibile() // CALCOLA IL PERCORSO    PER ORA MANCA DA INSERIRE GLI OSTACOLI (SIA PEDINE CHE MURI DI STANZE O IL BUCO CENTRALE)
	{
		int counterPassi = 0;
		boolean ok = false;
		
		if(!(this.partita.oldPos[this.partita.turno].contains("-")) && !(this.posizione.getText().contains("-")))
			return false;
		
		if (this.partita.oldPos[this.partita.turno].contains("-"))
		{
			String[] coordinate = this.partita.oldPos[this.partita.turno].split("-");
			int xold = Integer.parseInt(coordinate[0]);
			int yold = Integer.parseInt(coordinate[1]);

			if (xMouse > xold)
				counterPassi = counterPassi + (xMouse - xold);
			else counterPassi = counterPassi + (xold - xMouse);

			if (yMouse > yold)
				counterPassi = counterPassi + (yMouse - yold);
			else counterPassi = counterPassi + (yold - yMouse);
			
			if (counterPassi > this.esitoDadi)
				return false;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("studio")) // USCIRE DALLO STUDIO
		{
			if (xMouse > 7)
				counterPassi = counterPassi + (xMouse - 7);
			else counterPassi = counterPassi + (7 - xMouse);
			
			if (yMouse > 5)
				counterPassi = counterPassi + (yMouse - 5);
			else counterPassi = counterPassi + (5 - yMouse);
			
			if (counterPassi > (this.esitoDadi - 1))
				return false;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("ingresso")) // USCIRE DALL'INGRESSO
		{
			if (xMouse > 9)
				counterPassi = counterPassi + (xMouse - 9);
			else counterPassi = counterPassi + (9 - xMouse);
			
			if (yMouse > 5)
				counterPassi = counterPassi + (yMouse - 5);
			else counterPassi = counterPassi + (5 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 12)
				counterPassi = counterPassi + (xMouse - 12);
			else counterPassi = counterPassi + (12 - xMouse);
			
			if (yMouse > 8)
				counterPassi = counterPassi + (yMouse - 8);
			else counterPassi = counterPassi + (8 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 13)
				counterPassi = counterPassi + (xMouse - 13);
			else counterPassi = counterPassi + (13 - xMouse);
			
			if (yMouse > 8)
				counterPassi = counterPassi + (yMouse - 8);
			else counterPassi = counterPassi + (8 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			return ok;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("salotto")) // USCIRE DAL SALOTTO
		{
			if (xMouse > 18)
				counterPassi = counterPassi + (xMouse - 18);
			else counterPassi = counterPassi + (18 - xMouse);
			
			if (yMouse > 7)
				counterPassi = counterPassi + (yMouse - 7);
			else counterPassi = counterPassi + (7 - yMouse);
			
			if (counterPassi > (this.esitoDadi - 1))
				return false;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("libreria")) // USCIRE DALLA LIBRERIA
		{
			if (xMouse > 8)
				counterPassi = counterPassi + (xMouse - 8);
			else counterPassi = counterPassi + (8 - xMouse);
			
			if (yMouse > 9)
				counterPassi = counterPassi + (yMouse - 9);
			else counterPassi = counterPassi + (9 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 4)
				counterPassi = counterPassi + (xMouse - 4);
			else counterPassi = counterPassi + (4 - xMouse);
			
			if (yMouse > 12)
				counterPassi = counterPassi + (yMouse - 12);
			else counterPassi = counterPassi + (12 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			return ok;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("sala da pranzo")) // USCIRE DALLA SALA DA PRANZO
		{
			if (xMouse > 18)
				counterPassi = counterPassi + (xMouse - 18);
			else counterPassi = counterPassi + (18 - xMouse);
			
			if (yMouse > 9)
				counterPassi = counterPassi + (yMouse - 9);
			else counterPassi = counterPassi + (9 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 16)
				counterPassi = counterPassi + (xMouse - 16);
			else counterPassi = counterPassi + (16 - xMouse);
			
			if (yMouse > 13)
				counterPassi = counterPassi + (yMouse - 13);
			else counterPassi = counterPassi + (13 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			return ok;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("sala giochi")) // USCIRE DALLA SALA GIOCHI
		{
			if (xMouse > 2)
				counterPassi = counterPassi + (xMouse - 2);
			else counterPassi = counterPassi + (2 - xMouse);
			
			if (yMouse > 12)
				counterPassi = counterPassi + (yMouse - 12);
			else counterPassi = counterPassi + (12 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 7)
				counterPassi = counterPassi + (xMouse - 7);
			else counterPassi = counterPassi + (7 - xMouse);
			
			if (yMouse > 16)
				counterPassi = counterPassi + (yMouse - 16);
			else counterPassi = counterPassi + (16 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			return ok;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("serra")) // USCIRE DALLA SERRA
		{
			if (xMouse > 6)
				counterPassi = counterPassi + (xMouse - 6);
			else counterPassi = counterPassi + (6 - xMouse);
			
			if (yMouse > 20)
				counterPassi = counterPassi + (yMouse - 20);
			else counterPassi = counterPassi + (20 - yMouse);
			
			if (counterPassi > (this.esitoDadi - 1))
				return false;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("sala da ballo")) // USCIRE DALLA SALA DA BALLO
		{
			if (xMouse > 8)
				counterPassi = counterPassi + (xMouse - 8);
			else counterPassi = counterPassi + (8 - xMouse);
			
			if (yMouse > 20)
				counterPassi = counterPassi + (yMouse - 20);
			else counterPassi = counterPassi + (20 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 10)
				counterPassi = counterPassi + (xMouse - 10);
			else counterPassi = counterPassi + (10 - xMouse);
			
			if (yMouse > 17)
				counterPassi = counterPassi + (yMouse - 17);
			else counterPassi = counterPassi + (17 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 15)
				counterPassi = counterPassi + (xMouse - 15);
			else counterPassi = counterPassi + (15 - xMouse);
			
			if (yMouse > 17)
				counterPassi = counterPassi + (yMouse - 17);
			else counterPassi = counterPassi + (17 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			
			counterPassi = 0;
			
			if (xMouse > 17)
				counterPassi = counterPassi + (xMouse - 17);
			else counterPassi = counterPassi + (17 - xMouse);
			
			if (yMouse > 20)
				counterPassi = counterPassi + (yMouse - 20);
			else counterPassi = counterPassi + (20 - yMouse);
			
			if (counterPassi <= (this.esitoDadi - 1))
				ok = true;
			return ok;
		}
		if (this.partita.oldPos[this.partita.turno].equalsIgnoreCase("cucina")) // USCIRE DALLA CUCINA
		{
			if (xMouse > 20)
				counterPassi = counterPassi + (xMouse - 20);
			else counterPassi = counterPassi + (20 - xMouse);
			
			if (yMouse > 18)
				counterPassi = counterPassi + (yMouse - 18);
			else counterPassi = counterPassi + (18 - yMouse);
			
			if (counterPassi > (this.esitoDadi - 1))
				return false;
		}
		return true;
	}
	private int[] contatoreStanze(int counterStanza) // TIENE CONTO DI QUANTE PERSONE SONO PRESENTI IN UNA STANZA PER NON SOVRAPPORRE LE PEDINE
	{
		int[] result = new int[2];
		
		int x = 0;
		int y = 0;
		
		if (counterStanza == 1)
		{
			x = 0; y = 0;
		}
		if (counterStanza == 2)
		{
			x = 1; y = 0;
		}
		if (counterStanza == 3)
		{
			x = 2; y = 0;
		}
		if (counterStanza == 4)
		{
			x = 0; y = 1;
		}
		if (counterStanza == 5)
		{
			x = 1; y = 1;
		}
		if (counterStanza == 6)
		{
			x = 2; y = 1;
		}
		
		result[0] = x;
		result[1] = y;
		
		return result;
	}
	
	@FXML public void textChanged(ActionEvent event)
	{
		checkLegal();
	}
	
	@FXML public void spostaPedinaBlu(ActionEvent event) //SI PUO' MODIFICARE USANDO ARRAY DA 6 DI PEDINA[6] INVECE CHE PEDINABLU E VECCHIAPOS[6] INVECE CHE VECCHIAPOSBLU
	{												  //USANDO COME i IL TURNO CORRENTE, PER ORA TENIAMO SOLO LO SPOSTA PEDINA BLU
		int x = 1;
		int y = 1;		
		
		// CASI SPOSTAMENTO IN STANZA
		
		if (this.posizione.getText().equalsIgnoreCase("studio")) 
		{
			int[] result = contatoreStanze(this.counterStudio);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+2)*26);
			this.imageViewPedinaBlu.setY((double) (y+1)*25);
			this.counterStudio++;
			this.partita.oldPos[this.partita.turno] = "studio";
		}
		else if (this.posizione.getText().equalsIgnoreCase("ingresso")) 
		{
			int[] result = contatoreStanze(this.counterIngresso);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+11)*26);
			this.imageViewPedinaBlu.setY((double) (y+3)*25);
			this.counterIngresso++;
			this.partita.oldPos[this.partita.turno] = "ingresso";
		}
		else if (this.posizione.getText().equalsIgnoreCase("salotto")) 
		{
			int[] result = contatoreStanze(this.counterSalotto);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+19)*26);
			this.imageViewPedinaBlu.setY((double) (y+2)*25);
			this.counterSalotto++;
			this.partita.oldPos[this.partita.turno] = "salotto";
		}
		else if (this.posizione.getText().equalsIgnoreCase("libreria")) 
		{
			int[] result = contatoreStanze(this.counterLibreria);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+2)*26);
			this.imageViewPedinaBlu.setY((double) (y+8)*25);
			this.counterLibreria++;
			this.partita.oldPos[this.partita.turno] = "libreria";
		}
		else if (this.posizione.getText().equalsIgnoreCase("sala da pranzo")) 
		{
			int[] result = contatoreStanze(this.counterSalaDaPranzo);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+19)*26);
			this.imageViewPedinaBlu.setY((double) (y+11)*25);
			this.counterSalaDaPranzo++;
			this.partita.oldPos[this.partita.turno] = "sala da pranzo";
		}
		else if (this.posizione.getText().equalsIgnoreCase("sala giochi")) 
		{
			int[] result = contatoreStanze(this.counterSalaGiochi);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+2)*26);
			this.imageViewPedinaBlu.setY((double) (y+14)*25);
			this.counterSalaGiochi++;
			this.partita.oldPos[this.partita.turno] = "sala giochi";
		}
		else if (this.posizione.getText().equalsIgnoreCase("serra")) 
		{
			int[] result = contatoreStanze(this.counterSerra);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+2)*26);
			this.imageViewPedinaBlu.setY((double) (y+21)*25);
			this.counterSerra++;
			this.partita.oldPos[this.partita.turno] = "serra";
		}
		else if (this.posizione.getText().equalsIgnoreCase("sala da ballo")) 
		{
			int[] result = contatoreStanze(this.counterSalaDaBallo);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+10)*26);
			this.imageViewPedinaBlu.setY((double) (y+19)*25);
			this.counterSalaDaBallo++;
			this.partita.oldPos[this.partita.turno] = "sala da ballo";
		}
		else if (this.posizione.getText().equalsIgnoreCase("cucina") && !this.partita.oldPos[this.partita.turno].equalsIgnoreCase("cucina")) 
		{
			int[] result = contatoreStanze(this.counterCucina);
			x = result[0];
			y = result[1];
			this.imageViewPedinaBlu.setX((double) (x+20)*26);
			this.imageViewPedinaBlu.setY((double) (y+20)*25);
			this.counterCucina++;
			this.partita.oldPos[this.partita.turno] = "cucina";
		}
		else if (this.posizione.getText().equalsIgnoreCase("studio") || this.posizione.getText().equalsIgnoreCase("ingresso") || this.posizione.getText().equalsIgnoreCase("salotto") ||
				this.posizione.getText().equalsIgnoreCase("libreria") || this.posizione.getText().equalsIgnoreCase("sala da pranzo") || this.posizione.getText().equalsIgnoreCase("sala giochi") ||
				this.posizione.getText().equalsIgnoreCase("serra") || this.posizione.getText().equalsIgnoreCase("sala da ballo") || this.posizione.getText().equalsIgnoreCase("cucina"))
			;
		else	//CASO SPOSTAMENTO COORDINATE NORMALI
		{ 
			String[] coordinate = this.posizione.getText().split("-");
			this.imageViewPedinaBlu.setX((double) ((Integer.parseInt(coordinate[0]) - 1)*26));
			this.imageViewPedinaBlu.setY((double) ((Integer.parseInt(coordinate[1]) - 1)*25));
			this.partita.oldPos[this.partita.turno] = this.posizione.getText(); 
			
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("studio")) // RIMOZIONE PEDINA DAL CONTATORE DELLA STANZA QUALORA FOSSE IN UNA DI ESSE
			{
				this.counterStudio--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("ingresso"))
			{
				this.counterIngresso--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("salotto"))
			{
				this.counterSalotto--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("libreria"))
			{
				this.counterLibreria--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("sala da pranzo"))
			{
				this.counterSalaDaPranzo--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("sala giochi"))
			{
				this.counterSalaGiochi--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("serra"))
			{
				this.counterSerra--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("sala da ballo"))
			{
				this.counterSalaDaBallo--;
			}
			if(this.partita.oldPos[this.partita.turno].equalsIgnoreCase("cucina"))
			{
				this.counterCucina--;
			}
			// this.curPos[this.turno] = ((int) ((this.pedinaBlu.getX()-12.0)/26.0) + 2) + "-" + ((int) ((this.pedinaBlu.getX()-12.0)/26.0) + 2); // CALCOLO CASELLA
		}
		this.partita.oldXY[this.partita.turno][0] = (int) ((this.imageViewPedinaBlu.getX()-12.0)/26.0) + 2;
		this.partita.oldXY[this.partita.turno][1] = (int) ((this.imageViewPedinaBlu.getY()-12.0)/25.0) + 2;
		System.out.println(this.partita.oldXY[0][0] +"-"+this.partita.oldXY[0][1]);
		this.sposta.setDisable(true);
		this.buttonConfermaSpostamento.setDisable(true);
		
		if(this.vboxFase.getChildren().size() > 1);
			this.vboxFase.getChildren().remove(1);
		this.esitoDadi = 0;
	}
	// =========================================================
	// ===============================================================
	
	
	
	@FXML public void selezionaIndagine(ActionEvent event) throws IOException
	{
		if(this.vboxFase.getChildren().size() > 1)
		{
			this.vboxFase.getChildren().remove(1);
		}
		
		
		// creazione dinamica Indagine
		VBox vboxIndagine = new VBox();
		vboxIndagine.setAlignment(Pos.TOP_CENTER);
		
		Label labelIndagine = new Label("Indagine");
		labelIndagine.setTextFill(Color.web("white"));
		labelIndagine.setFont(Font.font("System", FontWeight.BOLD, 25));
		
		HBox hboxSelectGiocatore = new HBox();
		hboxSelectGiocatore.setAlignment(Pos.CENTER_RIGHT);
		hboxSelectGiocatore.setPrefWidth(600);
		Label labelGiocatore = new Label("Seleziona il giocatore: ");
		labelGiocatore.setTextFill(Color.web("white"));
		labelGiocatore.setFont(Font.font("System", 18));
		labelGiocatore.setPrefWidth(350);
		hboxSelectGiocatore.getChildren().add(labelGiocatore);
		
		HBox hboxSelectStanza = new HBox();
		hboxSelectStanza.setAlignment(Pos.CENTER_RIGHT);
		hboxSelectStanza.setPrefWidth(600);
		Label labelStanza = new Label("Ti trovi nella STANZA: ");
		labelStanza.setTextFill(Color.web("white"));
		labelStanza.setFont(Font.font("System", 18));
		labelStanza.setPrefWidth(350);
		hboxSelectStanza.getChildren().add(labelStanza);
		
		HBox hboxSelectArma = new HBox();
		hboxSelectArma.setAlignment(Pos.CENTER_RIGHT);
		hboxSelectArma.setPrefWidth(600);
		Label labelArma = new Label("Seleziona l'ARMA: ");
		labelArma.setTextFill(Color.web("white"));
		labelArma.setFont(Font.font("System", 18));
		labelArma.setPrefWidth(350);
		hboxSelectArma.getChildren().add(labelArma);
		
		HBox hboxSelectSospettato = new HBox();
		hboxSelectSospettato.setAlignment(Pos.CENTER_RIGHT);
		hboxSelectSospettato.setPrefWidth(600);
		Label labelSospettato = new Label("Seleziona il SOSPETTATO: ");
		labelSospettato.setTextFill(Color.web("white"));
		labelSospettato.setFont(Font.font("System", 18));
		labelSospettato.setPrefWidth(350);
		hboxSelectSospettato.getChildren().add(labelSospettato);
		
		
		List<String> listGiocatori = new ArrayList<String>();
		for(Giocatore g : this.partita.getGiocatori())
		{
			if(!g.getUsername().equals("Giocatore"))
				listGiocatori.add(g.getUsername());
		}
			
		List<String> listStanze = new ArrayList<String>();
		List<String> listArmi = new ArrayList<String>();
		List<String> listSospettati = new ArrayList<String>();
		for(CartaIndizio c : this.partita.getCarteIndizioTotali())
		{
			if(c.getTipo().equals(TipoCarta.STANZA))
				listStanze.add(c.getDescrizione());
			if(c.getTipo().equals(TipoCarta.ARMA))
				listArmi.add(c.getDescrizione());
			if(c.getTipo().equals(TipoCarta.SOSPETTATO))
				listSospettati.add(c.getDescrizione());
		}
		this.comboBoxGiocatori = new ComboBox<String>(FXCollections.observableArrayList(listGiocatori));
		comboBoxGiocatori.setValue(listGiocatori.get(0));
		comboBoxGiocatori.setPrefWidth(180);
		hboxSelectGiocatore.getChildren().add(comboBoxGiocatori);
		this.textFieldStanza = new TextField();
		textFieldStanza.setPrefWidth(180);
		textFieldStanza.setText(String.valueOf(this.partita.oldPos[0])); // ottiene la stanza
		textFieldStanza.setEditable(false);
		hboxSelectStanza.getChildren().add(textFieldStanza);
		this.comboBoxArmi = new ComboBox<String>(FXCollections.observableArrayList(listArmi));
		comboBoxArmi.setPrefWidth(180);
		comboBoxArmi.setValue(listArmi.get(0));
		hboxSelectArma.getChildren().add(comboBoxArmi);
		this.comboBoxSospettati = new ComboBox<String>(FXCollections.observableArrayList(listSospettati));
		comboBoxSospettati.setPrefWidth(180);
		comboBoxSospettati.setValue(listSospettati.get(0));
		hboxSelectSospettato.getChildren().add(comboBoxSospettati);
		
		this.buttonConfermaIndagine = new Button("Conferma indagine");
		this.buttonConfermaIndagine.setOnAction(this::confermaIndagine);
		
		Label labelResult = new Label("Risultato: ");
		labelResult.setTextFill(Color.web("white"));
		labelResult.setFont(Font.font("System", 18));
		labelResult.setPrefWidth(100);
		
		
		HBox hboxRisultato1 = new HBox();
		hboxRisultato1.setPrefWidth(300);
		hboxRisultato1.setAlignment(Pos.CENTER);
		this.textFieldRisultatoIndagine1 = new TextField();
		this.textFieldRisultatoIndagine1.setEditable(false);
		hboxRisultato1.getChildren().add(this.textFieldRisultatoIndagine1);
		
		HBox hboxRisultato2 = new HBox();
		hboxRisultato2.setPrefWidth(300);
		hboxRisultato2.setAlignment(Pos.CENTER);
		this.textFieldRisultatoIndagine2 = new TextField();
		this.textFieldRisultatoIndagine2.setEditable(false);
		hboxRisultato2.getChildren().add(this.textFieldRisultatoIndagine2);
		
		HBox hboxRisultato3 = new HBox();
		hboxRisultato3.setPrefWidth(300);
		hboxRisultato3.setAlignment(Pos.CENTER);
		this.textFieldRisultatoIndagine3 = new TextField();
		this.textFieldRisultatoIndagine3.setEditable(false);
		hboxRisultato3.getChildren().add(this.textFieldRisultatoIndagine3);
		
		Button buttonChiudi = new Button("Chiudi");
		buttonChiudi.setOnAction(this::chiudiIndagine);
				
		Separator sepOrr1 = new Separator();
		sepOrr1.setOrientation(Orientation.HORIZONTAL);
		sepOrr1.setPrefHeight(50);
		sepOrr1.setVisible(false);
		Separator sepOrr2 = new Separator();
		sepOrr2.setOrientation(Orientation.HORIZONTAL);
		sepOrr2.setPrefHeight(25);
		sepOrr2.setVisible(false);
		Separator sepOrr3 = new Separator();
		sepOrr3.setOrientation(Orientation.HORIZONTAL);
		sepOrr3.setPrefHeight(25);
		sepOrr3.setVisible(false);
		Separator sepOrr4 = new Separator();
		sepOrr4.setOrientation(Orientation.HORIZONTAL);
		sepOrr4.setPrefHeight(25);
		sepOrr4.setVisible(false);
		Separator sepOrr5 = new Separator();
		sepOrr5.setOrientation(Orientation.HORIZONTAL);
		sepOrr5.setPrefHeight(25);
		sepOrr5.setVisible(false);
		Separator sepOrr6 = new Separator();
		sepOrr6.setOrientation(Orientation.HORIZONTAL);
		sepOrr6.setPrefHeight(25);
		sepOrr6.setVisible(false);
		Separator sepOrr7 = new Separator();
		sepOrr7.setOrientation(Orientation.HORIZONTAL);
		sepOrr7.setPrefHeight(50);
		sepOrr7.setVisible(false);
		Separator sepOrr8 = new Separator();
		sepOrr8.setOrientation(Orientation.HORIZONTAL);
		sepOrr8.setPrefHeight(50);
		sepOrr8.setVisible(false);
		Separator sepOrr9 = new Separator();
		sepOrr9.setOrientation(Orientation.HORIZONTAL);
		sepOrr9.setPrefHeight(100);
		sepOrr9.setVisible(false);
						
		vboxIndagine.getChildren().add(sepOrr1);
		vboxIndagine.getChildren().add(labelIndagine);
		vboxIndagine.getChildren().add(sepOrr2);
		vboxIndagine.getChildren().add(hboxSelectGiocatore);
		vboxIndagine.getChildren().add(sepOrr3);
		vboxIndagine.getChildren().add(hboxSelectStanza);
		vboxIndagine.getChildren().add(sepOrr4);
		vboxIndagine.getChildren().add(hboxSelectArma);
		vboxIndagine.getChildren().add(sepOrr5);
		vboxIndagine.getChildren().add(hboxSelectSospettato);
		vboxIndagine.getChildren().add(sepOrr6);
		vboxIndagine.getChildren().add(this.buttonConfermaIndagine);
		vboxIndagine.getChildren().add(sepOrr7);
		vboxIndagine.getChildren().add(labelResult);
		vboxIndagine.getChildren().add(hboxRisultato1);
		vboxIndagine.getChildren().add(hboxRisultato2);
		vboxIndagine.getChildren().add(hboxRisultato3);
		vboxIndagine.getChildren().add(sepOrr8);
		vboxIndagine.getChildren().add(buttonChiudi);
		vboxIndagine.getChildren().add(sepOrr9);
		
		this.vboxFase.getChildren().add(vboxIndagine);
	}
	@FXML public void confermaIndagine(ActionEvent event)
	{
		for(CartaIndizio c : this.partita.getGiocatoreByUsername(this.comboBoxGiocatori.getValue()).getCarteIniziali())
		{
			if(this.textFieldStanza.getText().equalsIgnoreCase(c.getDescrizione()))
				this.textFieldRisultatoIndagine1.setText(c.getDescrizione());
			else if(this.comboBoxArmi.getValue().equalsIgnoreCase(c.getDescrizione()))
				this.textFieldRisultatoIndagine2.setText(c.getDescrizione());
			else if(this.comboBoxSospettati.getValue().equalsIgnoreCase(c.getDescrizione()))
				this.textFieldRisultatoIndagine3.setText(c.getDescrizione());
		}
		
		  int x = 1;
		  int y = 1;
		  
		  // CASI SPOSTAMENTO IN STANZA
		  
		  if (this.posizione.getText().equalsIgnoreCase("studio")) 
		  {
		   int[] result = contatoreStanze(this.counterStudio);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+2)*26);
		   this.imageViewPedinaVerde.setY((double) (y+1)*25);
		   this.counterStudio++;
		   this.partita.oldPos[2] = "studio";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("ingresso")) 
		  {
		   int[] result = contatoreStanze(this.counterIngresso);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+11)*26);
		   this.imageViewPedinaVerde.setY((double) (y+3)*25);
		   this.counterIngresso++;
		   this.partita.oldPos[2] = "ingresso";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("salotto")) 
		  {
		   int[] result = contatoreStanze(this.counterSalotto);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+19)*26);
		   this.imageViewPedinaVerde.setY((double) (y+2)*25);
		   this.counterSalotto++;
		   this.partita.oldPos[2] = "salotto";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("libreria")) 
		  {
		   int[] result = contatoreStanze(this.counterLibreria);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+2)*26);
		   this.imageViewPedinaVerde.setY((double) (y+8)*25);
		   this.counterLibreria++;
		   this.partita.oldPos[2] = "libreria";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("sala da pranzo")) 
		  {
		   int[] result = contatoreStanze(this.counterSalaDaPranzo);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+19)*26);
		   this.imageViewPedinaVerde.setY((double) (y+11)*25);
		   this.counterSalaDaPranzo++;
		   this.partita.oldPos[2] = "sala da pranzo";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("sala giochi")) 
		  {
		   int[] result = contatoreStanze(this.counterSalaGiochi);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+2)*26);
		   this.imageViewPedinaVerde.setY((double) (y+14)*25);
		   this.counterSalaGiochi++;
		   this.partita.oldPos[2] = "sala giochi";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("serra")) 
		  {
		   int[] result = contatoreStanze(this.counterSerra);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+2)*26);
		   this.imageViewPedinaVerde.setY((double) (y+21)*25);
		   this.counterSerra++;
		   this.partita.oldPos[2] = "serra";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("sala da ballo")) 
		  {
		   int[] result = contatoreStanze(this.counterSalaDaBallo);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+10)*26);
		   this.imageViewPedinaVerde.setY((double) (y+19)*25);
		   this.counterSalaDaBallo++;
		   this.partita.oldPos[2] = "sala da ballo";
		  }
		  else if (this.posizione.getText().equalsIgnoreCase("cucina") && !this.partita.oldPos[this.partita.turno].equalsIgnoreCase("cucina")) 
		  {
		   int[] result = contatoreStanze(this.counterCucina);
		   x = result[0];
		   y = result[1];
		   this.imageViewPedinaVerde.setX((double) (x+20)*26);
		   this.imageViewPedinaVerde.setY((double) (y+20)*25);
		   this.counterCucina++;
		   this.partita.oldPos[2] = "cucina";
		  }
	}
	@FXML public void chiudiIndagine(ActionEvent event)
	{
		if(this.vboxFase.getChildren().size() > 1)
		{
			this.vboxFase.getChildren().remove(1);
		}
	}
	@FXML public void selezionaAccusa(ActionEvent event) throws IOException
	{
		if(this.vboxFase.getChildren().size() > 1)
		{
			this.vboxFase.getChildren().remove(1);
		}
		
		
		// creazione dinamica Indagine
		VBox vboxAccusa = new VBox();
		vboxAccusa.setAlignment(Pos.TOP_CENTER);
		
		Label labelAccusa = new Label("Accusa");
		labelAccusa.setTextFill(Color.web("white"));
		labelAccusa.setFont(Font.font("System", FontWeight.BOLD, 25));
		
		Label labelIstruzioni = new Label("Seleziona la terna del delitto");
		labelIstruzioni.setTextFill(Color.web("white"));
		labelIstruzioni.setFont(Font.font("System", 18));
		
		HBox hboxSelectStanza = new HBox();
		hboxSelectStanza.setAlignment(Pos.CENTER_RIGHT);
		hboxSelectStanza.setPrefWidth(600);
		Label labelStanza = new Label("Ti trovi nella STANZA: ");
		labelStanza.setTextFill(Color.web("white"));
		labelStanza.setFont(Font.font("System", 18));
		labelStanza.setPrefWidth(350);
		hboxSelectStanza.getChildren().add(labelStanza);
		
		HBox hboxSelectArma = new HBox();
		hboxSelectArma.setAlignment(Pos.CENTER_RIGHT);
		hboxSelectArma.setPrefWidth(600);
		Label labelArma = new Label("Seleziona l'ARMA: ");
		labelArma.setTextFill(Color.web("white"));
		labelArma.setFont(Font.font("System", 18));
		labelArma.setPrefWidth(350);
		hboxSelectArma.getChildren().add(labelArma);
		
		HBox hboxSelectSospettato = new HBox();
		hboxSelectSospettato.setAlignment(Pos.CENTER_RIGHT);
		hboxSelectSospettato.setPrefWidth(600);
		Label labelSospettato = new Label("Seleziona il SOSPETTATO: ");
		labelSospettato.setTextFill(Color.web("white"));
		labelSospettato.setFont(Font.font("System", 18));
		labelSospettato.setPrefWidth(350);
		hboxSelectSospettato.getChildren().add(labelSospettato);
		
		Separator sepOrr1 = new Separator();
		sepOrr1.setOrientation(Orientation.HORIZONTAL);
		sepOrr1.setPrefHeight(25);
		sepOrr1.setVisible(false);
		Separator sepOrr2 = new Separator();
		sepOrr2.setOrientation(Orientation.HORIZONTAL);
		sepOrr2.setPrefHeight(50);
		sepOrr2.setVisible(false);
		Separator sepOrr3 = new Separator();
		sepOrr3.setOrientation(Orientation.HORIZONTAL);
		sepOrr3.setPrefHeight(25);
		sepOrr3.setVisible(false);
		Separator sepOrr4 = new Separator();
		sepOrr4.setOrientation(Orientation.HORIZONTAL);
		sepOrr4.setPrefHeight(25);
		sepOrr4.setVisible(false);
		Separator sepOrr5 = new Separator();
		sepOrr5.setOrientation(Orientation.HORIZONTAL);
		sepOrr5.setPrefHeight(25);
		sepOrr5.setVisible(false);
		Separator sepOrr6 = new Separator();
		sepOrr6.setOrientation(Orientation.HORIZONTAL);
		sepOrr6.setPrefHeight(50);
		sepOrr6.setVisible(false);
		Separator sepOrr7 = new Separator();
		sepOrr7.setOrientation(Orientation.HORIZONTAL);
		sepOrr7.setPrefHeight(50);
		sepOrr7.setVisible(false);
		Separator sepOrr8 = new Separator();
		sepOrr8.setOrientation(Orientation.HORIZONTAL);
		sepOrr8.setPrefHeight(100);
		sepOrr8.setVisible(false);
		
		List<String> listStanze = new ArrayList<String>();
		List<String> listArmi = new ArrayList<String>();
		List<String> listSospettati = new ArrayList<String>();
		for(CartaIndizio c : this.partita.getCarteIndizioTotali())
		{
			if(c.getTipo().equals(TipoCarta.STANZA))
				listStanze.add(c.getDescrizione());
			if(c.getTipo().equals(TipoCarta.ARMA))
				listArmi.add(c.getDescrizione());
			if(c.getTipo().equals(TipoCarta.SOSPETTATO))
				listSospettati.add(c.getDescrizione());
		}
		this.comboBoxAccusaStanze = new ComboBox<String>(FXCollections.observableArrayList(listStanze));
		comboBoxAccusaStanze.setPrefWidth(180);
		comboBoxAccusaStanze.setValue(listStanze.get(0));
		comboBoxAccusaStanze.setEditable(false);
		hboxSelectStanza.getChildren().add(comboBoxAccusaStanze);
		this.comboBoxAccusaArmi = new ComboBox<String>(FXCollections.observableArrayList(listArmi));
		this.comboBoxAccusaArmi.setPrefWidth(180);
		this.comboBoxAccusaArmi.setValue(listArmi.get(0));
		hboxSelectArma.getChildren().add(comboBoxAccusaArmi);
		this.comboBoxAccusaSospettati = new ComboBox<String>(FXCollections.observableArrayList(listSospettati));
		this.comboBoxAccusaSospettati.setPrefWidth(180);
		this.comboBoxAccusaSospettati.setValue(listSospettati.get(0));
		hboxSelectSospettato.getChildren().add(comboBoxAccusaSospettati);
	
		HBox hboxResult = new HBox();
		hboxResult.setPrefWidth(200);
		hboxResult.setAlignment(Pos.CENTER);
		this.textFieldRisultatoAccusa = new TextField();
		this.textFieldRisultatoAccusa.setPrefWidth(500);
		this.textFieldRisultatoAccusa.setFont(Font.font("System", 18));
		this.textFieldRisultatoAccusa.setAlignment(Pos.CENTER);
		this.textFieldRisultatoAccusa.setEditable(false);
		this.textFieldRisultatoAccusa.setVisible(false);
		hboxResult.getChildren().add(this.textFieldRisultatoAccusa);
		
		this.buttonConfermaAccusa = new Button("Conferma accusa");
		this.buttonConfermaAccusa.setOnAction(this::confermaAccusa);
		
		
		this.buttonEsci = new Button("Esci");
		buttonEsci.setOnAction(arg0 -> {
			try {terminaPartita(arg0);} catch (IOException e) {e.printStackTrace();}
		});
		this.buttonEsci.setVisible(false);
		
		vboxAccusa.setAlignment(Pos.TOP_CENTER);
		
		vboxAccusa.getChildren().add(sepOrr1);
		vboxAccusa.getChildren().add(labelAccusa);
		vboxAccusa.getChildren().add(sepOrr2);
		vboxAccusa.getChildren().add(labelIstruzioni);
		vboxAccusa.getChildren().add(sepOrr3);
		vboxAccusa.getChildren().add(hboxSelectStanza);
		vboxAccusa.getChildren().add(sepOrr4);
		vboxAccusa.getChildren().add(hboxSelectArma);
		vboxAccusa.getChildren().add(sepOrr5);
		vboxAccusa.getChildren().add(hboxSelectSospettato);
		vboxAccusa.getChildren().add(sepOrr6);
		vboxAccusa.getChildren().add(hboxResult);
		vboxAccusa.getChildren().add(sepOrr7);
		vboxAccusa.getChildren().add(this.buttonConfermaAccusa);
		vboxAccusa.getChildren().add(sepOrr8);
		vboxAccusa.getChildren().add(buttonEsci);
		this.vboxFase.getChildren().add(vboxAccusa);
		
		
		
	}
	@FXML public void confermaAccusa(ActionEvent event)
	{
		this.textFieldRisultatoAccusa.setVisible(true);
		this.buttonEsci.setVisible(true);
		
		// check terna
		boolean trovatoStanza = false;
		boolean trovatoArma = false;
		boolean trovatoSospettato = false;
		for(CartaIndizio c : this.partita.getTernaDelDelitto().getTerna())
		{
			if(c.getTipo().equals(TipoCarta.STANZA))
				if(c.getDescrizione().equalsIgnoreCase(this.comboBoxAccusaStanze.getValue()))
					trovatoStanza = true;
			else if(c.getTipo().equals(TipoCarta.ARMA))
				if(c.getDescrizione().equalsIgnoreCase(this.comboBoxAccusaArmi.getValue()))
					trovatoStanza = true;
			else if(c.getTipo().equals(TipoCarta.SOSPETTATO))
				if(c.getDescrizione().equalsIgnoreCase(this.comboBoxAccusaSospettati.getValue()))
					trovatoStanza = true;
		}
		if(trovatoStanza && trovatoArma && trovatoSospettato)
			this.textFieldRisultatoAccusa.setText("Complimenti, hai vinto!!!");
		else this.textFieldRisultatoAccusa.setText("Hai perso, andra' meglio la prossima volta!");
		
		// disabilitare bottoni
		this.buttonMovimento.setDisable(true);
		this.buttonIndagine.setDisable(true);
		this.buttonAccusa.setDisable(true);
		this.buttonFineTurno.setDisable(true);
		this.buttonApriTaccuino.setDisable(true);
		this.buttonApriBloccoNote.setDisable(true);
				
	}
	@FXML public void selezionaFineTurno(ActionEvent event) throws IOException
	{
		
	}
	
// STRUMENTI
	@FXML public void selezionaTaccuino(ActionEvent event) throws IOException
	{
		System.out.println("[Sistema] Giocatore ha aperto il Taccuino.");
		
		if(this.vboxFase.getChildren().size() > 1)
			this.vboxFase.getChildren().remove(1);
		
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaPartita/ViewTaccuino.fxml"));
		VBox viewTaccuino = (VBox) loader.load();
		this.vboxFase.getChildren().add(viewTaccuino);
		
		// disabilitare bottoni
		/*this.buttonMovimento.setDisable(true);
		this.buttonIndagine.setDisable(true);
		this.buttonAccusa.setDisable(true);
		this.buttonFineTurno.setDisable(true);
		this.buttonApriTaccuino.setDisable(true);
		this.buttonApriBloccoNote.setDisable(true);*/
	}
	@FXML public void selezionaBloccoNote(ActionEvent event) throws IOException
	{
		System.out.println("[Sistema] Giocatore ha aperto il BloccoNote.");
		
		if(this.vboxFase.getChildren().size() > 1)
			this.vboxFase.getChildren().remove(1);
		
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaPartita/ViewBloccoNote.fxml"));
		VBox viewBloccoNote = (VBox) loader.load();
		this.vboxFase.getChildren().add(viewBloccoNote);
		
		// disabilitare bottoni
		/*this.buttonMovimento.setDisable(true);
		this.buttonIndagine.setDisable(true);
		this.buttonAccusa.setDisable(true);
		this.buttonFineTurno.setDisable(true);
		this.buttonApriTaccuino.setDisable(true);
		this.buttonApriBloccoNote.setDisable(true);*/
	}
	
	
	public Partita getPartita()
	{
		return this.partita;
	}
	
}
