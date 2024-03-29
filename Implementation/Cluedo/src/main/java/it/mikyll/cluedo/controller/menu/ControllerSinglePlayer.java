package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.controller.game.ControllerGame;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerArtificial;
import it.mikyll.cluedo.model.game.player.PlayerHuman;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.*;

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

    public void initialize()
    {
        this.spinnerPlayersHuman.valueProperty().addListener(e -> {
            this.buttonStartSinglePlayer.setDisable(!canStart());
        });
        this.spinnerPlayersAI.valueProperty().addListener(e -> {
            this.buttonStartSinglePlayer.setDisable(!canStart());
        });
    }

    public void start()
    {
        System.out.println("User selected Single Player");

        this.vboxBackControls.setVisible(true);
        this.vboxSinglePlayer.setVisible(true);
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
        if (!canStart())
            return;

        List<Player> listPlayers = new ArrayList<>();

        List<Character> availableCharacters = new ArrayList<>(Arrays.asList(Character.values()));
        Random rand = new Random();
        for (int i = 0; i < this.spinnerPlayersHuman.getValue(); i++)
        {
            Player humanPlayer = new PlayerHuman("Player " + (i+1));
            int iCharacter = rand.nextInt(availableCharacters.size());
            humanPlayer.setCharacter(availableCharacters.get(iCharacter));
            availableCharacters.remove(iCharacter);
            listPlayers.add(humanPlayer);
        }
        for (int i = 0; i < this.spinnerPlayersAI.getValue(); i++)
        {
            Player bot = new PlayerArtificial("Bot " + (i+1));
            int iCharacter = rand.nextInt(availableCharacters.size());
            bot.setCharacter(availableCharacters.get(iCharacter));
            availableCharacters.remove(iCharacter);
            listPlayers.add(bot);
        }

        // Set turn
        Collections.shuffle(listPlayers);
        int turn = 1;
        for (Player listPlayer : listPlayers) {
            listPlayer.setTurn(turn);
            turn++;
        }

        ControllerGame ctrlGame = (ControllerGame) Navigator.getController(NavEntry.GAME);
        ctrlGame.setProperties(null, listPlayers);

        Navigator.switchView(NavEntry.GAME);
    }

    public boolean canStart()
    {
        int playersHuman = this.spinnerPlayersHuman.getValue();
        int playersAI = this.spinnerPlayersAI.getValue();

        return playersHuman + playersAI >= 2 && playersHuman + playersAI <= 6;
    }
}
