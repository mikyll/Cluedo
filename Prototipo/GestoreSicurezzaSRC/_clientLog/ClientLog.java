package _clientLog;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;




public class ClientLog extends Application {
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(ClientLog.class.getResource("/view/InterfacciaLogin/ViewLoginGestoreSicurezza.fxml"));
			AnchorPane viewLoginGestoreSicurezza = (AnchorPane) loader.load();
			Scene scene = new Scene(viewLoginGestoreSicurezza);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setTitle("ClientLog");
			stage.setScene(scene);
			stage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

