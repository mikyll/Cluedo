package controller.GestioneMultigiocatore;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public interface IGestioneMultigiocatore {
	@FXML public void selezionaHomeUtente(ActionEvent event) throws IOException;	
	@FXML public void selezionaRegistrazione(ActionEvent event) throws IOException;
	@FXML public void selezionaLogin(ActionEvent event) throws IOException;
	@FXML public void selezionaPartecipazione(ActionEvent event) throws IOException;
	@FXML public void selezionaCreazione(ActionEvent event) throws IOException;
}
