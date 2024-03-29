package it.mikyll.cluedo.controller.navigation;

import it.mikyll.cluedo.controller.menu.ControllerLoading;
import it.mikyll.cluedo.controller.game.ControllerGame;
import it.mikyll.cluedo.controller.menu.*;
import it.mikyll.cluedo.model.settings.Settings;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Navigator {
    private static Stage stage;
    private static Map<NavEntry, Scene> views;
    private static Map<NavEntry, IController> controllers;
    private static ControllerLoading ctrlLoading;
    private static HostServices hostServices;

    public static synchronized void initStage(Stage appStage) {
        stage = appStage;
        stage.setTitle(Settings.APP_TITLE);
        stage.setResizable(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        ctrlLoading = new ControllerLoading();
        stage.setScene(loadScene("views/ViewMenuLoading.fxml", ctrlLoading));
        stage.show();

        loadMenuScenes();
    }

    private static Scene loadScene(String filename, IController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(ControllerMain.class.getResource(Settings.RESOURCES_PATH + filename));
            loader.setController(controller);
            AnchorPane view = (AnchorPane) loader.load();

            Scene scene = new Scene(view);
            scene.getStylesheets().add(Navigator.class.getResource(Settings.RESOURCES_PATH + "styles/application.css").toExternalForm());

            ctrlLoading.incrementProgressBar("Loaded scene '" + filename + "'");

            return scene;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            ctrlLoading.showError(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void loadMenuScenes() {
        Thread loadingThread = new Thread(() -> {
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

            ControllerRulesHelp ctrlRH = new ControllerRulesHelp();
            views.put(NavEntry.RULES_HELP, loadScene("views/ViewMenuRulesHelp.fxml", ctrlRH));
            controllers.put(NavEntry.RULES_HELP, ctrlRH);

            ControllerGame ctrlGame = new ControllerGame();
            views.put(NavEntry.GAME, loadScene("views/ViewGame.fxml", ctrlGame));
            controllers.put(NavEntry.GAME, ctrlGame);

            switchView(NavEntry.MAIN);
        });

        loadingThread.start();
    }

    public static synchronized void switchView(NavEntry navEntry)
    {
        Platform.runLater(() -> {
            stage.setScene(views.get(navEntry));
            controllers.get(navEntry).start();
        });
    }

    public static synchronized IController getController(NavEntry menuEntry)
    {
        return controllers.get(menuEntry);
    }

    public static void setFullscreen(boolean value)
    {
        stage.setFullScreen(value);
    }

    public static void setHostServices(HostServices hostServicesRef)
    {
        hostServices = hostServicesRef;
    }
    public static void openURL(String url)
    {
        if (hostServices != null)
            hostServices.showDocument(url);
    }
}