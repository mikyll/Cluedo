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
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import networking.Client2;
import networking.Server2;

public class ControllerMenu {
	private SimpleDateFormat tformatter;
	
	private static final Pattern IP_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
	@FXML private Text textMM;	// text Main Menu
	@FXML private Text textSP;	// text Single-Player
	@FXML private Text textMP;	// text Multi-Player
	@FXML private Text textS;	// text Settings
	@FXML private Text textC;	// text Credits
	
	@FXML private VBox vboxMM;	// vbox Main Menu
	@FXML private VBox vboxSC;	// vbox Settings/Credits
	@FXML private VBox vboxB;	// vbox Back
	@FXML private VBox vboxSP;	// vbox Single-Player
	@FXML private VBox vboxMP;	// vbox Multi-Player
	@FXML private VBox vboxSR;	// vbox Server Room
	@FXML private VBox vboxCR;	// vbox Client Room
	@FXML private VBox vboxS;	// vbox Settings
	@FXML private VBox vboxC;	// vbox Credits
	
	
	
	// vbox Main Menu controls:
	@FXML private Button buttonSP;	// button Single-Player
	@FXML private Button buttonMP;	// button Multi-Player
	@FXML private Button buttonRH;	// button Rules & Help
	
	// vbox Bottom-right controls:
	@FXML private Button buttonS;	// button Settings
	@FXML private Button buttonC;	// button Credits
	
	// vbox Bottom-left controls:
	@FXML private Button buttonB;	// button Back
	
	// vbox Single-Player controls:
	@FXML private Spinner<Integer> spinnerON;	// spinner Opponents Number
	@FXML private ComboBox<String> comboboxDL;	// combobox Difficulty Level
	@FXML private Button buttonSG;				// button Start Game
	
	// vbox Multi-Player cotrols:
	@FXML private Button buttonCNR;				// button Create New Room
	@FXML private TextField textFieldIP;		// textField IP
	@FXML private Button buttonJER;				// button Join Existing Room
	@FXML private Label labelErrorIP;			// label Error IP
	@FXML private HBox hboxC;					// hbox Connecting
	@FXML private Button buttonSC;				// button Stop Connecting
	@FXML private TextField textFieldNickname;	// textField Nickname
	
	// vbox Multi-Player create new room cotrols:
	@FXML private Label labelIP;
	/*TEST socket*/
	@FXML private TextArea textAreaChatS;		// textArea Chat Server
	@FXML private TextField textFieldChatS;		// textField Chat Server
	@FXML private Button buttonSendMessageS;	// button Send Message Server
	@FXML private TextArea textAreaChatC;		// textArea Chat Client
	@FXML private TextField textFieldChatC;		// textField Chat Client
	@FXML private Button buttonSendMessageC;	// button Send Message Client
	
	@FXML private VBox vboxCCPL;			// vbox Client Connected Player List
	@FXML private VBox vboxSCPL;			// vbox Server Connected Player List
	@FXML private Button buttonReady;		// button Ready
	
	private ArrayList<Label> playerList;
	private ArrayList<Label> readyList;
	private ArrayList<Button> kickList;
	private boolean isServer;
	
	// vbox Settings controls:
	@FXML private CheckBox checkboxM;			// checkbox Music
	@FXML private Slider sliderMV;				// slider Music Volume
	@FXML private CheckBox checkboxA;			// checkbox Audio
	@FXML private Slider sliderAV;				// slider Audio Volume
	@FXML private ComboBox<String> comboboxL; 	// combobox Language
	@FXML private Button buttonRD;				// button Restore Defaults
	@FXML private Button buttonSE;				// button Save and Exit
	
	
	
	// test
	private Server2 server;
	private Client2 client;
	
	public ControllerMenu() {}
	
	public void initialize()
	{
		this.tformatter = new SimpleDateFormat("[HH:mm:ss]");
		
		// setup panels and text labels visibility
		this.vboxMM.setVisible(true);
		this.vboxSC.setVisible(true);
		this.textMM.setVisible(true);
		
		this.vboxB.setVisible(false);
		this.vboxSP.setVisible(false);
		this.textSP.setVisible(false);
		this.vboxMP.setVisible(false);
		this.textMP.setVisible(false);
		this.vboxS.setVisible(false);
		this.textS.setVisible(false);
		this.vboxC.setVisible(false);
		this.textC.setVisible(false);
		this.vboxSR.setVisible(false);
		this.vboxCR.setVisible(false);
	}
	
	@FXML public void selectSP(ActionEvent event) 
	{
		System.out.println("User selected Single-Player");
		
		this.vboxMM.setVisible(false);
		this.vboxSC.setVisible(false);
		this.textMM.setVisible(false);
				
		this.vboxSP.setVisible(true);
		this.vboxB.setVisible(true);
		this.textSP.setVisible(true);
		
	}
	@FXML public void selectMP(ActionEvent event) 
	{
		System.out.println("User selected Multi-Player");
		
		this.vboxMM.setVisible(false);
		this.vboxSC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxMP.setVisible(true);
		this.textMP.setVisible(true);
		this.vboxB.setVisible(true);	
		
		this.textFieldIP.setText("");
		this.buttonCNR.setDisable(false);
		this.buttonJER.setDisable(true);
		this.labelErrorIP.setVisible(false);
		this.hboxC.setVisible(false);
		
		// reset chat controls
		//this.textAreaChatC.setText("");
		//this.textAreaChatS.setText("");
		this.textFieldChatC.setText("");
		this.textFieldChatS.setText("");
		this.buttonSendMessageC.setDisable(true);
		this.buttonSendMessageS.setDisable(true);
	}
	@FXML public void selectCNR(ActionEvent event)
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
		this.server = new Server2(this.textFieldNickname.getText(), this.textAreaChatS, this.playerList, this.readyList);
		
		this.vboxMP.setVisible(false);
		this.vboxSR.setVisible(true);
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
		
		this.buttonCNR.setDisable(true);
		this.textFieldIP.setDisable(true);
		this.buttonJER.setDisable(true);
		this.hboxC.setVisible(true);
		this.buttonB.setDisable(true);
		
		// to-do: creazione socket e connessione al server (porta default)
		this.isServer = false;
		try {
			this.client = new Client2(this.textFieldNickname.getText(), InetAddress.getByName(this.textFieldIP.getText()), this.vboxMP, this.vboxCR, this.buttonB, this.textAreaChatC);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.client.sendConnect(this.textFieldNickname.getText());
		
	}
	@FXML public void selectSC(ActionEvent event)
	{
		System.out.println("Connection attempt interrupted.");
		
		this.buttonCNR.setDisable(false);
		this.textFieldIP.setDisable(false);
		this.buttonJER.setDisable(false);
		this.hboxC.setVisible(false);
		this.buttonB.setDisable(false);
		
		
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
	
	@FXML public void selectRH(ActionEvent event) 
	{
		System.out.println("User selected Rules & Help");
		
		this.vboxMM.setVisible(false);
		this.vboxSC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxB.setVisible(true);
	}
	
	@FXML public void selectS(ActionEvent event) 
	{
		System.out.println("User selected Settings");
		
		this.vboxMM.setVisible(false);
		this.vboxSC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxS.setVisible(true);
		this.textS.setVisible(true);
		this.vboxB.setVisible(true);
	}
	@FXML public void selectC(ActionEvent event) 
	{
		System.out.println("User selected Credits");
		
		this.vboxMM.setVisible(false);
		this.vboxSC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxC.setVisible(true);
		this.textC.setVisible(true);
		this.vboxB.setVisible(true);
	}
	
	@FXML public void selectSG(ActionEvent event) 
	{
		System.out.println("User selected Start Game");
		
		// to-do
		try {
			FXMLLoader loader = new FXMLLoader(ControllerMenu.class.getResource("/view/ViewGame.fxml"));
			Stage stage = (Stage) this.vboxMM.getScene().getWindow();
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
		
		this.vboxSP.setVisible(false);
		this.vboxB.setVisible(false);
		this.vboxMP.setVisible(false);
		this.vboxC.setVisible(false);
		this.vboxS.setVisible(false);
		this.textSP.setVisible(false);
		this.textMP.setVisible(false);
		this.textS.setVisible(false);
		this.textC.setVisible(false);
		this.vboxSR.setVisible(false);
		this.vboxCR.setVisible(false);
		
		this.vboxMM.setVisible(true);
		this.vboxSC.setVisible(true);
		this.textMM.setVisible(true);
		
	}
	
	@FXML public void restoreImp(ActionEvent event) 
	{
		System.out.println("Settings restored.");
	}
	@FXML public void selectSE(ActionEvent event) 
	{
		System.out.println("User selected Save and Exit");
		
		this.vboxS.setVisible(false);
		this.textS.setVisible(false);
		
		this.vboxMM.setVisible(true);
		this.vboxSC.setVisible(true);
		this.textMM.setVisible(true);
		
	}
	
	
}
