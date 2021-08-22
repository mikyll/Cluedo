package controller.GestioneTurno;

import java.util.concurrent.ThreadLocalRandom;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.partita.Azione;
import model.partita.Giocatore;
import model.partita.Partita;

public class MovimentoController {
	
	@FXML private VBox vboxMovimento;
	
	@FXML private Button buttonLanciaDadi;
	@FXML private TextField textFieldRisultato;
	@FXML private TextField textFieldPosizione;
	@FXML private Button buttonConfermaSpostamento;
	
	private Partita partita;
	private Giocatore giocatore;
	
	public void initialize() 
	{
		this.partita = Partita.getInstance(0,null);
		this.giocatore = this.partita.getGiocatoreByUsername("Giocatore");
		
		if(this.giocatore.isDadiLanciati())
			this.buttonLanciaDadi.setDisable(true);
	}
	
	@FXML public void lanciaDadi(ActionEvent event)
	{
		this.giocatore.setEsitoDadi(this.getRandomInt());
		this.giocatore.setDadiLanciati(true);
		this.textFieldRisultato.setText(String.valueOf(this.giocatore.getEsitoDadi()));
		System.out.println("[Sistema] Giocatore ha lanciato i dadi (" + this.giocatore.getEsitoDadi() + ")");
		this.buttonLanciaDadi.setDisable(true);
		// setAzioniDisponibili (non può più fare movimento ?)
		// disabilita bottoni di conseguenza
		
		// ===================================================
		// ROBA TEMP PER PRESENTAZIONE
		// associo onChange al textField (invisibile)
		VBox vboxBase = (VBox) ((HBox) ((VBox) this.vboxMovimento.getParent()).getParent()).getParent();
		TextField textFieldPosizione = (TextField) ((HBox) ((VBox) ((HBox) vboxBase.getChildren().get(1)).getChildren().get(1)).getChildren().get(6)).getChildren().get(0);
		System.out.println("POSIZIONE: " + textFieldPosizione.getText());
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	@FXML public void confermaSpostamento(ActionEvent event)
	{
		this.giocatore.setDadiLanciati(false);
		// aggiorna casella giocatore
		// sposta giocatore sulla mappa
		
		// set azioni disponibili (non può più fare movimento ma può fare indagine se è in una stanza)
		// TEMP
		this.giocatore.getAzioniDisponibili().put(Azione.INDAGINE, true);
		this.giocatore.getAzioniDisponibili().put(Azione.ACCUSA, true);
		
		// abilita bottoni di conseguenza
		VBox vboxBase = (VBox) ((HBox) ((VBox) this.vboxMovimento.getParent()).getParent()).getParent();
		VBox vboxControlli = (VBox) ((HBox) vboxBase.getChildren().get(2)).getChildren().get(2);
		if(this.giocatore.getAzioniDisponibili().get(Azione.MOVIMENTO))
			((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(1)).setDisable(false);
		if(this.giocatore.getAzioniDisponibili().get(Azione.INDAGINE))
			((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(3)).setDisable(false);
		if(this.giocatore.getAzioniDisponibili().get(Azione.ACCUSA))
			((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(5)).setDisable(false);
		((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(7)).setDisable(false);
		if(this.giocatore.getAzioniDisponibili().get(Azione.TACCUINO))
			((Button) ((HBox) vboxControlli.getChildren().get(1)).getChildren().get(1)).setDisable(false);
		if(this.giocatore.getAzioniDisponibili().get(Azione.BLOCCONOTE))
			((Button) ((HBox) vboxControlli.getChildren().get(1)).getChildren().get(3)).setDisable(false);
		
		// rimuove dalla vboxFase la vboxMovimento
		((VBox) this.vboxMovimento.getParent()).getChildren().remove(0);
	}
	
	private int getRandomInt() {return ThreadLocalRandom.current().nextInt(2, 13);}
}
