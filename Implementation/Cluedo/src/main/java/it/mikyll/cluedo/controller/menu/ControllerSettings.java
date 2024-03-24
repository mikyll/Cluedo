package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import it.mikyll.cluedo.persistence.SettingsRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ControllerSettings implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxSettings;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;

    @FXML private CheckBox checkBoxToggleMusic;
    @FXML private Slider sliderMusicVolume;
    @FXML private CheckBox checkBoxToggleSoundEffects;
    @FXML private Slider sliderSoundEffectsVolume;
    @FXML private Button buttonSaveSettings;
    @FXML private Button buttonCancelSettings;

    public void initialize()
    {
        this.vboxSettings.setVisible(true);
        this.vboxBackControls.setVisible(true);
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back");

        Navigator.switchView(NavEntry.MAIN);
    }

    @FXML
    public void toggleMusic(ActionEvent e)
    {
        MusicPlayer player = MusicPlayer.getInstance();
        if (checkBoxToggleMusic.isSelected())
        {
            sliderMusicVolume.setDisable(false);
            player.play();
        }
        else
        {
            sliderMusicVolume.setDisable(true);
            player.stop();
        }
    }

    @FXML public void updateMusicVolume(MouseEvent e)
    {
        MusicPlayer player = MusicPlayer.getInstance();
        player.setVolume(sliderMusicVolume.getValue() / 100);
    }

    @FXML public void toggleSoundEffects(ActionEvent e)
    {
        if (checkBoxToggleSoundEffects.isSelected())
        {
            sliderSoundEffectsVolume.setDisable(false);
            // TODO
        }
        else
        {
            sliderSoundEffectsVolume.setDisable(true);
            // TODO
        }
    }

    @FXML public void updateSoundEffectsVolume(MouseEvent e)
    {
        MusicPlayer player = MusicPlayer.getInstance();
        player.setVolume(sliderMusicVolume.getValue() / 100);
    }

    @FXML public void saveSettings(ActionEvent e)
    {
        Settings settings = Settings.getInstance();
        // language
        settings.setMusicEnabled(checkBoxToggleMusic.isSelected());
        settings.setMusicVolume(sliderMusicVolume.getValue());
        settings.setSoundEffectsEnabled(checkBoxToggleSoundEffects.isSelected());
        settings.setSoundEffectsVolume(sliderSoundEffectsVolume.getValue());
        SettingsRepository.saveSettings(settings);
    }

    @FXML public void cancelSettings(ActionEvent e)
    {

    }
}
