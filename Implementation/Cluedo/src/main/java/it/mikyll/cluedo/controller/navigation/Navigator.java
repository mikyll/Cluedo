package it.mikyll.cluedo.controller.navigation;

import it.mikyll.cluedo.controller.menu.*;
import it.mikyll.cluedo.model.settings.Settings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Navigator {
    private static Stage stage;
    private static Map<NavEntry, Scene> views;
    private static Map<NavEntry, IController> controllers;

    public static synchronized void initStage(Stage appStage)
    {
        stage = appStage;

        loadMenuScenes();
    }

    private static Scene loadScene(String filename, IController controller)
    {
        try {
            FXMLLoader loader = new FXMLLoader(ControllerMain.class.getResource(Settings.RESOURCES_PATH + filename));
            loader.setController(controller);
            AnchorPane view = (AnchorPane) loader.load();

            Scene scene = new Scene(view);
            scene.getStylesheets().add(Navigator.class.getResource(Settings.RESOURCES_PATH + "styles/application.css").toExternalForm());

            return scene;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void loadMenuScenes()
    {
        views = new HashMap<>();
        controllers = new HashMap<>();

        ControllerMain ctrlMain = new ControllerMain();
        views.put(NavEntry.MAIN, loadScene("views/ViewMenuMain.fxml", ctrlMain));
        controllers.put(NavEntry.MAIN, ctrlMain);

        ControllerSinglePlayer ctrlSP = new ControllerSinglePlayer();
        views.put(NavEntry.SINGLE_PLAYER, loadScene("views/ViewMenuSinglePlayer.fxml", ctrlSP));
        controllers.put(NavEntry.SINGLE_PLAYER, ctrlSP);

        ControllerMultiplayer ctrlMP = new ControllerMultiplayer();
        views.put(NavEntry.MULTIPLAYER, loadScene("views/ViewMenuMultiplayer.fxml", ctrlMP));
        controllers.put(NavEntry.MULTIPLAYER, ctrlMP);

        ControllerSettings ctrlSettings = new ControllerSettings();
        views.put(NavEntry.SETTINGS, loadScene("views/ViewMenuSettings.fxml", ctrlSettings));
        controllers.put(NavEntry.SETTINGS, ctrlSettings);

        ControllerAbout ctrlAbout = new ControllerAbout();
        views.put(NavEntry.ABOUT, loadScene("views/ViewMenuAbout.fxml", ctrlAbout));
        controllers.put(NavEntry.ABOUT, ctrlAbout);

        ControllerLobbyServer ctrlLS = new ControllerLobbyServer();
        views.put(NavEntry.LOBBY_SERVER, loadScene("views/ViewMenuLobbyServer.fxml", ctrlLS));
        controllers.put(NavEntry.LOBBY_SERVER, ctrlLS);

        ControllerLobbyClient ctrlLC = new ControllerLobbyClient();
        views.put(NavEntry.LOBBY_CLIENT, loadScene("views/ViewMenuLobbyClient.fxml", ctrlLC));
        controllers.put(NavEntry.LOBBY_CLIENT, ctrlLC);

        // TODO

        /*views.put(MenuEntry.MAIN, loadScene("views/ViewMenuMain.fxml", new ControllerMain()));
        views.put(MenuEntry.SINGLE_PLAYER, loadScene("views/ViewMenuSinglePlayer.fxml", new ControllerSinglePlayer()));
        views.put(MenuEntry.MULTIPLAYER, loadScene("views/ViewMenuMultiplayer.fxml", new ControllerMultiplayer()));
        views.put(MenuEntry.SETTINGS, loadScene("views/ViewMenuSettings.fxml", new ControllerSettings()));
        views.put(MenuEntry.ABOUT, loadScene("views/ViewMenuAbout.fxml", new ControllerAbout()));

        views.put(MenuEntry.LOBBY_SERVER, loadScene("views/ViewMenuLobbyServer.fxml", new ControllerLobbyServer()));
        //views.put(MenuEntry.LOBBY_CLIENT, loadScene("views/ViewMenuLobbyClient.fxml", new ControllerLobbyClient()));
        */
    }

    public static synchronized void switchView(NavEntry menuEntry)
    {
        stage.setScene(views.get(menuEntry));
        controllers.get(menuEntry).initialize();
        stage.show();
    }

    public static synchronized IController getController(NavEntry menuEntry)
    {
        return controllers.get(menuEntry);
    }
}