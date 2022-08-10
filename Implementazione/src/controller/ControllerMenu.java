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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
	private ArrayList<Label> listLabelKick = new ArrayList<Label>();
	private ArrayList<Label> listLabelBan = new ArrayList<Label>();
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
	
	@FXML private boolean checkEnableCreateLobby()
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
		
		return disableCreateButton;
	}
	
	@FXML public void createNewLobby(ActionEvent event)
	{
		// Validate
		
		if(this.textFieldPortCreate.getText().isEmpty())
			this.textFieldPortCreate.setText("" + ServerStream.DEFAULT_PORT);
		
		this.clearLists();
		
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
					alert.setHeaderText("Room creation failed");
					alert.setContentText("Another socket is already binded to " + privateIP + ":" + this.textFieldPortCreate.getText());
					alert.showAndWait();
					
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

		this.textAreaChat.setText(Message.getCurrentTimestamp() + " Lobby created\n" +
				Message.getCurrentTimestamp() + " User " + this.textFieldUsernameCreate.getText() + " has joined the lobby");
	}
	
	// Join Existing Lobby functions ==========================================
	private boolean validateIPv4(String address)
	{
		return PATTERN_IP.matcher(address).matches();
	}
	
	@FXML private boolean checkEnableJoinLobby()
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
	
		return disableJoinButton;
	}
	
	@FXML public void joinExistingLobby(ActionEvent event)
	{
		// Validate
		
		if(this.textFieldIP.getText().isEmpty())
			this.textFieldIP.setText("127.0.0.1");
		if(this.textFieldPortJoin.getText().isEmpty())
			this.textFieldPortJoin.setText("" + ServerStream.DEFAULT_PORT);
		
		this.clearLists();
		
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
		Platform.runLater(() -> {
			if(this.server != null)
			{
				HBox el = this.buildUserServerElement(username, isHost, isReady);
				
				this.listViewUsers.getItems().add(el);
				
				this.listLabelUsername.add((Label) el.getChildren().get(0));
				this.listLabelReady.add((Label) el.getChildren().get(1));
				this.listLabelKick.add((Label) el.getChildren().get(2));
				this.listLabelBan.add((Label) el.getChildren().get(3));
				
				this.buttonStartMultiPlayer.setDisable(true);
			}
			else if(this.client != null)
			{
				HBox el = this.buildUserClientElement(username, isHost, isReady);
				
				this.listViewUsers.getItems().add(el);
				
				this.listLabelUsername.add((Label) el.getChildren().get(0));
				this.listLabelReady.add((Label) el.getChildren().get(1));
			}
		});
	}
	
	@FXML public void send(ActionEvent event)
	{
		// TEST
		/*for(HBox el : this.listViewUsers.getItems())
		{
			System.out.println(((Label) el.getChildren().get(0)).getText());
		}*/
		
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
		
		
	}
	
	private void removeUser(User user)
	{
		
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
		}
	}
	
	private void banUser(String username, String address)
	{
		
	}
	
	private void addChatMessage(Message message)
	{
		this.textAreaChat.appendText(this.textAreaChat.getText().isEmpty() ? "" : "\n" +
			message.getTimestamp() + " " + message.getUsername() + ": " + message.getContent());
	}
	private void addJoinMessage(Message message)
	{
		this.textAreaChat.appendText(this.textAreaChat.getText().isEmpty() ? "" : "\n" +
			message.getTimestamp() + " User " + message.getUsername() + " has joined the lobby");
	}
	// private addChatMessage()
	
	// close Lobby Settings
	
	//////////////////////////
	
	public IMessageHandler lobbyMessageHandler = (Message msg) -> {
		switch(msg.getMsgType())
		{
		case CONNECT_REQUEST:
		{
			// add user to list
			this.addUser(msg.getUsername(), false, false);
			
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
			// add to user list
			
			// add chat message
			
			break;
		}
		
		case USER_LIST:
		{
			break;
		}
			
		case DISCONNECT:
		{
			break;
		}
			
		case CHAT:
		{
			System.out.println("prova");
			this.addChatMessage(msg);
			
			break;
		}
			
		case READY:
		{
			this.setReady(msg.getUsername(), Boolean.parseBoolean(msg.getContent()));
			
			// enable start button if everyone's ready
			
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
	};
	
	private HBox buildUserServerElement(String username, boolean isHost, boolean isReady)
	{
		HBox result = new HBox();
		
		Label lNickname = new Label(username);
		
		
		Label lReady = new Label();
		lReady.setPrefSize(25.0, 25.0);
		lReady.setStyle("-fx-background-radius: 30; -fx-background-color: " + (isReady ? "lime" : "red"));
		
		Label lKick = new Label("Kick");
		// add callback
		
		Label lBan = new Label("Ban");
		// add callback
		
		lReady.setVisible(!isHost);
		lKick.setVisible(!isHost);
		lBan.setVisible(!isHost);
		
		// TO-DO: highlight
		
		result.getChildren().addAll(lNickname, lReady, lKick, lBan);
		
		return result;
	}
	
	private HBox buildUserClientElement(String username, boolean isHost, boolean isReady)
	{
		HBox result = new HBox();
		
		Label lNickname = new Label(username);
		
		Label lReady = new Label();
		lReady.setPrefSize(25.0, 25.0);
		lReady.setStyle("-fx-background-radius: 30; -fx-background-color: " + (isReady ? "lime" : "red"));
		
		lReady.setVisible(!isHost);
		
		result.getChildren().addAll(lNickname, lReady);
		
		return result;
	}
	
	private void clearLists()
	{
		this.listViewUsers.getItems().clear();
		
		this.listLabelUsername.clear();
		this.listLabelReady.clear();
		this.listLabelKick.clear();
		this.listLabelBan.clear();
	}
}
