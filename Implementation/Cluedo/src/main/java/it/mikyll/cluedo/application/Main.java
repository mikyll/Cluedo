package it.mikyll.cluedo.application;

import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicTrack;
import it.mikyll.cluedo.persistence.SettingsManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // load settings
            Settings settings = SettingsManager.loadSettings("");
            MusicPlayer player = MusicPlayer.getInstance();
            player.setVolume(settings.getMusicVolume());
            if (settings.isMusicEnabled())
            {
                player.setTrack(MusicTrack.MENU);
                player.play();
            }

            FXMLLoader loader = new FXMLLoader(Main.class.getResource(Settings.RESOURCES_PATH + "views/ViewMenu2.fxml"));
            AnchorPane basePane = (AnchorPane) loader.load();
            Image img = new Image(Main.class.getResource(Settings.RESOURCES_PATH + "images/background-cluedo.png").toURI().toString(), true);
            BackgroundImage bgImage = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
            basePane.setBackground(new Background(bgImage));
            // loader.setController();
            Scene scene = new Scene(basePane);
            scene.getStylesheets().add(getClass().getResource(Settings.RESOURCES_PATH + "styles/application.css").toExternalForm());
            //scene.getStylesheets().add(getClass().getResource("/resources/styles/background.css").toExternalForm());
            stage.setTitle("Cluedo");
            stage.setScene(scene);
            stage.setMinWidth(800.0);
            stage.setMinHeight(600.0);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop()
    {
        //this.controller.closeConnection();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

}