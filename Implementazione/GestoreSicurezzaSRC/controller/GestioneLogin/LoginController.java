package controller.GestioneLogin;

import java.io.IOException;

import _clientApplicazione.ClientApplicazione;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {
	@FXML private TextField textFieldUsername;
	@FXML private PasswordField passwordFieldPassword;
	@FXML private TextField textFieldPasswordErrata;
	@FXML private Button buttonLogin;
	
	private String username = "Amministratore";
	private String password = "password";
	
	@FXML public void initialize()
	{
		this.textFieldPasswordErrata.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: red;");
	}
	
	@FXML public void selezionaLogin(ActionEvent event) throws IOException 
	{
		if(this.textFieldUsername.getText().equals(this.username) && this.passwordFieldPassword.getText().equals(this.password))
		{
			System.out.println("[GestoreSicurezza] Login effettuato con successo.");
			
			// mostra ViewLog
			FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaLog/ViewLog.fxml"));
			Stage stage = (Stage) this.buttonLogin.getScene().getWindow();
			AnchorPane viewLog = (AnchorPane) loader.load();
			Scene scene = new Scene(viewLog);
			stage.setScene(scene);
			
		}
		else this.textFieldPasswordErrata.setText("*Password errata");
	}
}
