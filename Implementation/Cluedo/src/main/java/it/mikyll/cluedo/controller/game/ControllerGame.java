package it.mikyll.cluedo.controller.game;

import it.mikyll.cluedo.controller.menu.ControllerRulesHelp;
import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.networking.ClientStream;
import it.mikyll.cluedo.model.settings.Settings;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ControllerGame implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private BorderPane borderPaneGame;
    @FXML private VBox vboxMenu;

    // Turn pane
    @FXML private VBox vboxTurn;
    @FXML private ImageView imageViewDice;
    @FXML private Label labelTurn;
    @FXML private Button buttonOkTurn;
    private final Image imageRollingDice;
    private final List<Image> diceImagesList;

    // Character selection pane
    @FXML private VBox vboxCharacterSelection;
    @FXML private ImageView imageViewCharacterSelection;
    @FXML private Button buttonPreviousCharacter;
    @FXML private Button buttonNextCharacter;
    @FXML private Label labelCharacterSelectionName;
    @FXML private Label labelCharacterSelectionColor;
    @FXML private Button buttonConfirmCharacter;

    // Players list pane
    @FXML private VBox vboxPlayersList;
    @FXML private Button buttonOkPlayersList;

    // Center pane
    @FXML private AnchorPane anchorPaneCenter;
    @FXML private ImageView imageViewBoard;
    @FXML private ImageView imageViewBoardLabels;

    @FXML private Label labelPawnScarlet;
    @FXML private Label labelPawnPeacock;
    @FXML private Label labelPawnGreen;
    @FXML private Label labelPawnMustard;
    @FXML private Label labelPawnPlum;
    @FXML private Label labelPawnWhite;

    // Left pane
    @FXML private VBox vboxLeft;
    @FXML private ListView<HBox> listViewPlayers;
    @FXML private TextArea textAreaChat;
    @FXML private TextField textFieldChat;
    @FXML private Button buttonChatSend;

    // Right pane
    @FXML private TabPane tabPaneRight;
    @FXML private Tab tabNotepad;
    @FXML private TextArea textAreaNotepad;

    // Menu controls
    @FXML private Button buttonResume;

    private GameCluedo game;
    private ClientStream client;

    private int testTurn = 1;

    public ControllerGame()
    {
        this.diceImagesList = new ArrayList<>();

        this.imageRollingDice = new Image(ControllerRulesHelp.class.getResource(Settings.RESOURCES_PATH + "images/dice_rolling.gif").toString());
        for(int i = 1; i <= 6; i++)
        {
            this.diceImagesList.add(new Image(ControllerRulesHelp.class.getResource(Settings.RESOURCES_PATH + "images/dice" + i + ".png").toString()));
        }
    }

    @Override
    public void initialize()
    {
        if (anchorPaneRoot != null && anchorPaneRoot.getScene() != null)
        {
            Stage stage = (Stage) anchorPaneRoot.getScene().getWindow();
            if (stage != null)
            {
                stage.setFullScreen(true);
            }
        }

        imageViewBoard.fitWidthProperty().bind(anchorPaneCenter.widthProperty());
        imageViewBoard.fitHeightProperty().bind(anchorPaneCenter.heightProperty());
        imageViewBoard.setPreserveRatio(true);
        imageViewBoard.setManaged(false);
        imageViewBoardLabels.fitWidthProperty().bind(anchorPaneCenter.widthProperty());
        imageViewBoardLabels.fitHeightProperty().bind(anchorPaneCenter.heightProperty());
        imageViewBoardLabels.setPreserveRatio(true);
        imageViewBoardLabels.setManaged(false);

        this.vboxTurn.setVisible(false);
        this.vboxCharacterSelection.setVisible(false);
        this.vboxPlayersList.setVisible(false);
        this.vboxMenu.setVisible(false);

        Image boardImage = imageViewBoard.getImage();
        double boardAspectRatio = boardImage.getWidth() / boardImage.getHeight();
        double boardWidth = Math.min(imageViewBoard.getFitWidth(), imageViewBoard.getFitHeight() * boardAspectRatio);
        double boardHeight = Math.min(imageViewBoard.getFitHeight(), imageViewBoard.getFitWidth() / boardAspectRatio);

        // Adjust left pane
        this.textAreaChat.setPrefHeight(this.vboxLeft.getHeight() - 50 - this.listViewPlayers.getHeight());

        // Adjust right pane width
        this.tabPaneRight.setPrefWidth(anchorPaneRoot.getWidth() - (vboxLeft.getWidth() + boardWidth));


        Platform.runLater(() -> {
            this.anchorPaneRoot.getScene().setOnKeyPressed(e -> {
                if(e.getCode() == KeyCode.ESCAPE)
                    this.toggleMenu(new ActionEvent());
            });
        });
    }

    public void setProperties(ClientStream client, Player playerList)
    {

    }

    @FXML
    public void showTurnView(ActionEvent event)
    {
        this.imageViewDice.setImage(imageRollingDice);
        this.labelTurn.setVisible(false);
        this.buttonOkTurn.setDisable(true);

        this.vboxTurn.setVisible(true);
        this.setBlur(borderPaneGame, true);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event1 -> {
            this.imageViewDice.setImage(this.diceImagesList.get(this.testTurn-1));
            this.labelTurn.setText(testTurn + (testTurn == 1 ? "st" : (testTurn == 2 ? "nd" : testTurn == 3 ? "rd" : "th")));
            this.labelTurn.setVisible(true);
            this.buttonOkTurn.setDisable(false);
        });
        delay.play();
    }

    @FXML
    public void closeTurnView()
    {
        this.vboxTurn.setVisible(false);
        this.setBlur(borderPaneGame, false);
    }

    @FXML
    public void showCharacterSelectionView(ActionEvent event) {
        this.vboxCharacterSelection.setVisible(true);
        this.setBlur(borderPaneGame, true);
    }

    @FXML
    public void closeCharacterSelectionView(ActionEvent event) {
        this.vboxCharacterSelection.setVisible(false);
        this.setBlur(borderPaneGame, false);
    }

    @FXML
    public void showPlayersListView(ActionEvent event) {
        this.vboxPlayersList.setVisible(true);
        this.setBlur(borderPaneGame, true);
    }

    @FXML
    public void closePlayersListView(ActionEvent event) {
        this.vboxPlayersList.setVisible(false);
        this.setBlur(borderPaneGame, false);
    }

    @FXML
    public void sendChatMessage(ActionEvent event)
    {
        // TODO
    }

    @FXML
    public void toggleMenu(ActionEvent event)
    {
        this.vboxMenu.setVisible(!this.vboxMenu.isVisible());
    }

    @FXML
    public void quit(ActionEvent event)
    {
        Navigator.switchView(NavEntry.MAIN);
    }

    private void setBlur(Node n, boolean value)
    {
        GaussianBlur effect = new GaussianBlur();
        effect.setRadius(15.0);
        n.setEffect(value ? effect : null);
    }
}
