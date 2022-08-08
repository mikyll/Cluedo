package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.networking.ServerStream;
import model.networking.message.IMessageHandler;
import model.networking.message.Message;

public class ControllerMenu {
	private SimpleDateFormat tformatter;
	
	private static final Pattern PATTERN_USERNAME = Pattern.compile("^[a-zA-Z0-9]{3,15}$");
	private static final Pattern PATTERN_IP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
	@FXML private VBox vboxMainMenu;
	@FXML private VBox vboxSettingsInfoControls;
	@FXML private VBox vboxBackControls;
	@FXML private VBox vboxSinglePlayer;
	@FXML private VBox vboxMultiPlayer;
	@FXML private VBox vboxCreateNewLobby;
	@FXML private VBox vboxJoinExistingLobby;
	@FXML private VBox vboxLobby;
	@FXML private VBox vboxLobbySettingsControls;
	@FXML private VBox vboxLobbySettings;
	@FXML private VBox vboxRulesHelp;
	@FXML private VBox vboxSettings;
	@FXML private VBox vboxInfo;
	
	// Main Menu controls:
	@FXML private Button buttonSinglePlayer;
	@FXML private Button buttonMultiPlayer;
	@FXML private Button buttonRulesHelp;
	
	// Bottom-right controls:
	@FXML private Button buttonSettings;
	@FXML private Button buttonInfo;
	
	// Bottom-left controls:
	@FXML private Button buttonBack;
	
	// SinglePlayer controls:
	// [...] settings controls
	@FXML private Button buttonStartSinglePlayer;
	
	// MultiPlayer cotrols:
	@FXML private Button buttonSelectCreateNewLobby;
	@FXML private Button buttonSelectJoinExistingLobby;
	
	// Create New Lobby controls:
	@FXML private TextField textFieldUsernameCreate;
	@FXML private Label labelErrorUsernameCreate;
	@FXML private Spinner<Integer> spinnerRoomSizeMin;
	@FXML private Spinner<Integer> spinnerRoomSizeMax;
	@FXML private TextField textFieldPort;
	@FXML private Label labelErrorPort;
	@FXML private Button buttonCreateNewLobby;
	
	// Join Existing Lobby controls:
	@FXML private TextField textFieldUsernameJoin;
	@FXML private Label labelErrorUsernameJoin;
	@FXML private TextField textFieldIP;
	@FXML private Label labelErrorIP;
	@FXML private HBox hboxConnectionJoin;
	@FXML private Button buttonJoinExistingLobby;
	
	// Lobby controls:
	@FXML private ListView<HBox> listViewUsers;
	@FXML private TextArea textAreaChat;
	@FXML private TextField textFieldChat;
	@FXML private Button buttonChatSend;
	@FXML private Button buttonStartMultiPlayer;
	
	// Lobby bottom-right controls:
	@FXML private Button buttonLobbySettings;
	
	// Lobby Settings controls:
	// [...]
	
	// Rules & Help controls:
	// [...]
	
	// Settings controls:
	// [...]
	
	// Info controls:
	// [...]
	
	public IMessageHandler messageHandler = (Message msg) -> {
		switch(msg.getMsgType())
		{
		
		case CONNECT_REQUEST:
			break;
			
		case CONNECT_OK:
			break;
			
		case CONNECT_REFUSED:
			break;
			
		case USER_JOINED:
			break;
			
		case USER_LIST:
			break;
			
		case DISCONNECT:
			break;
			
		case CHAT:
			break;
			
		case READY:
			break;
			
		case KICK:
			break;
			
		case BAN:
			break;
			
		case START_GAME:
			break;
			
		default:
			System.out.println("Unknown Message Type");
			break;
		
		}
	};
	
	
	public ControllerMenu() {}
	
	public void initialize()
	{
		this.tformatter = new SimpleDateFormat("[HH:mm:ss]");
		
		// setup panels and text labels visibility
		this.vboxMainMenu.setVisible(true);
		this.vboxSettingsInfoControls.setVisible(true);
		
		this.vboxBackControls.setVisible(false);
		this.vboxSinglePlayer.setVisible(false);
		this.vboxMultiPlayer.setVisible(false);
		this.vboxCreateNewLobby.setVisible(false);
		this.vboxJoinExistingLobby.setVisible(false);
		this.vboxLobby.setVisible(false);
		this.vboxLobbySettingsControls.setVisible(false);
		this.vboxRulesHelp.setVisible(false);
		this.vboxSettings.setVisible(false);
		this.vboxInfo.setVisible(false);
		
		this.textFieldPort.setPromptText("" + ServerStream.DEFAULT_PORT);
		
		this.spinnerRoomSizeMin.valueProperty().addListener((changed, oldval, newval) -> {
			this.spinnerRoomSizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newval, 6, this.spinnerRoomSizeMax.getValue()));
		});
		this.spinnerRoomSizeMax.valueProperty().addListener((changed, oldval, newval) -> {
			this.spinnerRoomSizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, newval, this.spinnerRoomSizeMin.getValue()));
		});
	}
	
	// MainMenu functions =====================================================
	@FXML public void selectSinglePlayer(ActionEvent event) 
	{
		System.out.println("User selected SinglePlayer");
		
		this.vboxMainMenu.setVisible(false);
		this.vboxSettingsInfoControls.setVisible(false);
				
		this.vboxSinglePlayer.setVisible(true);
		this.vboxBackControls.setVisible(true);
	}
	@FXML public void selectMultiPlayer(ActionEvent event) 
	{
		System.out.println("User selected MultiPlayer");
		
		this.vboxMainMenu.setVisible(false);
		this.vboxSettingsInfoControls.setVisible(false);
		
		this.vboxMultiPlayer.setVisible(true);
		this.vboxBackControls.setVisible(true);	
	}
	@FXML public void selectRulesHelp(ActionEvent event) 
	{
		System.out.println("User selected Rules & Help");
		
		this.vboxMainMenu.setVisible(false);
		this.vboxSettingsInfoControls.setVisible(false);
		
		this.vboxRulesHelp.setVisible(true);
		this.vboxBackControls.setVisible(true);
	}
	
	@FXML public void selectSettings(ActionEvent event) 
	{
		System.out.println("User selected Settings");
		
		this.vboxMainMenu.setVisible(false);
		this.vboxSettingsInfoControls.setVisible(false);
		
		this.vboxSettings.setVisible(true);
		this.vboxBackControls.setVisible(true);
	}
	@FXML public void selectInfo(ActionEvent event) 
	{
		System.out.println("User selected Info");
		
		this.vboxMainMenu.setVisible(false);
		this.vboxSettingsInfoControls.setVisible(false);
		
		this.vboxInfo.setVisible(true);
		this.vboxBackControls.setVisible(true);
	}
	
	@FXML public void selectBack(ActionEvent event)
	{
		System.out.println("User selected Back");
		
		if(this.vboxSinglePlayer.isVisible())
		{
			this.vboxSinglePlayer.setVisible(false);
			this.vboxBackControls.setVisible(false);
			
			this.vboxMainMenu.setVisible(true);
			this.vboxSettingsInfoControls.setVisible(true);
		}
		else if(this.vboxMultiPlayer.isVisible())
		{
			this.vboxMultiPlayer.setVisible(false);
			this.vboxBackControls.setVisible(false);
			
			this.vboxMainMenu.setVisible(true);
			this.vboxSettingsInfoControls.setVisible(true);
		}
		else if(this.vboxRulesHelp.isVisible())
		{
			this.vboxRulesHelp.setVisible(false);
			this.vboxBackControls.setVisible(false);
			
			this.vboxMainMenu.setVisible(true);
			this.vboxSettingsInfoControls.setVisible(true);
		}
		else if(this.vboxSettings.isVisible())
		{
			this.vboxSettings.setVisible(false);
			this.vboxBackControls.setVisible(false);
			
			this.vboxMainMenu.setVisible(true);
			this.vboxSettingsInfoControls.setVisible(true);
		}
		else if(this.vboxInfo.isVisible())
		{
			this.vboxInfo.setVisible(false);
			this.vboxBackControls.setVisible(false);
			
			this.vboxMainMenu.setVisible(true);
			this.vboxSettingsInfoControls.setVisible(true);
		}
		else if(this.vboxCreateNewLobby.isVisible())
		{
			this.vboxCreateNewLobby.setVisible(false);
			
			this.vboxMultiPlayer.setVisible(true);
		}
		else if(this.vboxJoinExistingLobby.isVisible())
		{
			this.vboxJoinExistingLobby.setVisible(false);
			
			this.vboxMultiPlayer.setVisible(true);
		}
		else if(this.vboxLobby.isVisible())
		{
			this.vboxLobby.setVisible(false);
			this.vboxLobbySettingsControls.setVisible(false);
			
			this.vboxMultiPlayer.setVisible(true);
		}
	}
	
	// SinglePlayer functions =================================================
	
	@FXML public void startSinglePlayerGame(ActionEvent event)
	{
		// to-do
		try {
			FXMLLoader loader = new FXMLLoader(ControllerMenu.class.getResource("/view/ViewGame.fxml"));
			Stage stage = (Stage) this.vboxMainMenu.getScene().getWindow();
			loader.setController(new ControllerGame());
			AnchorPane quiz = (AnchorPane) loader.load();
		
			Scene scene = new Scene(quiz);
			scene.getStylesheets().add(ControllerMenu.class.getResource("/application/application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE: " + e.getMessage());
			System.exit(1);
		}
	}
	
	// MultiPlayer functions ==================================================
	@FXML public void selectCreateNewLobby(ActionEvent event)
	{
		System.out.println("User selected Create New Lobby");
		
		this.vboxMultiPlayer.setVisible(false);
		
		this.vboxCreateNewLobby.setVisible(true);
		
		// Reset fields
		this.textFieldUsernameCreate.setText("");
		// set textField color(?)
		this.labelErrorUsernameCreate.setVisible(false);
		
		// reset spinner
		//this.spinnerRoomSizeMin.set
		
		this.textFieldPort.setText("");
		this.textFieldPort.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorPort.setVisible(false);
		
		this.buttonCreateNewLobby.setDisable(true);
	}
	
	@FXML public void selectJoinExistingLobby(ActionEvent event)
	{
		System.out.println("User selected Join Existing Lobby");
		
		this.vboxMultiPlayer.setVisible(false);
		
		this.vboxJoinExistingLobby.setVisible(true);
		
		// Reset fields
		this.textFieldUsernameJoin.setText("");
		this.textFieldUsernameJoin.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorUsernameJoin.setVisible(false);
		
		this.textFieldIP.setText("");
		this.textFieldIP.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorIP.setVisible(false);
		
		this.hboxConnectionJoin.setVisible(false);
		this.buttonJoinExistingLobby.setDisable(true);
	}
	
	// Create New Lobby functions =============================================
	private boolean validateUsername(String username)
	{
		return PATTERN_USERNAME.matcher(username).matches();
	}
	
	private boolean validatePort(String port)
	{
		boolean result;
		int p;
		
		try {
			p = Integer.parseInt(port);
			result = (p > 1023 && p < 65536);
		} catch(Exception e) {
			result = false;
		}
		
		return result;
	}
	
	@FXML private void checkEnableCreateLobby()
	{
		boolean disableCreateButton = false, errorUsername = false, errorPort = false;
		
		if(!this.validateUsername(this.textFieldUsernameCreate.getText()))
		{
			disableCreateButton = true;
			errorUsername = true;
		}
		
		if(!(this.validatePort(this.textFieldPort.getText()) || this.textFieldPort.getText().isEmpty()))
		{
			disableCreateButton = true;
			errorPort = true;
		}
		
		this.buttonCreateNewLobby.setDisable(disableCreateButton);
		this.labelErrorUsernameCreate.setVisible(errorUsername);
		this.textFieldUsernameCreate.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorPort.setVisible(errorPort);
		this.textFieldPort.setStyle(errorPort ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
	
	}
	
	@FXML public void createNewLobby(ActionEvent event)
	{
		System.out.println("User created a new lobby");
		
		this.vboxCreateNewLobby.setVisible(false);
		
		this.vboxLobby.setVisible(true);
		this.vboxLobbySettingsControls.setVisible(true);
		this.buttonLobbySettings.setDisable(false);
	}
	
	// Join Existing Lobby functions ==========================================
	private boolean validateIPv4(String address)
	{
		return PATTERN_IP.matcher(address).matches();
	}
	
	@FXML private void checkEnableJoinLobby()
	{
		boolean disableJoinButton = false, errorUsername = false, errorIP = false;
		
		if(!this.validateUsername(this.textFieldUsernameJoin.getText()))
		{
			disableJoinButton = true;
			errorUsername = true;
		}
		
		if(!(this.validateIPv4(this.textFieldIP.getText()) || this.textFieldIP.getText().isEmpty()))
		{
			disableJoinButton = true;
			errorIP = true;
		}
		
		this.buttonJoinExistingLobby.setDisable(disableJoinButton);
		this.labelErrorUsernameJoin.setVisible(errorUsername);
		this.textFieldUsernameJoin.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorIP.setVisible(errorIP);
		this.textFieldIP.setStyle(errorIP ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
	}
	
	@FXML public void joinExistingLobby(ActionEvent event)
	{
		System.out.println("User is trying to join an existing lobby");
	}
	
	// Lobby functions ========================================================
	
	@FXML public void selectLobbySettings(ActionEvent event)
	{
		System.out.println("User selected Lobby Settings");
		
		this.buttonLobbySettings.setDisable(true);
	}
	
	@FXML public void closeLobbySettings(ActionEvent event)
	{
		System.out.println("User closed Lobby Settings");
	}
	
	// Start Game
	@FXML public void startMultiPlayerGame(ActionEvent event)
	{
		System.out.println("User started the game");
			
		// to-do
		try {
			FXMLLoader loader = new FXMLLoader(ControllerMenu.class.getResource("/view/ViewGame.fxml"));
			Stage stage = (Stage) this.vboxMainMenu.getScene().getWindow();
			loader.setController(new ControllerGame());
			BorderPane basePane = (BorderPane) loader.load();
		
			Scene scene = new Scene(basePane);
			scene.getStylesheets().add(ControllerMenu.class.getResource("/application/application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE: " + e.getMessage());
			System.exit(1);
		}
	}
	
	// private addChatMessage()
	
	// close Lobby Settings
	
	//////////////////////////
	
	/*@FXML public void selectCNR(ActionEvent event)
	{
		System.out.println("User selected Create New Room");
		
		// to-do: init arrays and pass them to server + add function to kick button listener.
		
		this.playerList = new ArrayList<Label>();
		this.readyList = new ArrayList<Label>();
		this.kickList = new ArrayList<Button>();
		for(int i = 0; i < 6; i++)
		{
			HBox hbox = (HBox) this.vboxSCPL.getChildren().get(i);
			this.playerList.add((Label)hbox.getChildren().get(0));
			this.readyList.add((Label)hbox.getChildren().get(1));
			this.kickList.add((Button)hbox.getChildren().get(2));
			System.out.println(((Label)hbox.getChildren().get(0)).getText() + ((Label)hbox.getChildren().get(1)).getText());
			if(i != 0)
				hbox.setVisible(false);
		}
		this.playerList.get(0).setText("•1. " + this.textFieldNickname.getText());
		
		// to-do: server code
		this.server = new ServerDatagram(this.textFieldNickname.getText(), this.textAreaChatS, this.playerList, this.readyList);
		
		this.vboxMultiPlayer.setVisible(false);
		this.vboxLobbyServer.setVisible(true);
		this.labelIP.setVisible(true);
		String address = "";
		try {address = InetAddress.getLocalHost().toString().split("/")[1];} catch (UnknownHostException e) {e.printStackTrace();}
		this.labelIP.setText("IP address: " + address);
		this.isServer = true;
		
	}
	@FXML public void validateIP()
	{
		if(!IP_PATTERN.matcher(this.textFieldIP.getText()).matches())
		{
			this.buttonJER.setDisable(true);
			this.labelErrorIP.setVisible(true);
		}
		else {
			this.buttonJER.setDisable(false);
			this.labelErrorIP.setVisible(false);
		}
	}
	@FXML public void selectJER(ActionEvent event)
	{
		System.out.println("User selected Join Existing Room");
		
		this.buttonCreateLobby.setDisable(true);
		this.textFieldIP.setDisable(true);
		this.buttonJER.setDisable(true);
		this.hboxC.setVisible(true);
		this.buttonBack.setDisable(true);
		
		// to-do: creazione socket e connessione al server (porta default)
		this.isServer = false;
		try {
			this.client = new ClientDatagram(this.textFieldNickname.getText(), InetAddress.getByName(this.textFieldIP.getText()), this.vboxMultiPlayer, this.vboxLobbyClient, this.buttonBack, this.textAreaChatC);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.client.sendConnect(this.textFieldNickname.getText());
		
	}
	@FXML public void selectSC(ActionEvent event)
	{
		System.out.println("Connection attempt interrupted.");
		
		this.buttonCreateLobby.setDisable(false);
		this.textFieldIP.setDisable(false);
		this.buttonJER.setDisable(false);
		this.hboxC.setVisible(false);
		this.buttonBack.setDisable(false);
		
		
		// to-do: stop connection attempt
	}
	@FXML public void validateChatMessage()
	{
		if((this.textFieldChatC.getText().isEmpty() || this.textFieldChatC.getText().isBlank()) &&
			(this.textFieldChatS.getText().isEmpty() || this.textFieldChatS.getText().isBlank()))
		{
			this.buttonSendMessageC.setDisable(true);
			this.buttonSendMessageS.setDisable(true);
		}
		else
		{
			this.buttonSendMessageC.setDisable(false);
			this.buttonSendMessageS.setDisable(false);
		}
	}
	@FXML public void sendMessageS(ActionEvent event)
	{
		System.out.println("User (server) sent message " + this.textFieldChatS.getText());
		
		Date date = new Date(System.currentTimeMillis());
		String timestamp = tformatter.format(date);
		this.textAreaChatS.setText(this.textAreaChatS.getText() + "\n" + timestamp + " " + this.textFieldNickname.getText() + ": " + this.textFieldChatS.getText());
		
		// send to everyone else
		server.sendChatMessage(this.textFieldNickname.getText(), this.textFieldChatS.getText());
		
		this.textFieldChatS.setText("");
		this.buttonSendMessageS.setDisable(true);
	}
	
	@FXML public void toggleReady(ActionEvent event)
	{
		if(this.buttonReady.getText().equals("Ready"))
		{
			this.buttonReady.setText("Not Ready");
			this.buttonReady.setStyle("-fx-background-color: red");
		}
		else
		{
			this.buttonReady.setText("Ready");
			this.buttonReady.setStyle("-fx-background-color: lime");
		}
	}
	@FXML public void sendMessageC(ActionEvent event)
	{
		System.out.println("User (client) sent message " + this.textFieldChatC.getText());
		
		Date date = new Date(System.currentTimeMillis());
		String timestamp = tformatter.format(date);
		this.textAreaChatC.setText(this.textAreaChatC.getText() + "\n" + timestamp + " " + this.textFieldNickname.getText() + ": " + this.textFieldChatC.getText());
		
		// send to server
		this.client.sendChatMessage(this.textFieldNickname.getText(), this.textFieldChatC.getText());
		
		this.textFieldChatC.setText("");
		this.buttonSendMessageC.setDisable(true);
	}
	
	
	
	
	@FXML public void selectSG(ActionEvent event) 
	{
		System.out.println("User selected Start Game");
		
		// to-do
		try {
			FXMLLoader loader = new FXMLLoader(ControllerMenu.class.getResource("/view/ViewGame.fxml"));
			Stage stage = (Stage) this.vboxMainMenu.getScene().getWindow();
			loader.setController(new ControllerGame());
			AnchorPane quiz = (AnchorPane) loader.load();
		
			Scene scene = new Scene(quiz);
			scene.getStylesheets().add(ControllerMenu.class.getResource("/application/application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE: " + e.getMessage());
			System.exit(1);
		}
	}
	
	@FXML public void selectB(ActionEvent event) 
	{
		System.out.println("User selected Back");
		
		this.vboxSinglePlayer.setVisible(false);
		this.vboxBack.setVisible(false);
		this.vboxMultiPlayer.setVisible(false);
		this.vboxInfo.setVisible(false);
		this.vboxSettings.setVisible(false);
		this.textSP.setVisible(false);
		this.textMP.setVisible(false);
		this.textS.setVisible(false);
		this.textC.setVisible(false);
		this.vboxLobbyServer.setVisible(false);
		this.vboxLobbyClient.setVisible(false);
		
		this.vboxMainMenu.setVisible(true);
		this.vboxSettingsInfo.setVisible(true);
		this.textMM.setVisible(true);
		
	}
	
	@FXML public void restoreImp(ActionEvent event) 
	{
		System.out.println("Settings restored.");
	}
	@FXML public void selectSE(ActionEvent event) 
	{
		System.out.println("User selected Save and Exit");
		
		this.vboxSettings.setVisible(false);
		this.textS.setVisible(false);
		
		this.vboxMainMenu.setVisible(true);
		this.vboxSettingsInfo.setVisible(true);
		this.textMM.setVisible(true);
		
	}
	*/
	
}
