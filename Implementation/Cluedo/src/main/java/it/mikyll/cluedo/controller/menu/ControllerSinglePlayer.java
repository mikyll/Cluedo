package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.controller.game.ControllerGame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ControllerSinglePlayer implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxBackControls;
    @FXML private VBox vboxSinglePlayer;

    @FXML private Button buttonBack;

    // SinglePlayer controls:
    // [...] settings controls
    @FXML private Spinner<Integer> spinnerPlayersHuman;
    @FXML private Spinner<Integer> spinnerPlayersAI;
    @FXML private Button buttonStartSinglePlayer;

    public ControllerSinglePlayer() {}

    public void initialize() {
        this.vboxBackControls.setVisible(true);
        this.vboxSinglePlayer.setVisible(true);

        this.spinnerPlayersHuman.valueProperty().addListener(e -> {
            canStart();
        });
        this.spinnerPlayersAI.valueProperty().addListener(e -> {
            canStart();
        });
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back");

        Navigator.switchView(NavEntry.MAIN);
    }

    @FXML
    public void startGame(ActionEvent event)
    {
        ControllerGame ctrlGame = (ControllerGame) Navigator.getController(NavEntry.GAME);

        Navigator.switchView(NavEntry.GAME);
    }

    public void canStart()
    {
        int playersHuman = this.spinnerPlayersHuman.getValue();
        int playersAI = this.spinnerPlayersAI.getValue();

        this.buttonStartSinglePlayer.setDisable(playersHuman + playersAI < 2
                || playersHuman + playersAI > 6);
    }
}
