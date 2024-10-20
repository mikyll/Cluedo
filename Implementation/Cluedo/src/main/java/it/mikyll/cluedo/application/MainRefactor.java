package it.mikyll.cluedo.application;

import it.mikyll.cluedo.controller.menu.ControllerLobbyClient;
import it.mikyll.cluedo.controller.menu.ControllerLobbyServer;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.persistence.SettingsManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainRefactor extends Application {
    @Override
    public void start(Stage stage) {
        SettingsManager.loadSettings(SettingsManager.SETTINGS_FILENAME);

        Navigator.initStage(stage);
        Navigator.setHostServices(this.getHostServices());
    }

    @Override
    public void stop()
    {
        ((ControllerLobbyServer) Navigator.getController(NavEntry.LOBBY_SERVER)).closeConnection();
        ((ControllerLobbyClient) Navigator.getController(NavEntry.LOBBY_CLIENT)).closeConnection();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }

}