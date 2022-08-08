package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import networking.ClientDatagram;
import networking.ServerDatagram;

public class ControllerMenu {
	private SimpleDateFormat tformatter;
	
	private static final Pattern IP_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
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
	@FXML private TextField textFieldNicknameCreate;
	@FXML private Label labelErrorNicknameCreate;
	@FXML private Spinner<Integer> spinnerRoomSizeMin;
	@FXML private Spinner<Integer> spinnerRoomSizeMax;
	@FXML private Button buttonCreateNewLobby;
	
	// Join Existing Lobby controls:
	@FXML private TextField textFieldNicknameJoin;
	@FXML private Label labelErrorNicknameJoin;
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
		this.textFieldNicknameCreate.setText("");
		// set textField color(?)
		this.labelErrorNicknameCreate.setVisible(false);
		// reset spinner
		this.buttonCreateNewLobby.setDisable(true);
	}
	
	@FXML public void selectJoinExistingLobby(ActionEvent event)
	{
		System.out.println("User selected Join Existing Lobby");
		
		this.vboxMultiPlayer.setVisible(false);
		
		this.vboxJoinExistingLobby.setVisible(true);
		
		// Reset fields
		this.textFieldNicknameJoin.setText("");
		// set textField color(?)
		this.labelErrorNicknameJoin.setVisible(false);
		this.textFieldIP.setText("");
		// set textField color(?)
		this.labelErrorIP.setVisible(false);
		this.hboxConnectionJoin.setVisible(false);
		this.buttonJoinExistingLobby.setDisable(true);
	}
	
	// Create New Lobby functions =============================================
	
	// validate Nickname
	
	@FXML public void createNewLobby(ActionEvent event)
	{
		System.out.println("User created a new lobby");
	}
	
	// Join Existing Lobby functions ==========================================
	
	// validate Nickname
	
	// validate IP
	
	@FXML public void joinExistingLobby(ActionEvent event)
	{
		System.out.println("User is trying to join an existing lobby");
	}
	
	// Lobby functions ========================================================
	
	@FXML public void selectLobbySettings(ActionEvent event)
	{
		
	}
	
	// Start Game
	@FXML public void startMultiPlayerGame(ActionEvent event)
	{
		
	}
	
	// select Lobby Settings
	
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
