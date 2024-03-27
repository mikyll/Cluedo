package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import it.mikyll.cluedo.persistence.SettingsManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ControllerSettings implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxSettings;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;

    @FXML private CheckBox checkBoxToggleChat;
    @FXML private CheckBox checkBoxToggleMusic;
    @FXML private Slider sliderMusicVolume;
    @FXML private CheckBox checkBoxToggleSoundEffects;
    @FXML private Slider sliderSoundEffectsVolume;
    @FXML private Button buttonSaveSettings;
    @FXML private Button buttonCancelSettings;

    private Settings settings;
    private MusicPlayer player;

    public ControllerSettings()
    {
        this.settings = Settings.getInstance();
        this.player = MusicPlayer.getInstance();
    }

    public void initialize()
    {
        this.settings = Settings.getInstance();

        this.vboxSettings.setVisible(true);
        this.vboxBackControls.setVisible(true);

        this.checkBoxToggleChat.setSelected(settings.isChatEnabled());
        this.checkBoxToggleMusic.setSelected(settings.isMusicEnabled());
        this.sliderMusicVolume.setValue(settings.getMusicVolume());
        this.sliderMusicVolume.setDisable(!settings.isMusicEnabled());
        this.checkBoxToggleSoundEffects.setSelected(settings.isSoundEffectsEnabled());
        this.sliderSoundEffectsVolume.setValue(settings.getSoundEffectsVolume());
        this.sliderSoundEffectsVolume.setDisable(!settings.isSoundEffectsEnabled());
    }

    @FXML
    public void selectBack(ActionEvent e)
    {
        System.out.println("User selected Back");

        if (isSettingsChanged())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Changes not saved", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Error Dialog");
            alert.setContentText("Leaving this page will discard the changes in progress.\nLeave anyways?");
            alert.showAndWait();
            if(alert.getResult() == ButtonType.NO || alert.getResult() == ButtonType.CANCEL)
            {
                return;
            }
        }

        player.setVolume(settings.getMusicVolume());
        if (settings.isMusicEnabled())
            player.play();
        else player.stop();

        Navigator.switchView(NavEntry.MAIN);
    }

    @FXML
    public void selectLanguage(ActionEvent e)
    {

    }

    @FXML
    public void toggleMusic(ActionEvent e)
    {
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

    @FXML
    public void updateMusicVolume(MouseEvent e)
    {
        MusicPlayer player = MusicPlayer.getInstance();
        player.setVolume(sliderMusicVolume.getValue() / 100);
    }

    @FXML
    public void toggleSoundEffects(ActionEvent e)
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

    @FXML
    public void updateSoundEffectsVolume(MouseEvent e)
    {
        MusicPlayer player = MusicPlayer.getInstance();
        player.setVolume(sliderMusicVolume.getValue() / 100);
    }

    @FXML
    public void saveSettings(ActionEvent e)
    {
        Settings settings = Settings.getInstance();

        // language
        settings.setChatEnabled(checkBoxToggleChat.isSelected());
        settings.setMusicEnabled(checkBoxToggleMusic.isSelected());
        settings.setMusicVolume(sliderMusicVolume.getValue());
        settings.setSoundEffectsEnabled(checkBoxToggleSoundEffects.isSelected());
        settings.setSoundEffectsVolume(sliderSoundEffectsVolume.getValue());
        SettingsManager.saveSettings(settings, ".settings.json");
    }

    @FXML
    public void cancelSettings(ActionEvent e)
    {

    }

    private boolean isSettingsChanged()
    {
        return this.checkBoxToggleChat.isSelected() != settings.isChatEnabled()
                || this.checkBoxToggleMusic.isSelected() != settings.isMusicEnabled()
                || this.sliderMusicVolume.getValue() != settings.getMusicVolume()
                || this.checkBoxToggleSoundEffects.isSelected() != settings.isSoundEffectsEnabled()
                || this.sliderSoundEffectsVolume.getValue() != settings.getSoundEffectsVolume();
    }
}
