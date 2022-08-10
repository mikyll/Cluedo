package controller;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.networking.ClientStream;
import model.networking.ServerStream;
import model.networking.User;
import model.networking.message.IMessageHandler;
import model.networking.message.Message;
import model.networking.message.MessageType;

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
	@FXML private Spinner<Integer> spinnerRoomSizeMin;
	@FXML private Spinner<Integer> spinnerRoomSizeMax;
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
	
	private ServerStream server;
	private ClientStream client;
	
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
		this.vboxLobbySettingsControls.setVisible(false);
		this.vboxRulesHelp.setVisible(false);
		this.vboxSettings.setVisible(false);
		this.vboxInfo.setVisible(false);
		
		this.textFieldPortCreate.setPromptText("default: " + ServerStream.DEFAULT_PORT);
		this.textFieldPortJoin.setPromptText("default: " + ServerStream.DEFAULT_PORT);
		
		this.spinnerRoomSizeMin.valueProperty().addListener((changed, oldval, newval) -> {
			this.spinnerRoomSizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newval, 6, this.spinnerRoomSizeMax.getValue()));
		});
		this.spinnerRoomSizeMax.valueProperty().addListener((changed, oldval, newval) -> {
			this.spinnerRoomSizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, newval, this.spinnerRoomSizeMin.getValue()));
		});
		
		this.listViewUsers.setItems(FXCollections.observableArrayList());
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
			this.closeConnection();
			
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
		this.textFieldUsernameCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.textFieldUsernameCreate.requestFocus();
		this.labelErrorUsernameCreate.setVisible(false);
		
		// reset spinner
		this.spinnerRoomSizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 2));
		this.spinnerRoomSizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 6));
		
		this.textFieldPortCreate.setText("");
		this.textFieldPortCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
		this.labelErrorPortCreate.setVisible(false);
		
		this.buttonCreateNewLobby.setDisable(!this.validateUsername(this.textFieldUsernameCreate.getText()));
		
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
		
		this.buttonJoinExistingLobby.setDisable(!this.validateUsername(this.textFieldUsernameJoin.getText()));
	
		this.buttonStartMultiPlayer.setVisible(false);
		this.buttonReady.setVisible(true);
		this.buttonReady.setText(" Not ready");
		this.buttonReady.setStyle("-fx-background-color: red");
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
	
	@FXML private boolean checkEnableCreateNewLobby()
	{
		boolean disableCreateButton = false, errorUsername = false, errorPort = false;
		
		if(!this.validateUsername(this.textFieldUsernameCreate.getText()))
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
		
		// create new room -> start server (if OK switch to Server Room View)
		try{
			this.server = new ServerStream(
					this.textFieldUsernameCreate.getText(),
					Integer.parseInt(this.textFieldPortCreate.getText()),
					this.spinnerRoomSizeMin.getValue(),
					this.spinnerRoomSizeMax.getValue(),
					this.lobbyMessageHandler);
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
		
		this.addUser(this.textFieldUsernameCreate.getText(), true, true);
		
		this.addJoinMessage(new Message(MessageType.CHAT, Message.getCurrentTimestamp(), this.textFieldUsernameCreate.getText(), ""));
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
		
		this.hboxConnectionJoin.setVisible(true);
		
		// connect to existing room -> start client (if OK switch to Client Room View)
		this.client = new ClientStream(
				this.textFieldUsernameJoin.getText(),
				this.textFieldIP.getText(),
				Integer.parseInt(this.textFieldPortJoin.getText()),
				this.lobbyMessageHandler);
		this.server = null;
		
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
	
	@FXML public void toggleReady(ActionEvent event)
	{
		boolean ready = this.buttonReady.getText().trim().equalsIgnoreCase("Ready");
		ready = !ready;
		
		this.buttonReady.setText(ready ? "Ready" : "Not ready");
		this.buttonReady.setStyle("-fx-background-color: " + (ready ? "lime" : "red"));
		
		this.setReady(this.textFieldUsernameJoin.getText(), ready);
		
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
			if(this.server != null)
			{
				this.server.sendChatMessage(this.textFieldChat.getText());
				this.addChatMessage(new Message(MessageType.CHAT, Message.getCurrentTimestamp(), this.textFieldUsernameCreate.getText(), this.textFieldChat.getText()));
			}
			else if(this.client != null)
			{
				this.client.sendChatMessage(this.textFieldChat.getText());
				this.addChatMessage(new Message(MessageType.CHAT, Message.getCurrentTimestamp(), this.textFieldUsernameJoin.getText(), this.textFieldChat.getText()));
			}
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
			if(l.getText().equals(this.textFieldUsernameCreate.getText()))
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
			if(l.getText().equals(this.textFieldUsernameJoin.getText()))
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
		this.removeUser(username);
		
		this.server.kickUser(username);
	}
	
	private void banUser(String username, String address)
	{
		if(username != null)
			this.removeUser(username);
		else
		{
			try {
				this.server.getUsernameFromAddress(InetAddress.getByName(address));
			} catch (UnknownHostException e) {System.out.println("Address not found");}
		}
		
		// add to banned list
		
		this.server.banUser(username, address);
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
	// private addChatMessage()
	
	// close Lobby Settings
	
	//////////////////////////
	
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
				case CONNECT_OK:
				{
					this.hboxConnectionJoin.setVisible(false);
					this.vboxJoinExistingLobby.setVisible(false);
					this.vboxLobby.setVisible(true);
					
					// set user list
					this.setUsers(User.stringToUserList(msg.getContent()));
					
					// add chat message
					msg.setUsername(this.textFieldUsernameJoin.getText());
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
						if(msg.getUsername().equals(this.textFieldUsernameJoin.getText()))
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
					break;
					
				case BAN:
					break;
					
				case START_GAME:
					break;
					
				default:
					System.out.println("Unknown Message Type");
					break;
			}
		});
	};
	
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
			System.out.println(e);
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
	
	private void clearLists()
	{
		this.listViewUsers.getItems().clear();
		
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
}
