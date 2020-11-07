package controller.GestioneTurno;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import model.partita.CartaIndizio;
import model.partita.Partita;
import model.partita.Taccuino;
import model.partita.TipoCarta;

public class StrumentiController {
	@FXML private VBox vboxTaccuino;
	@FXML private VBox vboxBloccoNote;
	
	@FXML private VBox vboxStanze;
	@FXML private VBox vboxArmi;
	@FXML private VBox vboxSospettati;
	@FXML private Button buttonChiudi;
	
	@FXML private TextArea textAreaBloccoNote;
	@FXML private Button buttonSalva;
	@FXML private Button buttonChiudiSenzaSalvare;
	
	@FXML private Taccuino taccuino;
	
	private Partita partita;
	
	public StrumentiController() {}
	@FXML public void initialize()
	{
		this.partita = Partita.getInstance(0, null);
		if(this.vboxTaccuino != null)
		{
			this.taccuino = this.partita.getGiocatoreByUsername("Giocatore").getTaccuino();
			
			// NB: Taccuino costruito dinamicamente!!!
			for(CartaIndizio c : this.taccuino.getTaccuino().keySet())
			{
				CheckBox checkBox = new CheckBox(c.getDescrizione());
				checkBox.setStyle("-fx-text-fill: white");
				checkBox.setSelected(this.taccuino.getTaccuino().get(c));
				
				Separator separator = new Separator();
				separator.setOrientation(Orientation.HORIZONTAL);
				separator.setPrefHeight(10.0);
				separator.setVisible(false);
				
				if(c.getTipo().equals(TipoCarta.STANZA))
				{
					checkBox.setOnAction(this::selezionaCheckBoxStanza);
					this.vboxStanze.getChildren().add(checkBox);
					this.vboxStanze.getChildren().add(separator);
				}
				else if(c.getTipo().equals(TipoCarta.ARMA))
				{
					checkBox.setOnAction(this::selezionaCheckBoxArma);
					this.vboxArmi.getChildren().add(checkBox);
					this.vboxArmi.getChildren().add(separator);
				}
				else if(c.getTipo().equals(TipoCarta.SOSPETTATO))
				{
					checkBox.setOnAction(this::selezionaCheckBoxSospettato);
					this.vboxSospettati.getChildren().add(checkBox);
					this.vboxSospettati.getChildren().add(separator);
				}
			}
			
		}
		if(this.vboxBloccoNote != null)
			this.textAreaBloccoNote.setText(this.partita.getGiocatoreByUsername("Giocatore").getBloccoNote());
	}
	
// TACCUINO
	@FXML private void selezionaCheckBoxStanza(ActionEvent event)
	{
		CheckBox checkBox = (CheckBox) event.getSource();
		// aggiornamento valore taccuino
		this.taccuino.getTaccuino().put(new CartaIndizio(checkBox.getText(), TipoCarta.STANZA), checkBox.isSelected());
	}
	@FXML private void selezionaCheckBoxArma(ActionEvent event)
	{
		CheckBox checkBox = (CheckBox) event.getSource();
		// aggiornamento valore taccuino
		this.taccuino.getTaccuino().put(new CartaIndizio(checkBox.getText(), TipoCarta.STANZA), checkBox.isSelected());
	}
	@FXML private void selezionaCheckBoxSospettato(ActionEvent event)
	{
		CheckBox checkBox = (CheckBox) event.getSource();
		// aggiornamento valore taccuino
		this.taccuino.getTaccuino().put(new CartaIndizio(checkBox.getText(), TipoCarta.STANZA), checkBox.isSelected());
	}
	@FXML public void chiudiTaccuino(ActionEvent event)
	{
		// riabilita i bottoni
		this.enableButtons(event);
		
		VBox vboxFase = (VBox) this.vboxTaccuino.getParent();
		vboxFase.getChildren().remove(1);
		
		System.out.println("[Sistema] Giocatore ha chiuso il Taccuino.");
	}
	
// BLOCCO NOTE
	@FXML public void salvaBloccoNote(ActionEvent event)
	{
		// salvataggio modifiche
		this.partita.getGiocatoreByUsername("Giocatore").setBloccoNote(this.textAreaBloccoNote.getText());
		
		// riabilita i bottoni
		this.enableButtons(event);
		
		VBox vboxFase = (VBox) this.vboxBloccoNote.getParent();
		vboxFase.getChildren().remove(1);
		
		System.out.println("[Sistema] Giocatore ha chiuso il BloccoNote (salva).");
	}
	@FXML public void chiudiBloccoNote(ActionEvent event)
	{
		// riabilita i bottoni
		this.enableButtons(event);
		
		VBox vboxFase = (VBox) this.vboxBloccoNote.getParent();
		vboxFase.getChildren().remove(1);
		
		System.out.println("[Sistema] Giocatore ha chiuso il BloccoNote (chiudi senza salvare).");
	}
	private void enableButtons(ActionEvent event)
	{
		/*Button button = (Button) event.getSource();
		
		VBox vboxControlli = null;
		// disaccoppiamento dei due casi (potrebbe tornare utile in futuro)
		if(button.getText().equals("Chiudi"))
		{
			VBox vboxBase = (VBox) ((HBox) ((VBox) this.vboxTaccuino.getParent()).getParent()).getParent();
			vboxControlli = (VBox) ((HBox) vboxBase.getChildren().get(2)).getChildren().get(2);
			// HBox hboxControlliSotto
		}
		else if(button.getText().equals("Salva") || button.getText().equals("Chiudi senza salvare"))
		{
			VBox vboxBase = (VBox) ((HBox) ((VBox) this.vboxBloccoNote.getParent()).getParent()).getParent();
			vboxControlli = (VBox) ((HBox) vboxBase.getChildren().get(2)).getChildren().get(2);
			// HBox hboxControlliSotto
		}
		
		Giocatore g = this.partita.getGiocatoreByUsername("Giocatore");
		if(g.getAzioniDisponibili().get(Azione.MOVIMENTO))
			((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(1)).setDisable(false);
		if(g.getAzioniDisponibili().get(Azione.INDAGINE))
			((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(3)).setDisable(false);
		if(g.getAzioniDisponibili().get(Azione.ACCUSA))
			((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(5)).setDisable(false);
		((Button) ((HBox) vboxControlli.getChildren().get(0)).getChildren().get(7)).setDisable(false);
		if(g.getAzioniDisponibili().get(Azione.TACCUINO))
			((Button) ((HBox) vboxControlli.getChildren().get(1)).getChildren().get(1)).setDisable(false);
		if(g.getAzioniDisponibili().get(Azione.BLOCCONOTE))
			((Button) ((HBox) vboxControlli.getChildren().get(1)).getChildren().get(3)).setDisable(false);
		*/
	}
}
