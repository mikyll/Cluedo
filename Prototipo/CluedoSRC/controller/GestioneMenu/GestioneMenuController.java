package controller.GestioneMenu;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import _clientApplicazione.ClientApplicazione;
import controller.GestionePartita.GestionePartitaController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.impostazioni.Impostazioni;
import model.impostazioni.ImpostazioniParserXML;
import model.impostazioni.MusicPlayer;
import model.menu.RegoleAiuto;
import model.menu.Tutorial;
import model.partita.Difficolta;

public class GestioneMenuController {
	private static final String versionID = "Alpha v1.0";
	private static final int TICK_DELAY = 3000;
	
	// controlli generali
	@FXML private AnchorPane base;
	@FXML private AnchorPane baseImpostazioni;
	@FXML private AnchorPane baseCrediti;
	@FXML private Button buttonBack;
	
	// controlli HomeUtente	
	@FXML private Button buttonGiocatoreSingolo;
	@FXML private Button buttonMultigiocatore;
	@FXML private Button buttonRegoleAiuto;
	@FXML private Button buttonImpostazioni;
	@FXML private Button buttonCrediti;
	
	// controlli ViewGiocatoreSingolo
	@FXML private Spinner<Integer> spinnerNumero;
	@FXML private ComboBox<String> comboBoxDifficolta;
	@FXML private Button buttonAvviaPartita;
	
	// controlli ViewRegoleAiuto
	@FXML private ImageView imageViewRules;
	@FXML private Button buttonPreviousRule;
	@FXML private Button buttonNextRule;
	@FXML private Button buttonAvviaTutorial;
	private RegoleAiuto rules;
	private int counterImageRules;
	
	// controlli ViewTutorial
	@FXML private Button buttonPrevious;
	@FXML private Button buttonPlayPause;
	@FXML private Button buttonNext;
	@FXML private ImageView imageViewPlayPause;
	private boolean enablePlay = true; // enable (true = play, false = pause)
	private Timer timerTutorial;
	@FXML private ImageView imageViewTutorial;
	private Tutorial tutorial;
	private int counterImageTutorial;
	
	
	// controlli ViewImpostazioni
	@FXML private CheckBox checkBoxMusica;
	@FXML private Slider sliderVolume; 
	@FXML private TextField textFieldVolume;
	@FXML private CheckBox checkBoxEffetti;
	@FXML private Slider sliderAudio; 
	@FXML private TextField textFieldAudio;
	@FXML private ComboBox<String> comboBoxLingua;
	@FXML private Button buttonRipristina;
	@FXML private Button salvaEdEsci;
	private Impostazioni impostazioni;
	private NumberFormat formatterNoFractionDigits;	// per percentuali (nessun decimale)
	private MusicPlayer musicPlayer;
	
	// controlli ViewCrediti
	@FXML private TextArea textAreaCrediti;
	
	// quando viene istanziato il controller, la sequenza è: -> costruttore -> initialize
	@FXML public void initialize() throws IOException // NB: viene chiamato ogni volta che ci si sposta nel MenuUtente => MusicPlayer non deve cambiare, l'istanza dev'essere la stessa
	{
		// CARICAMENTO IMPOSTAZIONI DA FILE .xml
		if((this.impostazioni = ImpostazioniParserXML.loadImpostazioni()) == null)
		{
			ImpostazioniParserXML.createDefaultImpostazioni();
			this.impostazioni = ImpostazioniParserXML.loadImpostazioni();
		}
		
		// if PRIMO ACCESSO
		//
		//
		//
		
		// System.out.println(this.impostazioni.toString());
		this.formatterNoFractionDigits = NumberFormat.getInstance();
		this.formatterNoFractionDigits.setMaximumFractionDigits(0);
		
		String musicTitle = "David Fesliyan - Unfolding Revelation (Menu).mp3";
		this.musicPlayer = MusicPlayer.getIstanza(musicTitle);
		this.musicPlayer.getMediaPlayer().setVolume(this.impostazioni.getVolumeMusica() / 100);
		this.musicPlayer.getMediaPlayer().setCycleCount(Integer.MAX_VALUE); // per ripetere la traccia ad oltranza
		if(this.impostazioni.isEnabledMusica())
		{
			this.musicPlayer.getMediaPlayer().play();
			System.out.println("Music playing (" + this.formatterNoFractionDigits.format(this.impostazioni.getVolumeMusica()) + "%): " + musicTitle);
		}
		else this.musicPlayer.getMediaPlayer().stop();
		// caricamento tutorial
        this.tutorial = new Tutorial();
        this.counterImageTutorial = 0;
        
        this.rules = new RegoleAiuto();
        this.counterImageRules = 0;
    }
	
// NAVIGAZIONE MENU
	@FXML public void selezionaAvviaTutorial(ActionEvent event) throws IOException 
	{
		// mostra ViewTutorial
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/ViewTutorial.fxml"));
		Stage stage = (Stage) this.buttonAvviaTutorial.getScene().getWindow();
		AnchorPane viewTutorial = (AnchorPane) loader.load();
		Scene scene = new Scene(viewTutorial);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
		
		
		// ottengo i componenti
		ImageView imageViewTutorial = (ImageView) ((VBox)viewTutorial.getChildren().get(0)).getChildren().get(0);
		
		// inizializzo i componenti
		imageViewTutorial.setImage(this.tutorial.getListaImmagini().get(this.counterImageTutorial));
	}
	@FXML public void selezionaHomeUtente(ActionEvent event) throws IOException
	{
		// mostra HomeUtente
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/HomeUtente.fxml"));
		Stage stage = (Stage) this.buttonBack.getScene().getWindow();
		AnchorPane homeUtente = (AnchorPane) loader.load();
		Scene scene = new Scene(homeUtente);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
		
		
		if(this.timerTutorial != null) // se si torna indietro da ViewTutorial (timer != null) => il timer viene cancellato
			this.timerTutorial.cancel();
	}
	@FXML public void selezionaGiocatoreSingolo(ActionEvent event) throws IOException 
	{
		// mostra ViewGiocatoreSingolo
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/ViewGiocatoreSingolo.fxml"));
		Stage stage = (Stage) this.buttonGiocatoreSingolo.getScene().getWindow();
		AnchorPane viewGiocatoreSingolo = (AnchorPane) loader.load();
		Scene scene = new Scene(viewGiocatoreSingolo);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
		
		
		// ottengo i componenti
		Spinner<Integer> spinnerNumero = (Spinner<Integer>) ((HBox) ((VBox) viewGiocatoreSingolo.getChildren().get(0)).getChildren().get(1)).getChildren().get(1);
		ComboBox<String> comboBoxDifficolta = (ComboBox<String>) ((HBox) ((VBox) viewGiocatoreSingolo.getChildren().get(0)).getChildren().get(3)).getChildren().get(1);
		
		// inizializzo i componenti
		spinnerNumero.setValueFactory(new IntegerSpinnerValueFactory(2, 6));
		spinnerNumero.getValueFactory().setValue(2);
		comboBoxDifficolta.setItems(FXCollections.observableArrayList(this.getModalitaAsString()));
		comboBoxDifficolta.setValue(Difficolta.FACILE.toString());
	}
	@FXML public void selezionaMultigiocatore(ActionEvent event) throws IOException 
	{
		// mostra HomeMultigiocatore
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/HomeMultigiocatore.fxml"));
		Stage stage = (Stage) this.buttonMultigiocatore.getScene().getWindow();
		AnchorPane homeMultigiocatore = (AnchorPane) loader.load();
		Scene scene = new Scene(homeMultigiocatore);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
	}
	@FXML public void selezionaRegoleAiuto(ActionEvent event) throws IOException 
	{
		// mostra ViewRegoleAiuto
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/ViewRegoleAiuto.fxml"));
		Stage stage = (Stage) this.buttonRegoleAiuto.getScene().getWindow();
		AnchorPane viewRegoleAiuto = (AnchorPane) loader.load();
		Scene scene = new Scene(viewRegoleAiuto);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
		
		
		/*VBox vbox = (VBox) viewRegoleAiuto.getChildren().get(0);
		WebView webView = new WebView();
		webView.getEngine().load("http://www.google.com");
		
		URL url = this.getClass().getResource("/altro/Regole.html");
		VBox vbox = (VBox) viewRegoleAiuto.getChildren().get(0);
		WebView webView = new WebView();
		webView.getEngine().load(url.toString());
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().add(webView);
		for(Node n : vbox.getChildren())
		{
			System.out.println(n.toString());
		}*/
		/*WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();
		webEngine.load("/altro/Regole.html");*/
	}
	@FXML public void selezionaImpostazioni(ActionEvent event) throws IOException 
	{
		// mostra ViewImpostazioni
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/ViewImpostazioni.fxml"));
		Stage stage = (Stage) this.buttonImpostazioni.getScene().getWindow();
		AnchorPane viewImpostazioni = (AnchorPane) loader.load();
		Scene scene = new Scene(viewImpostazioni);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
		
		
		// ottengo i vari componenti
		CheckBox checkBoxMusica = (CheckBox) ((HBox) ((VBox) viewImpostazioni.getChildren().get(0)).getChildren().get(1)).getChildren().get(1);
		CheckBox checkBoxEffetti = (CheckBox) ((HBox) ((VBox) viewImpostazioni.getChildren().get(0)).getChildren().get(3)).getChildren().get(1);
		Slider sliderVolume = (Slider) ((HBox) ((VBox) viewImpostazioni.getChildren().get(0)).getChildren().get(1)).getChildren().get(2);
		Slider sliderAudio = (Slider) ((HBox) ((VBox) viewImpostazioni.getChildren().get(0)).getChildren().get(3)).getChildren().get(2);
		TextField textFieldVolume = (TextField) ((HBox) ((VBox) viewImpostazioni.getChildren().get(0)).getChildren().get(1)).getChildren().get(3);
		TextField textFieldAudio = (TextField) ((HBox) ((VBox) viewImpostazioni.getChildren().get(0)).getChildren().get(3)).getChildren().get(3);
		ComboBox<String> comboBoxLingua = (ComboBox<String>) ((HBox) ((VBox) viewImpostazioni.getChildren().get(0)).getChildren().get(5)).getChildren().get(1);
		
		// inizializzo i componenti
		checkBoxMusica.setSelected(this.impostazioni.isEnabledMusica());
		sliderVolume.setSnapToTicks(true);
		sliderVolume.setMajorTickUnit(1.0);
		sliderVolume.setValue(this.impostazioni.getVolumeMusica());
		sliderVolume.setDisable(!this.impostazioni.isEnabledMusica());
		textFieldVolume.setText(String.valueOf(this.formatterNoFractionDigits.format(this.impostazioni.getVolumeMusica()) + "%"));
		textFieldVolume.setDisable(!this.impostazioni.isEnabledMusica());		
		checkBoxEffetti.setSelected(this.impostazioni.isEnabledEffetti());
		sliderAudio.setSnapToTicks(true);
		sliderAudio.setMajorTickUnit(1.0);		
		sliderAudio.setValue(this.impostazioni.getEffettiAudio());
		sliderAudio.setDisable(!this.impostazioni.isEnabledEffetti());
		textFieldAudio.setText(String.valueOf(this.formatterNoFractionDigits.format(this.impostazioni.getEffettiAudio()) + "%"));
		textFieldAudio.setDisable(!this.impostazioni.isEnabledEffetti());		
		comboBoxLingua.setItems(FXCollections.observableArrayList(this.impostazioni.getLingueDisponibili()));
		comboBoxLingua.setValue(this.impostazioni.getLingua());
		
		// aggiungo i listeners
		sliderVolume.valueProperty().addListener(e -> {textFieldVolume.setText(this.formatterNoFractionDigits.format(sliderVolume.getValue()) + "%");});
		checkBoxMusica.selectedProperty().addListener(e -> {sliderVolume.setDisable(!checkBoxMusica.isSelected()); textFieldVolume.setDisable(!checkBoxMusica.isSelected());});
		sliderAudio.valueProperty().addListener(e -> {textFieldAudio.setText(this.formatterNoFractionDigits.format(sliderAudio.getValue()) + "%");});
		checkBoxEffetti.selectedProperty().addListener(e -> {sliderAudio.setDisable(!checkBoxEffetti.isSelected()); textFieldAudio.setDisable(!checkBoxEffetti.isSelected());});
	}
	@FXML public void selezionaCrediti(ActionEvent event) throws IOException 
	{
		// mostra ViewCrediti
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/ViewCrediti.fxml"));
		Stage stage = (Stage) this.buttonCrediti.getScene().getWindow();
		AnchorPane viewCrediti = (AnchorPane) loader.load();
		Scene scene = new Scene(viewCrediti);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
		
		
		// ottengo i componenti
		TextArea textAreaCrediti = (TextArea) ((HBox) ((VBox) viewCrediti.getChildren().get(0)).getChildren().get(1)).getChildren().get(1);
		
		// inizializzo i componenti
		String crediti = "Version " + versionID + "\n\n" + 
				"developed by:" + "\n" + "Michele Righi\nLorenzo Righi\nEnrico Sarneri" + "\n\n\n" + 
				"Music by:" + "\n" + "David Fesliyan";
		textAreaCrediti.setEditable(false);
		textAreaCrediti.setMouseTransparent(true);
		textAreaCrediti.setFocusTraversable(false);
		textAreaCrediti.setText(crediti);
	}
	
// TUTORIAL
	@FXML public void argomentoPrecedente(ActionEvent event)
	{
		System.out.println("|<-\tprevious scene");
		this.previousImage();
	}
	@FXML public void argomentoSuccessivo(ActionEvent event)
	{
		System.out.println("->|\tnext scene");
		this.nextImage();
	}
	@FXML public void togglePlayPause(ActionEvent event) // NB: l'icona mostrata è quella per fare l'azione opposta a quella corrente
	{
		if(this.enablePlay) // play
		{
			this.scheduleTimer();
			System.out.println("|>\tplay");
			this.enablePlay = false;
			this.imageViewPlayPause.setImage(new Image("resources/icons/icon-pause.png"));
		}
		else // pause
		{
			this.timerTutorial.cancel();
			System.out.println("||\tpause");
			this.enablePlay = true;
			this.imageViewPlayPause.setImage(new Image("resources/icons/icon-play.png"));
		}
	}
	private void previousImage()
	{
		if(this.counterImageTutorial > 0)
		{
			this.counterImageTutorial--;
			this.imageViewTutorial.setImage(this.tutorial.getListaImmagini().get(this.counterImageTutorial));
		}
	}
	private void nextImage()
	{
		if(this.counterImageTutorial < this.tutorial.getListaImmagini().size() - 1)
		{
			this.counterImageTutorial++;
			this.imageViewTutorial.setImage(this.tutorial.getListaImmagini().get(this.counterImageTutorial));
		}
		else 
		{
			System.out.println("Timer: cancelled");
			this.timerTutorial.cancel();
		}
	}
	
	@FXML public void previousRule(ActionEvent event) 
	{
		if(this.counterImageRules > 0)
		{
			this.counterImageRules--;
			this.imageViewRules.setImage(this.rules.getListaImmagini().get(this.counterImageRules));
		}
	}
	
	@FXML public void nextRule(ActionEvent event) 
	{
		if(this.counterImageRules < this.rules.getListaImmagini().size() - 1)
		{
			this.counterImageRules++;
			this.imageViewRules.setImage(this.rules.getListaImmagini().get(this.counterImageRules));
		}
	}
	
// GIOCATORE SINGOLO
	@FXML public void avviaPartita(ActionEvent event) throws IOException
	{
		System.out.println("[Sistema] Partita avviata (avversari: " + this.spinnerNumero.getValue() + 
				", difficolta: " + this.comboBoxDifficolta.getValue() + ")");
		
		String musicTitle = "George Arkomanis - Relaxation In Mystery (Game).mp3";
		this.musicPlayer.getMediaPlayer().stop();
		this.musicPlayer = null;
		this.musicPlayer = MusicPlayer.createNewMusicPlayer(musicTitle);
		
		// mostra ViewGioco
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaPartita/ViewGioco.fxml"));
		Stage stage = (Stage) this.buttonAvviaPartita.getScene().getWindow();
		loader.setController(new GestionePartitaController(this.impostazioni, stage, this.spinnerNumero.getValue(), this.comboBoxDifficolta.getValue())); // NB: si setta il Controller passandogli le impostazioni (x la musica) e lo stage (x toggle FullScreen)
		AnchorPane viewGioco = (AnchorPane) loader.load();
		Scene scene = new Scene(viewGioco);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
		stage.setFullScreen(true);
		// stage.setResizable(false);
		
		
		/*ListView<String> listViewGiocatori = (ListView<String>) ((HBox) ((VBox) ((HBox) ((VBox) viewGioco.getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).getChildren().get(1)).getChildren().get(0);
		ListView<ImageView> listViewColori = (ListView<ImageView>) ((HBox) ((VBox) ((HBox) ((VBox) viewGioco.getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).getChildren().get(1)).getChildren().get(1);
		
		// TEST
		List<String> giocatori = new ArrayList<String>(6);
		giocatori.add("MarioMarioMario1");
		giocatori.add("MarioMarioMario2");
		giocatori.add("MarioMarioMario3");
		giocatori.add("MarioMarioMario4");
		giocatori.add("MarioMarioMario5");
		giocatori.add("MarioMarioMario6");
		listViewGiocatori.setItems(FXCollections.observableArrayList(giocatori));
		
		List<ImageView> colori = new ArrayList<ImageView>(6);*/
		//colori.add(new ImageView().setImage(new Image(GestioneMenuController.class.getResource("/").toString())));
		
		/*
		// NB: QUESTO LO FACCIO SOLO SE NON METTO FULLSCREEN, ALTRIMENTI SBORDA MALE
		javafx.geometry.Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
		System.out.println("Dimensioni finestra: " + stage.getWidth() + "x" + stage.getHeight());
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		System.out.println(screenSize.getWidth() + "x" + screenSize.getHeight());
		stage.setResizable(false);
		*/
	}
// IMPOSTAZIONI
	@FXML public void ripristinaImpostazioni(ActionEvent event)
	{
		// caricamento file di impostazioni di default (= ripristina)
		ImpostazioniParserXML.createDefaultImpostazioni();
		this.impostazioni = ImpostazioniParserXML.loadImpostazioni();
		
		// aggiorna i controlli
		checkBoxMusica.setSelected(this.impostazioni.isEnabledMusica());
		sliderVolume.setValue(this.impostazioni.getVolumeMusica());
		sliderVolume.setDisable(!this.impostazioni.isEnabledMusica());
		textFieldVolume.setText(String.valueOf(this.formatterNoFractionDigits.format(this.impostazioni.getVolumeMusica()) + "%"));
		textFieldVolume.setDisable(!this.impostazioni.isEnabledMusica());		
		checkBoxEffetti.setSelected(this.impostazioni.isEnabledEffetti());
		sliderAudio.setValue(this.impostazioni.getEffettiAudio());
		sliderAudio.setDisable(!this.impostazioni.isEnabledEffetti());
		textFieldAudio.setText(String.valueOf(this.formatterNoFractionDigits.format(this.impostazioni.getEffettiAudio()) + "%"));
		textFieldAudio.setDisable(!this.impostazioni.isEnabledEffetti());		
		comboBoxLingua.setValue(this.impostazioni.getLingua());
		
		System.out.println("Impostazioni ripristinate.");
	}
	@FXML public void salvaEdEsci(ActionEvent event) throws IOException
	{
		// aggiorna l'oggetto 'Impostazioni'
		this.impostazioni.setVolumeMusica(this.sliderVolume.getValue());
		this.impostazioni.setEnabledMusica(this.checkBoxMusica.isSelected());
		this.impostazioni.setEffettiAudio(this.sliderAudio.getValue());
		this.impostazioni.setEnabledEffetti(this.checkBoxEffetti.isSelected());
		this.impostazioni.setLingua(this.comboBoxLingua.getValue());
		this.musicPlayer.getMediaPlayer().setVolume(sliderVolume.getValue());
		
		System.out.println("Impostazioni salvate.");
		System.out.println("VolumeMusica: " + this.impostazioni.getVolumeMusica() + "% (" + (this.impostazioni.isEnabledMusica() ? "ON)" : "OFF)")); 
		System.out.println("EffettiAudio: " + this.impostazioni.getEffettiAudio() + "% (" + (this.impostazioni.isEnabledEffetti() ? "ON)" : "OFF)"));
		System.out.println("Lingua: " + this.comboBoxLingua.getValue());
		
		// salvo le impostazioni sul file .xml
		ImpostazioniParserXML.updateImpostazioni(this.impostazioni);
		
		
		// mostra HomeUtente
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/HomeUtente.fxml"));
		Stage stage = (Stage) this.buttonBack.getScene().getWindow();
		AnchorPane homeUtente = (AnchorPane) loader.load();
		Scene scene = new Scene(homeUtente);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
	}
		
// UTILITIES
	private String[] getModalitaAsString()
	{
		Difficolta[] modalita = Difficolta.values();
		String[] modalitaString = new String[modalita.length];
		for(int i = 0; i < modalita.length; i++)
			modalitaString[i] = modalita[i].toString();
		
		return modalitaString;
	}
	private void scheduleTimer()
	{
		this.timerTutorial = new Timer();
		
		this.timerTutorial.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Timer: tick");
				nextImage();
			}
		}, 0, TICK_DELAY);
	}
}

/*for(Node n : ((HBox) ((VBox) viewGiocatoreSingolo.getChildren().get(0)).getChildren().get(3)).getChildren())
{
	System.out.println(n);
}*/

/*FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource(resource));
Stage stage = (Stage) button.getScene().getWindow();
AnchorPane view = (AnchorPane) loader.load();
Scene scene = new Scene(view);
scene.getStylesheets().add(getClass().getResource("/resources/styles/background.css").toExternalForm());
stage.setScene(scene);*/

