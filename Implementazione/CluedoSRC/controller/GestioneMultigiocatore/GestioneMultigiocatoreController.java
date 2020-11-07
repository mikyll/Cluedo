package controller.GestioneMultigiocatore;

import java.io.IOException;

import controller.GestioneMenu.GestioneMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GestioneMultigiocatoreController implements IGestioneMultigiocatore{
	@FXML private AnchorPane base;
	@FXML private Button buttonBack;
	
	@FXML public void initialize()
	{
		// if PRIMO ACCESSO
	}
	public GestioneMultigiocatoreController() {}
	
	@FXML public void selezionaHomeUtente(ActionEvent event) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(GestioneMenuController.class.getResource("/view/InterfacciaMenu/HomeUtente.fxml"));
		Stage stage = (Stage) buttonBack.getScene().getWindow();
		AnchorPane homeUtente = (AnchorPane) loader.load();
		Scene scene = new Scene(homeUtente);
		scene.getStylesheets().add(getClass().getResource("/resources/styles/application.css").toExternalForm());
		stage.setScene(scene);
	}
	
	@FXML public void selezionaRegistrazione(ActionEvent event) throws IOException
	{
		
	}
	@FXML public void selezionaLogin(ActionEvent event) throws IOException
	{
		
	}
	@FXML public void selezionaPartecipazione(ActionEvent event) throws IOException
	{
		
	}
	@FXML public void selezionaCreazione(ActionEvent event) throws IOException
	{
		
	}
}
