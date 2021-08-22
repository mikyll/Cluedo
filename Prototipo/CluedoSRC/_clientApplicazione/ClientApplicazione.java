package _clientApplicazione;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.web.WebView;



public class ClientApplicazione extends Application {
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(ClientApplicazione.class.getResource("/view/InterfacciaMenu/HomeUtente.fxml"));
			AnchorPane homeUtente = (AnchorPane) loader.load();
			// loader.setController();
			Scene scene = new Scene(homeUtente);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add(getClass().getResource("/resources/styles/background.css").toExternalForm());
			stage.setTitle("Cluedo game");
			stage.setScene(scene);
			stage.show();
			
			/*stage.setTitle("JavaFX WebView Example");
	        WebView webView = new WebView();
	        webView.getEngine().load("http://www.google.com");
	        VBox vBox = new VBox(webView);
	        Scene scene = new Scene(vBox, 960, 600);
	        stage.setScene(scene);
	        stage.show();*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
