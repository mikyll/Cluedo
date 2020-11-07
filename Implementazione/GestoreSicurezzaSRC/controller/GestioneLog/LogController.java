package controller.GestioneLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import _clientApplicazione.ClientApplicazione;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LogController {
	
	// ViewLog
	@FXML private CheckBox checkBoxAccesso;
	@FXML private CheckBox checkBoxConnessione;
	@FXML private CheckBox checkBoxPartita;
	@FXML private TextField textFieldFilterUtente;
	@FXML private TextField textFieldFilterCodPartita;
	@FXML private TextArea textAreaLog;
		
	@FXML private Button buttonAggiorna;
	@FXML private Button buttonAnalisiLog;
	
	// ViewAnalisiLog
	@FXML private TextArea textAreaAnomalie;
	@FXML private Button buttonBack;
	
	private String logs;
	
	@FXML public void intialize()
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader("Log.txt"));
			String line;
			while((line = reader.readLine()) != null)
			{
				this.logs = this.textAreaLog.getText() + "\n" + line;
			}
			reader.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@FXML public void selezionaAggiorna(ActionEvent event)
	{
		// aggiorna TextArea
		try {
			BufferedReader reader = new BufferedReader(new FileReader("Log.txt"));
			String line;
			while((line = reader.readLine()) != null)
			{
				this.textAreaLog.setText(this.textAreaLog.getText() + "\n" + line);
			}
			reader.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	}
	@FXML public void selezionaAnalisiLog(ActionEvent event) throws IOException
	{
		// mostra ViewAnalisiLog
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaLog/ViewAnalisiLog.fxml"));
		Stage stage = (Stage) this.buttonAnalisiLog.getScene().getWindow();
		AnchorPane viewAnalisiLog = (AnchorPane) loader.load();
		Scene scene = new Scene(viewAnalisiLog);
		stage.setScene(scene);
		
		if(this.logs.length() > 1)
			// nulla
		this.textAreaAnomalie = (TextArea) ((HBox) viewAnalisiLog.getChildren().get(1)).getChildren().get(1);
		this.textAreaAnomalie.setText("[20/05/2020 20:15:07] codPartita=\"dX4d0\" op=\"partita avviata\"\r\n" + 
					"[20/05/2020 20:16:06] codPartita=\"dX4d0\" op=\"partita terminata - vincitore: Mario82\"\r\n" + 
					"PARTITA TROPPO BREVE");
		
	}
	
	@FXML public void selezionaTornaIndietro(ActionEvent event) throws IOException
	{
		// mostra ViewLog
		FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaLog/ViewLog.fxml"));
		Stage stage = (Stage) this.buttonBack.getScene().getWindow();
		AnchorPane viewLog = (AnchorPane) loader.load();
		Scene scene = new Scene(viewLog);
		stage.setScene(scene);
	}
}
