package it.mikyll.cluedo.controller;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import it.mikyll.cluedo.model.networking.ClientStream;
import it.mikyll.cluedo.model.networking.ServerStream;
import it.mikyll.cluedo.model.networking.User;
import it.mikyll.cluedo.model.networking.message.IMessageHandler;
import it.mikyll.cluedo.model.networking.message.Message;
import it.mikyll.cluedo.model.networking.message.MessageType;

public class ControllerMenu {
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
	@FXML private Spinner<Integer> spinnerLobbySizeMin;
	@FXML private Spinner<Integer> spinnerLobbySizeMax;
	@FXML private TextField textFieldPortCreate;
	@FXML private Label labelErrorPortCreate;
	@FXML private Button buttonCreateNewLobby;
	
	// Join Existing Lobby controls:
	@FXML private TextField textFieldUsernameJoin;
	@FXML private Label labelErrorUsernameJoin;
	@FXML private TextField textFieldIP;
	@FXML private Label labelErrorIP;
	@FXML private TextField textFieldPortJoin;
	@FXML private Label labelErrorPortJoin;
	@FXML private HBox hboxConnectionJoin;
	@FXML private Button buttonJoinExistingLobby;
	
	// Lobby controls:
	@FXML private ListView<HBox> listViewUsers;
	private ArrayList<Label> listLabelUsername = new ArrayList<Label>();
	private ArrayList<Label> listLabelReady = new ArrayList<Label>();
	private ArrayList<Button> listButtonKick = new ArrayList<Button>();
	private ArrayList<Button> listButtonBan = new ArrayList<Button>();
	@FXML private TextArea textAreaChat;
	@FXML private TextField textFieldChat;
	@FXML private Button buttonChatSend;
	@FXML private Button buttonReady;
	@FXML private Button buttonStartMultiPlayer;
	@FXML private HBox hboxIPaddress;
	@FXML private Label labelLobbyLANaddress;
	
	// Lobby bottom-right controls:
	@FXML private Button buttonLobbySettings;
	
	// Lobby Settings controls:
	@FXML private Button buttonCloseLobbySettings;
	@FXML private ListView<HBox> listViewBannedUsers;
	@FXML private TextField textFieldBanUsername;
	@FXML private TextField textFieldBanAddress;
	@FXML private Button buttonBan;
	@FXML private Label labelLobbyPrivacy;
	@FXML private Button buttonLobbyPrivacy;

	// Rules & Help controls:
	// [...]
	
	// Settings controls:
	@FXML private CheckBox checkBoxToggleMusic;
	@FXML private Slider sliderMusicVolume;
	@FXML private CheckBox checkBoxToggleSoundEffects;
	@FXML private Slider sliderSoundEffectsVolume;
	@FXML private Button buttonSaveSettings;
	@FXML private Button buttonCancelSettings;
	
	// Info controls:
	// [...]
	
	private ServerStream server;
	private ClientStream client;
	private String username;
	
	public ControllerMenu() {}
	
	public void initialize()
	{
		// setup panels and text labels visibility
		this.vboxMainMenu.setVisible(true);
		this.vboxSettingsInfoControls.setVisible(true);
		
		this.vboxBackControls.setVisible(false);
		this.vboxSinglePlayer.setVisible(false);
		this.vboxMultiPlayer.setVisible(false);
		this.vboxCreateNewLobby.setVisible(false);
		this.vboxJoinExistingLobby.setVisible(false);
		this.vboxLobby.setVisible(false);
		this.hboxIPaddress.setVisible(false);
		this.vboxLobbySettingsControls.setVisible(false);
		this.vboxLobbySettings.setVisible(false);
		this.vboxRulesHelp.setVisible(false);
		this.vboxSettings.setVisible(false);
		this.vboxInfo.setVisible(false);
		
		this.textFieldPortCreate.setPromptText("default: " + ServerStream.DEFAULT_PORT);
		this.textFieldPortJoin.setPromptText("default: " + ServerStream.DEFAULT_PORT);
		
		this.spinnerLobbySizeMin.valueProperty().addListener((changed, oldval, newval) -> {
			this.spinnerLobbySizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newval, 6, this.spinnerLobbySizeMax.getValue()));
		});
		this.spinnerLobbySizeMax.valueProperty().addListener((changed, oldval, newval) -> {
			this.spinnerLobbySizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, newval, this.spinnerLobbySizeMin.getValue()));
		});
		
		this.listViewUsers.setItems(FXCollections.observableArrayList());
		this.listViewBannedUsers.setItems(FXCollections.observableArrayList());
		
		Platform.runLater(() -> {
			this.vboxMainMenu.getScene().setOnKeyPressed(e -> {
				if(e.getCode() == KeyCode.ESCAPE)
					this.selectBack(new ActionEvent());
			});
		});
	}
	
	// MainMenu functions =====================================================
	@FXML public void selectSinglePlayer(ActionEvent event) 
	{
		System.out.println("User selected SinglePlayer");
		
		this.vboxMainMenu.setVisible(false);
		this.vboxSettingsInfoControls.setVisible(false);
		
		// request focus on first element
				
		this.vboxSinglePlayer.setVisible(true);
		this.vboxBackControls.setVisible(true);
	}
	@FXML public void selectMultiPlayer(ActionEvent event) 
	{
		System.out.println("User selected MultiPlayer");
		
		this.vboxMainMenu.setVisible(false);
		this.vboxSettingsInfoControls.setVisible(false);
		
		Platform.runLater(()->this.buttonSelectCreateNewLobby.requestFocus());
		
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
			Alert alert = new Alert(AlertType.CONFIRMATION, "Leave the lobby?", ButtonType.YES, ButtonType.NO);
			alert.setTitle("Confirmation Dialog");
			alert.setContentText("Are you sure you want to leave the lobby?");
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES)
			{
				this.closeConnection();
				
				this.vboxLobby.setVisible(false);
				this.hboxIPaddress.setVisible(false);
				this.vboxLobbySettingsControls.setVisible(false);
				
				this.vboxMultiPlayer.setVisible(true);
			}
		}
	}
	
	// SinglePlayer functions =================================================
	
	@FXML public void startSinglePlayerGame(ActionEvent event)
	{
		// to-do
		try {
			FXMLLoader loader = new FXMLLoader(ControllerMenu.class.getResource("/it/mikyll/cluedo/views/ViewGame.fxml"));
			Stage stage = (Stage) this.vboxMainMenu.getScene().getWindow();
			loader.setController(new ControllerGame());
			BorderPane game = (BorderPane) loader.load();
		
			Scene scene = new Scene(game);
			scene.getStylesheets().add(ControllerMenu.class.getResource("/it/mikyll/cluedo/styles/application.css").toExternalForm());
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
		this.textFieldUsernameCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.textFieldUsernameCreate.requestFocus();
		this.labelErrorUsernameCreate.setVisible(false);
		
		// reset spinner
		this.spinnerLobbySizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 2));
		this.spinnerLobbySizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 6));
		
		this.textFieldPortCreate.setText("");
		this.textFieldPortCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorPortCreate.setVisible(false);
		
		this.buttonCreateNewLobby.setDisable(!validateUsername(this.textFieldUsernameCreate.getText()));
		
		this.buttonStartMultiPlayer.setVisible(true);
		this.buttonReady.setVisible(false);
	}
	
	@FXML public void selectJoinExistingLobby(ActionEvent event)
	{
		System.out.println("User selected Join Existing Lobby");
		
		this.vboxMultiPlayer.setVisible(false);
		
		this.vboxJoinExistingLobby.setVisible(true);
		
		// Reset fields
		this.textFieldUsernameJoin.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.textFieldUsernameJoin.requestFocus();
		this.labelErrorUsernameJoin.setVisible(false);
		
		this.textFieldIP.setText("");
		this.textFieldIP.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorIP.setVisible(false);
		
		this.textFieldPortJoin.setText("");
		this.textFieldPortCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorPortJoin.setVisible(false);
		
		this.hboxConnectionJoin.setVisible(false);
		
		this.buttonJoinExistingLobby.setDisable(!validateUsername(this.textFieldUsernameJoin.getText()));
	
		this.buttonStartMultiPlayer.setVisible(false);
		this.buttonReady.setVisible(true);
		this.buttonReady.setText(" Not ready");
		this.buttonReady.setStyle("-fx-background-color: red");
	}
	
	// Create New Lobby functions =============================================
	public static boolean validateUsername(String username)
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
	
	@FXML private boolean checkEnableCreateNewLobby()
	{
		boolean disableCreateButton = false, errorUsername = false, errorPort = false;
		
		if(!validateUsername(this.textFieldUsernameCreate.getText()))
		{
			disableCreateButton = true;
			errorUsername = true;
		}
		
		if(!(this.validatePort(this.textFieldPortCreate.getText()) || this.textFieldPortCreate.getText().isEmpty()))
		{
			disableCreateButton = true;
			errorPort = true;
		}
		
		this.buttonCreateNewLobby.setDisable(disableCreateButton);
		this.labelErrorUsernameCreate.setVisible(errorUsername);
		this.textFieldUsernameCreate.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorPortCreate.setVisible(errorPort);
		this.textFieldPortCreate.setStyle(errorPort ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		
		return !disableCreateButton;
	}
	
	@FXML public void createNewLobby(ActionEvent event)
	{
		if (!this.checkEnableCreateNewLobby())
			return;
		
		if(this.textFieldPortCreate.getText().isEmpty())
			this.textFieldPortCreate.setText("" + ServerStream.DEFAULT_PORT);
		
		this.clearLists();
		this.clearChat();
		this.username = this.textFieldUsernameCreate.getText();
		
		// create new room -> start server (if OK switch to Server Room View)
		try{
			this.server = new ServerStream(
					this.username,
					Integer.parseInt(this.textFieldPortCreate.getText()),
					this.spinnerLobbySizeMin.getValue(),
					this.spinnerLobbySizeMax.getValue(),
					true);
					//this.lobbyMessageHandler);
			this.client = null;
		} catch (IOException e) {
			System.out.println("Server: ServerSocket creation failed");
			if(e instanceof BindException)
			{
				System.out.println("Server: another socket is already binded to this address and port");
				try(final DatagramSocket socket = new DatagramSocket()) {
					socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
					String privateIP = socket.getLocalAddress().getHostAddress();
					
					Alert alert = new Alert(AlertType.ERROR, "Room creation failed");
					alert.setTitle("Error Dialog");
					alert.setContentText("Another socket is already binded to " + privateIP + ":" + this.textFieldPortCreate.getText());
					alert.show();
					
					return;
				} catch (SocketException e1) {
					e.printStackTrace();
				} catch (UnknownHostException e1) {
					e.printStackTrace();
				}
			}
		}
		
		this.vboxCreateNewLobby.setVisible(false);
		
		this.vboxLobby.setVisible(true);
		this.vboxLobbySettingsControls.setVisible(true);
		this.buttonLobbySettings.setDisable(false);
		
		this.setServerAddress();
		this.hboxIPaddress.setVisible(true);
		this.labelLobbyPrivacy.setId("privacyOpen");
		this.buttonLobbyPrivacy.setText(" Open");
		
		this.addUser(this.username, true, true);
		
		this.addJoinMessage(new Message(MessageType.CHAT, this.username, ""));
		this.buttonStartMultiPlayer.setDisable(true);
	}
	
	// Join Existing Lobby functions ==========================================
	private boolean validateIPv4(String address)
	{
		return PATTERN_IP.matcher(address).matches();
	}
	
	@FXML private boolean checkEnableJoinExistingLobby()
	{
		boolean disableJoinButton = false, errorUsername = false, errorIP = false, errorPort = false;
		
		if(!validateUsername(this.textFieldUsernameJoin.getText()))
		{
			disableJoinButton = true;
			errorUsername = true;
		}
		
		if(!(this.validateIPv4(this.textFieldIP.getText()) || this.textFieldIP.getText().isEmpty()))
		{
			disableJoinButton = true;
			errorIP = true;
		}
		
		if(!(this.validatePort(this.textFieldPortJoin.getText()) || this.textFieldPortJoin.getText().isEmpty()))
		{
			disableJoinButton = true;
			errorPort = true;
		}
		
		this.buttonJoinExistingLobby.setDisable(disableJoinButton);
		this.labelErrorUsernameJoin.setVisible(errorUsername);
		this.textFieldUsernameJoin.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorIP.setVisible(errorIP);
		this.textFieldIP.setStyle(errorIP ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorPortJoin.setVisible(errorPort);
		this.textFieldPortJoin.setStyle(errorPort ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
	
		return !disableJoinButton;
	}
	
	@FXML public void joinExistingLobby(ActionEvent event)
	{
		if (!this.checkEnableJoinExistingLobby())
			return;
		
		if(this.textFieldIP.getText().isEmpty())
			this.textFieldIP.setText("127.0.0.1");
		if(this.textFieldPortJoin.getText().isEmpty())
			this.textFieldPortJoin.setText("" + ServerStream.DEFAULT_PORT);
		
		this.clearLists();
		this.clearChat();
		this.username = this.textFieldUsernameJoin.getText();
		
		this.hboxConnectionJoin.setVisible(true);
		
		// connect to existing room -> start client (if OK switch to Client Room View)
		this.client = new ClientStream(
				this.username,
				this.textFieldIP.getText(),
				Integer.parseInt(this.textFieldPortJoin.getText()));
				//this.lobbyMessageHandler);
		this.server = null;
		
		System.out.println("User is trying to join an existing lobby");
	}
	
	// Lobby functions ========================================================
	
	@FXML public void selectLobbySettings(ActionEvent event)
	{
		System.out.println("User selected Lobby Settings");
		
		this.buttonBack.setDisable(true);
		this.buttonLobbySettings.setDisable(true);
		
		this.textFieldBanUsername.setText("");
		this.textFieldBanUsername.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.textFieldBanAddress.setText("");
		this.textFieldBanAddress.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.buttonBan.setDisable(true);
		
		this.vboxLobbySettings.setVisible(true);
	}
	
	@FXML public void closeLobbySettings(ActionEvent event)
	{
		System.out.println("User closed Lobby Settings");
		
		this.vboxLobbySettings.setVisible(false);
		
		this.buttonBack.setDisable(false);
		this.buttonLobbySettings.setDisable(false);
	}
	
	@FXML public void ban(ActionEvent event)
	{
		if (!this.checkEnableBanUser())
			return;
		
		this.banUser(this.textFieldBanUsername.getText(), this.textFieldBanAddress.getText());
	}
	@FXML public void toggleLobbyPrivacy(ActionEvent event)
	{
		if(this.buttonLobbyPrivacy.getText().contains("Open"))
		{
			this.labelLobbyPrivacy.setId("privacyClosed");
			this.buttonLobbyPrivacy.setText("Closed");
			
			this.server.setPrivacy(false);
		}
		else
		{
			this.labelLobbyPrivacy.setId("privacyOpen");
			this.buttonLobbyPrivacy.setText(" Open");
			
			this.server.setPrivacy(true);
		}
	}
	
	@FXML public void toggleReady(ActionEvent event)
	{
		boolean ready = this.buttonReady.getText().trim().equalsIgnoreCase("Ready");
		ready = !ready;
		
		this.buttonReady.setText(ready ? "Ready" : "Not ready");
		this.buttonReady.setStyle("-fx-background-color: " + (ready ? "lime" : "red"));
		
		this.setReady(this.username, ready);
		
		this.client.sendReady(ready);
		
		// TO-DO: set a 5 sec timer that disables the button, so that users can't spam the toggle
	}
	
	@FXML public void checkEnableSendChat(KeyEvent event)
	{
		if(this.textFieldChat.getText().length() > 100)
		{
			this.textFieldChat.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
			this.buttonChatSend.setDisable(true);
		}
		else
		{
			this.textFieldChat.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
			this.buttonChatSend.setDisable(false);
		}
    }
	
	/*
	 * Send CHAT message
	 */
	@FXML public void send(ActionEvent event)
	{
		if(!this.textFieldChat.getText().isEmpty() && this.textFieldChat.getText().length() <= 100)
		{
			this.server.sendChatMessage(this.textFieldChat.getText());
			this.addChatMessage(new Message(MessageType.CHAT, this.username, this.textFieldChat.getText()));
			
			this.textFieldChat.clear();
		}
		
	}
	
	// Start Game
	@FXML public void startMultiPlayerGame(ActionEvent event)
	{
		System.out.println("User started the game");
			
		// TO-DO
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
	
	// Lobby Settings =========================================================
	@FXML private boolean checkEnableBanUser()
	{
		boolean disableBanButton = false, errorUsername = false, errorIP = false;
		
		if(this.textFieldBanUsername.getText().isEmpty() &&
				this.textFieldBanAddress.getText().isEmpty())
		{
			disableBanButton = true;
		}
		if(!validateUsername(this.textFieldBanUsername.getText()))
		{
			errorUsername = true;
		}
		if(!this.validateIPv4(this.textFieldBanAddress.getText()))
		{
			errorIP = true;
			if(errorUsername)
				disableBanButton = true;
		}
		if(validateUsername(this.textFieldBanUsername.getText()))
		{
			if(!this.validateIPv4(this.textFieldBanAddress.getText()) &&
					!this.textFieldBanAddress.getText().isEmpty())
				disableBanButton = true;
		}
		if(this.validateIPv4(this.textFieldBanAddress.getText()))
		{
			if(!validateUsername(this.textFieldBanUsername.getText()) &&
					!this.textFieldBanUsername.getText().isEmpty())
				disableBanButton = true;
		}
		
		if(this.textFieldBanUsername.getText().equals(this.textFieldUsernameCreate.getText()))
		{
			disableBanButton = true;
			errorUsername = true;
		}
		
		this.buttonBan.setDisable(disableBanButton);
		this.textFieldBanUsername.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.textFieldBanAddress.setStyle(errorIP ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		
		return !disableBanButton;
	}
	
	// Utilities ==============================================================
	private void closeConnection()
	{
		if(this.server != null)
		{
			this.server.sendClose();
			this.server = null;
		}
		if(this.client != null)
		{
			this.client.sendClose();
			this.client = null;
		}
	}
	
	// it's called only by client, when it receive a user_list
	private void setUsers(List<User> users)
	{
		this.clearLists();
		
		if(this.client != null)
		{
			this.addUser(users.get(0).getUsername(), true, true);
			for(int i = 1; i < users.size(); i++)
			{
				this.addUser(users.get(i).getUsername(), false, users.get(i).isReady());
			}
		}
	}
	
	private void addUser(String username, boolean isHost, boolean isReady)
	{
		if(this.server != null)
		{
			HBox el = this.buildUserServerElement(username, isHost, isReady);
			
			this.listViewUsers.getItems().add(el);
			
			this.listLabelUsername.add((Label) el.getChildren().get(0));
			this.listLabelReady.add((Label) el.getChildren().get(1));
			this.listButtonKick.add((Button) el.getChildren().get(2));
			this.listButtonBan.add((Button) el.getChildren().get(3));
			
			// Highlight user
			Label l = this.listLabelUsername.get(this.listLabelUsername.size() - 1);
			if(l.getText().equals(this.username))
				l.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14.0));
				
		}
		else if(this.client != null)
		{
			HBox el = this.buildUserClientElement(username, isHost, isReady);
			
			this.listViewUsers.getItems().add(el);
			
			this.listLabelUsername.add((Label) el.getChildren().get(0));
			this.listLabelReady.add((Label) el.getChildren().get(1));
			
			// Highlight user
			Label l = this.listLabelUsername.get(this.listLabelUsername.size() - 1);
			if(l.getText().equals(this.username))
				l.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14.0));
		}
	}
	
	private void removeUser(String username)
	{
		if(this.server != null)
		{
			for(int i = 0; i < this.listLabelUsername.size(); i++)
			{
				if(this.listLabelUsername.get(i).getText().equals(username))
				{
					this.listLabelUsername.remove(i);
					this.listLabelReady.remove(i);
					this.listButtonKick.remove(i);
					this.listButtonBan.remove(i);
					
					this.listViewUsers.getItems().remove(i);
				}
			}
		}
		else if(this.client != null)
		{
			for(int i = 0; i < this.listLabelUsername.size(); i++)
			{
				if(this.listLabelUsername.get(i).getText().equals(username))
				{
					this.listLabelUsername.remove(i);
					this.listLabelReady.remove(i);
					
					this.listViewUsers.getItems().remove(i);
				}
			}
		}
	}
	
	private void setReady(String username, boolean ready)
	{
		for(int i = 1; i < this.listLabelUsername.size(); i++)
		{
			if(this.listLabelUsername.get(i).getText().equals(username))
			{
				this.listLabelReady.get(i).setStyle("-fx-background-radius: 30; -fx-background-color: " + (ready ? "lime" : "red"));
			}
		}
		
		if(server != null)
		{
			// TO-DO check if can enable START
			if(this.server.canStart())
				this.buttonStartMultiPlayer.setDisable(false);
		}
	}
	
	private void kickUser(String username)
	{
		this.addKickMessage(new Message(MessageType.KICK, username, ""));
		
		this.removeUser(username);
		
		this.server.sendKickUser(username);
		
		this.buttonStartMultiPlayer.setDisable(!this.server.canStart());
	}
	
	private void banUser(String username, String address)
	{
		if(username == null && address == null)
		{
			return;
		}
		if(username != null)
		{
			this.addBanMessage(new Message(MessageType.BAN, username, ""));
			
			this.removeUser(username);
		}
		else
		{
			try {
				String sUsername = this.server.getUsernameFromAddress(InetAddress.getByName(address));
				
				this.removeUser(sUsername);
			} catch (UnknownHostException e) {System.out.println("Address not found");}
		}
		
		// add to banned list (LobbySettings)
		this.listViewBannedUsers.getItems().add(this.buildBannedUserElement(username, address));
		
		this.server.sendBanUser(username, address);
		
		this.buttonStartMultiPlayer.setDisable(!this.server.canStart());
	}
	
	private void removeFromBanList(HBox row, String username, String address)
	{
		this.listViewBannedUsers.getItems().remove(row);
		
		if(username != null)
		{
			this.addRevokeBanMessage(new Message(MessageType.BAN, username, ""));
		}
		
		this.server.removeBan(username, address);
	}
	
	private void addChatMessage(Message message)
	{
		this.textAreaChat.setText(this.textAreaChat.getText() + (this.textAreaChat.getText().isEmpty() ? "" : "\n") +
			message.getTimestamp() + " " + message.getUsername() + ": " + message.getContent());
	}
	private void addJoinMessage(Message message)
	{
		this.textAreaChat.setText(this.textAreaChat.getText() + (this.textAreaChat.getText().isEmpty() ? "" : "\n") +
			message.getTimestamp() + " User '" + message.getUsername() + "' has joined the lobby");
	}
	private void addLeaveMessage(Message message)
	{
		this.textAreaChat.setText(this.textAreaChat.getText() + (this.textAreaChat.getText().isEmpty() ? "" : "\n") +
				message.getTimestamp() + " User '" + message.getUsername() + "' has left the lobby");
	}
	private void addKickMessage(Message message)
	{
		this.textAreaChat.setText(this.textAreaChat.getText() + (this.textAreaChat.getText().isEmpty() ? "" : "\n") +
				message.getTimestamp() + " User '" + message.getUsername() + "' has been kicked out");
	}
	private void addBanMessage(Message message)
	{
		this.textAreaChat.setText(this.textAreaChat.getText() + (this.textAreaChat.getText().isEmpty() ? "" : "\n") +
				message.getTimestamp() + " User '" + message.getUsername() + "' has been banned from the lobby");
	}
	private void addRevokeBanMessage(Message message)
	{
		this.textAreaChat.setText(this.textAreaChat.getText() + (this.textAreaChat.getText().isEmpty() ? "" : "\n") +
				message.getTimestamp() + " User '" + message.getUsername() + "' is no longer banned from the lobby");
	}
	// private addChatMessage()
	
	// close Lobby Settings
	
	// Component builders
	
	private HBox buildUserServerElement(String username, boolean isHost, boolean isReady)
	{
		HBox result = new HBox();
		result.setPrefHeight(30.0);
		result.setAlignment(Pos.CENTER_LEFT);
		result.setSpacing(5.0);
		
		Label lUsername = new Label(username);
		lUsername.setPrefWidth(200.0);
		lUsername.setFont(Font.font("System", 14));
		lUsername.setTextFill(Color.WHITE);
		
		Label lReady = new Label();
		lReady.setPrefSize(25.0, 25.0);
		if(isHost)
		{
			lReady.setId("host");
			Tooltip tR = new Tooltip("User '" + username + "' is the host");
			tR.setFont(Font.font(14.0));
			lReady.setTooltip(tR);
		}
		else lReady.setStyle("-fx-background-radius: 30; -fx-background-color: " + (isReady ? "lime" : "red"));
		
		Button bKick = new Button();
		bKick.setPrefSize(25.0, 25.0);
		bKick.setId("kick");
		// add callback
		Tooltip tK = new Tooltip("Kick user '" + username + "'");
		tK.setFont(Font.font(14.0));
		bKick.setTooltip(tK);
		bKick.setOnAction(e -> {
			this.kickUser(username);
		});
		
		Button bBan = new Button();
		bBan.setPrefSize(25.0, 25.0);
		bBan.setId("ban");
		// add callback
		Tooltip tB = new Tooltip("Ban user '" + username + "'");
		tB.setFont(Font.font(14.0));
		bBan.setTooltip(tB);
		bBan.setOnAction((ActionEvent e) -> {
			this.banUser(username, this.server.getAddressFromUsername(username).toString());
		});
		
		bKick.setVisible(!isHost);
		bBan.setVisible(!isHost);
		
		// TO-DO: highlight
		
		result.getChildren().addAll(lUsername, lReady, bKick, bBan);
		
		return result;
	}
	
	private HBox buildUserClientElement(String username, boolean isHost, boolean isReady)
	{
		HBox result = new HBox();
		result.setPrefHeight(30.0);
		result.setAlignment(Pos.CENTER_LEFT);
		result.setSpacing(5.0);
		
		Label lUsername = new Label(username);
		lUsername.setPrefWidth(200.0);
		lUsername.setFont(Font.font("System", 14));
		lUsername.setTextFill(Color.WHITE);
		
		Label lReady = new Label();
		lReady.setPrefSize(25.0, 25.0);
		if(isHost)
		{
			lReady.setId("host");
			Tooltip tR = new Tooltip("User '" + username + "' is the host");
			tR.setFont(Font.font(14.0));
			lReady.setTooltip(tR);
		}
		else lReady.setStyle("-fx-background-radius: 30; -fx-background-color: " + (isReady ? "lime" : "red"));
		
		
		result.getChildren().addAll(lUsername, lReady);
		
		return result;
	}
	
	private HBox buildBannedUserElement(String username, String address)
	{
		HBox result = new HBox();
		result.setPrefWidth(390.0);
		result.setAlignment(Pos.CENTER_LEFT);
		result.setSpacing(5.0);
		
		Label lUsername = new Label(username == null ? "-" : username);
		lUsername.setPrefWidth(150.0);
		lUsername.setFont(Font.font("System", 14));
		lUsername.setTextFill(Color.WHITE);
		
		Label lAddress = new Label(address == null ? "-" : address);
		lAddress.setPrefWidth(150.0);
		lAddress.setFont(Font.font("System", 14));
		lAddress.setTextFill(Color.WHITE);
		
		Button bRevokeBan = new Button();
		bRevokeBan.setPrefSize(25.0, 25.0);
		bRevokeBan.setId("revokeBan");
		// add callback
		Tooltip tRB = new Tooltip("Revoke ban");
		tRB.setFont(Font.font(14.0));
		bRevokeBan.setTooltip(tRB);
		bRevokeBan.setOnAction((ActionEvent e) -> {
			this.removeFromBanList(result, username, address);
		});
		
		result.getChildren().addAll(lUsername, lAddress, bRevokeBan);
		
		return result;
	}
	
	// Handle Lobby Messages ==================================================
	
	public IMessageHandler lobbyMessageHandler = (Message msg) -> {
		Platform.runLater(() -> {
			switch(msg.getMsgType())
			{
				case CONNECT_REQUEST:
				{
					// add user to list
					this.addUser(msg.getUsername(), false, false);
					this.buttonStartMultiPlayer.setDisable(true);
					
					// add chat message
					this.addJoinMessage(msg);
					
					break;
				}
				case CONNECT_ACCEPTED:
				{
					this.hboxConnectionJoin.setVisible(false);
					this.vboxJoinExistingLobby.setVisible(false);
					this.vboxLobby.setVisible(true);
					
					// set user list
					this.setUsers(User.stringToUserList(msg.getContent()));
					
					// add chat message
					msg.setUsername(this.username);
					this.addJoinMessage(msg);
					
					break;
				}
				
				case CONNECT_REFUSED:
				{
					Alert alert = new Alert(AlertType.ERROR, "Connection failed");
					alert.setTitle("Error Dialog");
					alert.setContentText(msg.getContent());
					alert.show();
					this.hboxConnectionJoin.setVisible(false);
					
					break;
				}
					
				case USER_JOINED:
				{
					if(this.client != null)
					{
						// add to user list
						this.addUser(msg.getUsername(), false, false);
						
						// add chat message
						this.addJoinMessage(msg);
					}
					
					
					break;
				}
				
				case USER_LIST:
				{
					break;
				}
					
				case DISCONNECT:
				{
					if(this.server != null)
					{
						this.addLeaveMessage(msg);
						
						this.removeUser(msg.getUsername());
					}
					if(this.client != null)
					{
						if(msg.getUsername().equals(this.username))
						{
							this.selectBack(new ActionEvent());
							
							Alert alert = new Alert(AlertType.INFORMATION, "Disconnected from server");
							alert.setTitle("Error Dialog");
							alert.setContentText(msg.getContent());
							alert.show();
						}
						else
						{
							this.addLeaveMessage(msg);
							this.removeUser(msg.getUsername());
						}
					}
					
					break;
				}
					
				case CHAT:
				{
					this.addChatMessage(msg);
					
					break;
				}
					
				case READY:
				{
					this.setReady(msg.getUsername(), Boolean.parseBoolean(msg.getContent()));
					
					if(this.server != null)
					{
						this.buttonStartMultiPlayer.setDisable(!this.server.canStart());
					}
					
					break;
				}
					
				case KICK:
				{
					if(msg.getUsername().equals(this.username))
					{
						this.selectBack(new ActionEvent());
						
						Alert alert = new Alert(AlertType.INFORMATION, "Disconnected from server");
						alert.setTitle("Error Dialog");
						alert.setContentText(msg.getContent());
						alert.show();
					}
					else
					{
						this.removeUser(msg.getUsername());
						
						this.addKickMessage(msg);
					}
					
					break;
				}
					
				case BAN:
				{
					if(msg.getUsername().equals(this.username))
					{
						this.selectBack(new ActionEvent());
						
						Alert alert = new Alert(AlertType.INFORMATION, "Disconnected from server");
						alert.setTitle("Error Dialog");
						alert.setContentText(msg.getContent());
						alert.show();
					}
					else
					{
						this.removeUser(msg.getUsername());
						
						this.addBanMessage(msg);
					}
					
					break;
				}

				case START_GAME:
					break;
					
				default:
					System.out.println("Unknown Message Type");
					break;
			}
		});
	};
	
	
	
	private void setServerAddress()
	{
		try(final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			String privateIP = socket.getLocalAddress().getHostAddress();
			this.labelLobbyLANaddress.setText(privateIP);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private void clearLists()
	{
		this.listViewUsers.getItems().clear();
		this.listViewBannedUsers.getItems().clear();
		
		this.listLabelUsername.clear();
		this.listLabelReady.clear();
		this.listButtonKick.clear();
		this.listButtonBan.clear();
	}
	
	private void clearChat()
	{
		this.textAreaChat.setText("");
		this.textFieldChat.setText("");
		this.textFieldChat.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.buttonChatSend.setDisable(false);
	}


	// Settings functions =====================================================
	@FXML public void toggleMusic(ActionEvent e)
	{
		MusicPlayer player = MusicPlayer.getInstance();
		if (checkBoxToggleMusic.isSelected())
		{
			sliderMusicVolume.setDisable(false);
			player.play();
		}
		else
		{
			sliderMusicVolume.setDisable(true);
			player.stop();
		}
	}

	@FXML public void updateMusicVolume(MouseEvent e)
	{
		MusicPlayer player = MusicPlayer.getInstance();
		player.setVolume(sliderMusicVolume.getValue() / 100);
	}

	@FXML public void toggleSoundEffects(ActionEvent e)
	{
		if (checkBoxToggleSoundEffects.isSelected())
		{
			sliderSoundEffectsVolume.setDisable(false);
			// TODO
		}
		else
		{
			sliderSoundEffectsVolume.setDisable(true);
			// TODO
		}
	}

	@FXML public void updateSoundEffectsVolume(MouseEvent e)
	{
		MusicPlayer player = MusicPlayer.getInstance();
		player.setVolume(sliderMusicVolume.getValue() / 100);
	}

	@FXML public void saveSettings(ActionEvent e)
	{
		Settings settings = Settings.getInstance();
		// language
		settings.setMusicEnabled(checkBoxToggleMusic.isSelected());
		settings.setMusicVolume(sliderMusicVolume.getValue());
		settings.setSoundEffectsEnabled(checkBoxToggleSoundEffects.isSelected());
		settings.setSoundEffectsVolume(sliderSoundEffectsVolume.getValue());
		//SettingsRepository.saveSettings(settings);
	}

	@FXML public void cancelSettings(ActionEvent e)
	{

	}
}
