package it.mikyll.cluedo.application;

import it.mikyll.cluedo.controller.menu.ControllerLobbyClient;
import it.mikyll.cluedo.controller.menu.ControllerLobbyServer;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainRefactor extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Cluedo");
        stage.setResizable(false);

        Navigator.initStage(stage);
        Navigator.switchView(NavEntry.MAIN);
    }


    @Override
    public void stop()
    {
        ((ControllerLobbyServer) Navigator.getController(NavEntry.LOBBY_SERVER)).closeConnection();
        ((ControllerLobbyClient) Navigator.getController(NavEntry.LOBBY_CLIENT)).closeConnection();
        Platform.exit();
        //System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

}