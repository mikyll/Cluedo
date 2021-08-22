package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ControllerMenu {
	
	@FXML private Text textMM;	// text Main Menu
	@FXML private Text textGS;	// text Giocatore Singolo
	@FXML private Text textMG;	// text MultiGiocatore
	@FXML private Text textIm;	// text Impostazioni
	@FXML private Text textC;	// text Crediti
	
	@FXML private VBox vboxMM;	// vbox Main Menu
	@FXML private VBox vboxIC;	// vbox Impostazioni/Crediti
	@FXML private VBox vboxGS;	// vbox Giocatore Singolo
	@FXML private VBox vboxIn;	// vbox Indietro
	@FXML private VBox vboxMG;	// vbox MultiGiocatore
	@FXML private VBox vboxIm;	// vbox Impostazioni
	@FXML private VBox vboxC;	// vbox Crediti
	
	// vbox Main Menu controls:
	@FXML private Button buttonGS;	// button Giocatore Singolo
	@FXML private Button buttonMG;	// button MultiGiocatore
	@FXML private Button buttonRA;	// button Regole e Aiuto
	
	// vbox Bottom-right controls:
	@FXML private Button buttonIm;	// button Impostazioni
	@FXML private Button buttonC;	// button Crediti
	
	// vbox Bottom-left controls:
	@FXML private Button buttonIn;	// button indietro
	
	// vbox Giocatore Singolo controls:
	@FXML private Spinner<Integer> spinnerNA;	// spinner Numero Avversari
	@FXML private ComboBox<String> comboboxDA;	// combobox Difficoltà Avversari
	@FXML private Button buttonAP;				// button Avvia Partita
	
	// vbox Impostazioni controls:
	@FXML private CheckBox checkboxM;			// checkbox Musica
	@FXML private Slider sliderMV;				// slider Musica Volume
	@FXML private CheckBox checkboxA;			// checkbox Audio
	@FXML private Slider sliderAV;				// slider Audio Volume
	@FXML private ComboBox<String> comboboxL; 	// combobox Lingua
	@FXML private Button buttonR;				// button Ripristina
	@FXML private Button buttonSE;						// button Salva ed Esci
	
	public ControllerMenu() {}
	
	public void initialize()
	{
		
		
		// setup panels and text labels visibility
		this.vboxMM.setVisible(true);
		this.vboxIC.setVisible(true);
		this.textMM.setVisible(true);
		
		this.vboxIn.setVisible(false);
		this.vboxGS.setVisible(false);
		this.textGS.setVisible(false);
		this.vboxMG.setVisible(false);
		this.textMG.setVisible(false);
		this.vboxIm.setVisible(false);
		this.textIm.setVisible(false);
		this.vboxC.setVisible(false);
		this.textC.setVisible(false);
	}
	
	@FXML public void selectGS(ActionEvent event) 
	{
		System.out.println("Selezione Giocatore Singolo");
		
		this.vboxMM.setVisible(false);
		this.vboxIC.setVisible(false);
		this.textMM.setVisible(false);
				
		this.vboxGS.setVisible(true);
		this.vboxIn.setVisible(true);
		this.textGS.setVisible(true);
		
	}
	@FXML public void selectMG(ActionEvent event) 
	{
		System.out.println("Selezione Multigiocatore");
		
		this.vboxMM.setVisible(false);
		this.vboxIC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxMG.setVisible(true);
		this.textMG.setVisible(true);
		this.vboxIn.setVisible(true);
		
	}
	@FXML public void selectRA(ActionEvent event) 
	{
		System.out.println("Selezione Regole e Aiuto");
		
		this.vboxMM.setVisible(false);
		this.vboxIC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxIn.setVisible(true);
	}
	
	@FXML public void selectIm(ActionEvent event) 
	{
		System.out.println("Selezione Impostazioni");
		
		this.vboxMM.setVisible(false);
		this.vboxIC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxIm.setVisible(true);
		this.textIm.setVisible(true);
		this.vboxIn.setVisible(true);
	}
	@FXML public void selectC(ActionEvent event) 
	{
		System.out.println("Selezione Crediti");
		
		this.vboxMM.setVisible(false);
		this.vboxIC.setVisible(false);
		this.textMM.setVisible(false);
		
		this.vboxC.setVisible(true);
		this.textC.setVisible(true);
		this.vboxIn.setVisible(true);
	}
	
	@FXML public void selectAP(ActionEvent event) 
	{
		System.out.println("Selezione Avvia Partita");
	}
	
	@FXML public void selectIn(ActionEvent event) 
	{
		System.out.println("Selezione Indietro");
		
		this.vboxGS.setVisible(false);
		this.vboxIn.setVisible(false);
		this.vboxMG.setVisible(false);
		this.vboxC.setVisible(false);
		this.vboxIm.setVisible(false);
		this.textGS.setVisible(false);
		this.textMG.setVisible(false);
		this.textIm.setVisible(false);
		this.textC.setVisible(false);
		
		this.vboxMM.setVisible(true);
		this.vboxIC.setVisible(true);
		this.textMM.setVisible(true);
		
	}
	
	@FXML public void restoreImp(ActionEvent event) 
	{
		System.out.println("Impostazioni ripristinate.");
	}
	@FXML public void selectSE(ActionEvent event) 
	{
		System.out.println("Selezione Salva ed Esci");
		
		this.vboxIm.setVisible(false);
		this.textIm.setVisible(false);
		
		this.vboxMM.setVisible(true);
		this.vboxIC.setVisible(true);
		this.textMM.setVisible(true);
		
	}
	
	
}
