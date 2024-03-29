package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.settings.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControllerMain implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxMainMenu;
    @FXML private VBox vboxSettingsInfoControls;

    @FXML private Button buttonSinglePlayer;
    @FXML private Button buttonMultiPlayer;
    @FXML private Button buttonRulesHelp;

    @FXML private Button buttonSettings;
    @FXML private Button buttonAbout;

    public void initialize() {}

    public void start()
    {
        this.vboxMainMenu.setVisible(true);
        this.vboxSettingsInfoControls.setVisible(true);
    }

    // MainMenu functions =====================================================
    @FXML
    public void selectSinglePlayer(ActionEvent event)
    {
        Navigator.switchView(NavEntry.SINGLE_PLAYER);
    }

    @FXML
    public void selectMultiplayer(ActionEvent event)
    {
        Navigator.switchView(NavEntry.MULTIPLAYER);
    }

    @FXML
    public void selectRulesHelp(ActionEvent event)
    {
        Navigator.switchView(NavEntry.RULES_HELP);
    }

    @FXML
    public void selectSettings(ActionEvent event)
    {
        Navigator.switchView(NavEntry.SETTINGS);
    }

    @FXML
    public void selectAbout(ActionEvent event)
    {
        Navigator.switchView(NavEntry.ABOUT);
    }
}
