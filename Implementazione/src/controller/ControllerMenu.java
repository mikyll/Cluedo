package controller;

import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ControllerMenu {
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
	@FXML private Button buttonCNR;			// button Create New Room
	@FXML private TextField textFieldIP;	// textField IP
	@FXML private Button buttonJER;			// button Join Existing Room
	@FXML private Label labelErrorIP;		// label Error IP
	@FXML private HBox hboxC;				// hbox Connecting
	@FXML private Button buttonSC;			// button Stop Connecting
	
	// vbox Multi-Player create new room cotrols:
	@FXML private Label labelIP;
	/*TEST socket*/
	@FXML private VBox vboxR;				// vbox Room
	@FXML private TextArea textAreaChat;	// textArea Chat
	@FXML private TextField textFieldChat;	// textField Chat
	@FXML private Button buttonSendMessage;	// button Send Message
	private boolean isServer;
	
	// vbox Settings controls:
	@FXML private CheckBox checkboxM;			// checkbox Music
	@FXML private Slider sliderMV;				// slider Music Volume
	@FXML private CheckBox checkboxA;			// checkbox Audio
	@FXML private Slider sliderAV;				// slider Audio Volume
	@FXML private ComboBox<String> comboboxL; 	// combobox Language
	@FXML private Button buttonRD;				// button Restore Defaults
	@FXML private Button buttonSE;				// button Save and Exit
	
	public ControllerMenu() {}
	
	public void initialize()
	{
		
		
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
	}
	@FXML public void selectCNR(ActionEvent event)
	{
		System.out.println("User selected Create New Room");
		
		// to-do: server code
		
		this.vboxMP.setVisible(false);
		this.vboxR.setVisible(true);
		this.labelIP.setVisible(true);
		this.labelIP.setText(""); // set IP
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
	@FXML public void sendMessage(ActionEvent event)
	{
		if(this.isServer)
		{
			// invio a tutti i client
		}
		else {
			// invio al server
		}
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
