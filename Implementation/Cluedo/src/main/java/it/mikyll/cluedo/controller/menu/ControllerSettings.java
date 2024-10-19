package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import it.mikyll.cluedo.model.sounds.MusicTrack;
import it.mikyll.cluedo.persistence.SettingsManager;
import it.mikyll.cluedo.view.gui.javafx.CenteredAlert;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class ControllerSettings implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxSettings;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;

    @FXML private CheckBox checkBoxToggleChat;
    @FXML private CheckBox checkBoxToggleMusic;
    @FXML private Slider sliderMusicVolume;
    @FXML private Label labelMusicVolume;
    @FXML private CheckBox checkBoxToggleSoundEffects;
    @FXML private Slider sliderSoundEffectsVolume;
    @FXML private Label labelSoundEffectsVolume;

    @FXML private Button buttonSaveSettings;
    @FXML private Button buttonCancelSettings;

    private Window window;

    private final Settings settings;
    private final MusicPlayer musicPlayer;

    public ControllerSettings()
    {
        settings = Settings.getInstance();
        musicPlayer = MusicPlayer.getInstance();
    }

    public void initialize() {

        this.labelMusicVolume.textProperty().bind(
                Bindings.format("%.0f", this.sliderMusicVolume.valueProperty())
        );
        this.labelSoundEffectsVolume.textProperty().bind(
                Bindings.format("%.0f", this.sliderSoundEffectsVolume.valueProperty())
        );
    }

    public void start()
    {
        System.out.println("User selected Settings");

        window = this.anchorPaneRoot.getScene().getWindow();

        this.vboxSettings.setVisible(true);
        this.vboxBackControls.setVisible(true);

        syncElementsWithSettings();
        updateMusic();

        updateButtons();
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back");

        if (isSettingsChanged())
        {
            CenteredAlert alert = new CenteredAlert(window, Alert.AlertType.CONFIRMATION, "Changes Not Saved",
                    "Leaving this page will discard the changes in progress.\nLeave anyways?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if(alert.getResult() == ButtonType.NO || alert.getResult() == ButtonType.CANCEL)
            {
                return;
            }
        }

        updateMusic();

        Navigator.switchView(NavEntry.MAIN);
    }

    @FXML
    public void selectLanguage(ActionEvent event)
    {
        updateButtons();
    }

    @FXML
    public void toggleMusic(ActionEvent event)
    {
        if (checkBoxToggleMusic.isSelected())
        {
            sliderMusicVolume.setDisable(false);
            musicPlayer.setVolume(sliderMusicVolume.getValue());
            musicPlayer.play(MusicTrack.MENU);
        }
        else
        {
            sliderMusicVolume.setDisable(true);
            musicPlayer.stop();
        }

        updateButtons();
    }

    @FXML
    public void updateMusicVolume(MouseEvent event)
    {
        musicPlayer.setVolume(sliderMusicVolume.getValue());

        updateButtons();
    }

    @FXML
    public void toggleSoundEffects(ActionEvent event)
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

        updateButtons();
    }

    @FXML
    public void updateSoundEffectsVolume(MouseEvent event)
    {
        //MusicPlayer player = MusicPlayer.getInstance();
        //player.setVolume(sliderSoundEffectsVolume.getValue());

        updateButtons();
    }

    @FXML
    public void saveSettings(ActionEvent event)
    {
        // Language
        settings.setChatEnabled(checkBoxToggleChat.isSelected());
        settings.setMusicEnabled(checkBoxToggleMusic.isSelected());
        settings.setMusicVolume(sliderMusicVolume.getValue());
        settings.setSoundEffectsEnabled(checkBoxToggleSoundEffects.isSelected());
        settings.setSoundEffectsVolume(sliderSoundEffectsVolume.getValue());
        SettingsManager.saveSettings(settings, ".settings.json");

        updateButtons();
    }

    @FXML
    public void cancelSettings(ActionEvent event)
    {
        syncElementsWithSettings();
        updateMusic();

        updateButtons();
    }

    private boolean isSettingsChanged()
    {
        return this.checkBoxToggleChat.isSelected() != settings.isChatEnabled()
                || this.checkBoxToggleMusic.isSelected() != settings.isMusicEnabled()
                || this.sliderMusicVolume.getValue() != settings.getMusicVolume()
                || this.checkBoxToggleSoundEffects.isSelected() != settings.isSoundEffectsEnabled()
                || this.sliderSoundEffectsVolume.getValue() != settings.getSoundEffectsVolume();
    }

    private void syncElementsWithSettings()
    {
        this.checkBoxToggleChat.setSelected(settings.isChatEnabled());
        this.checkBoxToggleMusic.setSelected(settings.isMusicEnabled());
        this.sliderMusicVolume.setValue(settings.getMusicVolume());
        this.sliderMusicVolume.setDisable(!settings.isMusicEnabled());
        this.checkBoxToggleSoundEffects.setSelected(settings.isSoundEffectsEnabled());
        this.sliderSoundEffectsVolume.setValue(settings.getSoundEffectsVolume());
        this.sliderSoundEffectsVolume.setDisable(!settings.isSoundEffectsEnabled());
    }

    private void updateMusic()
    {
        if (settings.isMusicEnabled())
        {
            musicPlayer.setVolume(settings.getMusicVolume());
            musicPlayer.play(MusicTrack.MENU);
        }

        updateButtons();
    }

    private void updateButtons()
    {
        this.buttonSaveSettings.setDisable(!isSettingsChanged());
        this.buttonCancelSettings.setDisable(!isSettingsChanged());
    }
}
