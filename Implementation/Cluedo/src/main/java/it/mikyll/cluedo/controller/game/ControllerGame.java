package it.mikyll.cluedo.controller.game;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.clues.Characters;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.networking.ClientStream;
import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import it.mikyll.cluedo.model.sounds.MusicTrack;
import it.mikyll.cluedo.persistence.AssetLoader;
import it.mikyll.cluedo.view.gui.javafx.CenteredAlert;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.*;

public class ControllerGame implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private BorderPane borderPaneGame;
    @FXML private VBox vboxMenu;

    // Dice stuff
    private final Image imageRollingDice;
    private final List<Image> diceImagesList;

    // Character selection pane
    @FXML private VBox vboxWaitingCharacterSelection;

    @FXML private VBox vboxCharacterSelection;
    @FXML private Label labelCharacterSelectionTitle;
    @FXML private ImageView imageViewCharacterSelection;
    @FXML private Button buttonPreviousCharacter;
    @FXML private Button buttonNextCharacter;
    @FXML private Label labelCharacterSelectionName;
    @FXML private Label labelCharacterSelectionColor;
    @FXML private Label labelCharacterSelectionDescription;
    @FXML private Tooltip tooltipCharacterSelectionDescription;
    @FXML private Button buttonConfirmCharacter;
    private List<Character> characters;
    private Map<String, Image> mapCharacterImages;
    private List<Character> availableCharacters;
    private int selectedCharacter = 0;


    /*private List<Characters> availableCharacters;
    private final Image imageCharacterSelection;
    private final Map<Characters, Image> mapCharacterImages;
    private int selectedCharacter = 0;*/

    // PlayersList pane
    @FXML private VBox vboxPlayersList;
    @FXML private HBox hboxListPlayersFull;
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

    private Window window;

    private GameCluedo game;
    private ClientStream client;

    private int playerTurn = 1;

    public ControllerGame()
    {
        // Load characters
        this.characters = AssetLoader.loadCharacters();
        this.mapCharacterImages = new HashMap<>();
        for (Character character : this.characters) {
            String filename = character.getId() + ".png";
            Image characterImage = new Image(ControllerGame.class.getResource(Settings.RESOURCES_PATH + "images/characters/" + filename).toString());
            this.mapCharacterImages.put(character.getId(), characterImage);
        }

        // Load dice images
        this.imageRollingDice = new Image(ControllerGame.class.getResource(Settings.RESOURCES_PATH + "images/dice/dice_rolling.gif").toString());
        this.diceImagesList = new ArrayList<>();
        for(int i = 1; i <= 6; i++)
        {
            this.diceImagesList.add(new Image(ControllerGame.class.getResource(Settings.RESOURCES_PATH + "images/dice/dice" + i + ".png").toString()));
        }

        // Load character images
        /*this.imageCharacterSelection = new Image(ControllerGame.class.getResource(Settings.RESOURCES_PATH + "images/characters/scarlet.png").toString());
        this.mapCharacterImages = new HashMap<>();
        for(int i = 0; i < Characters.values().length; i++)
        {
            Characters character = Characters.values()[i];
            String name = character.toString().toLowerCase();
            Image characterImage = new Image(ControllerGame.class.getResource(Settings.RESOURCES_PATH + "images/characters/" + name + ".png").toString());
            this.mapCharacterImages.put(character, characterImage);
        }*/
    }

    @Override
    public void initialize()
    {
        imageViewBoard.fitWidthProperty().bind(anchorPaneCenter.widthProperty());
        imageViewBoard.fitHeightProperty().bind(anchorPaneCenter.heightProperty());
        imageViewBoard.setPreserveRatio(true);
        imageViewBoard.setManaged(false);
        imageViewBoardLabels.fitWidthProperty().bind(anchorPaneCenter.widthProperty());
        imageViewBoardLabels.fitHeightProperty().bind(anchorPaneCenter.heightProperty());
        imageViewBoardLabels.setPreserveRatio(true);
        imageViewBoardLabels.setManaged(false);
        
        this.vboxWaitingCharacterSelection.setVisible(false);
        this.vboxCharacterSelection.setVisible(false);
        this.vboxPlayersList.setVisible(false);
        this.vboxMenu.setVisible(false);

        Platform.runLater(() -> {
            this.anchorPaneRoot.getScene().setOnKeyPressed(e -> {
                if(e.getCode() == KeyCode.ESCAPE)
                    this.toggleMenu(new ActionEvent());
            });
        });
    }

    public void start()
    {
        System.out.println("User selected Game");

        this.window = anchorPaneRoot.getScene().getWindow();

        Settings settings = Settings.getInstance();
        if (settings.isMusicEnabled())
        {
            MusicPlayer musicPlayer = MusicPlayer.getInstance();
            musicPlayer.setVolume(settings.getMusicVolume());
            musicPlayer.play(MusicTrack.GAME);
        }

        Navigator.setFullscreen(true);

        // Get board size
        Image boardImage = imageViewBoard.getImage();
        double boardAspectRatio = boardImage.getWidth() / boardImage.getHeight();
        double boardWidth = Math.min(imageViewBoard.getFitWidth(), imageViewBoard.getFitHeight() * boardAspectRatio);
        double boardHeight = Math.min(imageViewBoard.getFitHeight(), imageViewBoard.getFitWidth() / boardAspectRatio);

        // Adjust left pane
        this.textAreaChat.setPrefHeight(this.vboxLeft.getHeight() - 50 - this.listViewPlayers.getHeight());

        // Adjust right pane width
        this.tabPaneRight.setPrefWidth(anchorPaneRoot.getWidth() - (vboxLeft.getWidth() + boardWidth));

        // Character Selection pane
        availableCharacters = new ArrayList<>(this.characters);
        vboxCharacterSelection.setPrefWidth(anchorPaneRoot.getWidth() - (vboxLeft.getWidth() + boardWidth));
        selectedCharacter = 0;
        updateSelectedCharacter();

        // PlayersList pane
        //showRollingDiceTurn(3);
        showCharacterSelectionView(new ActionEvent());

        // NB: in Single Player we won't show the turn animation, since there might be multiple human players

        // Character selection
    }

    public void setProperties(ClientStream client, List<Player> playersList)
    {
        initPlayersListView(playersList);

        hboxListPlayersFull.getChildren().clear();
        for(int i = 0; i < playersList.size(); i++)
        {
            Player player = playersList.get(i);
            hboxListPlayersFull.getChildren().add(buildVBoxPlayerFullElement(player.getTurn(), player.getCharacter(), player.getUsername(), i == 0));
        }
    }

    // Left view ==============================================================
    private void initPlayersListView(List<Player> playersList)
    {
        playersList.sort((player1, player2) -> Integer.compare(player1.getTurn(), player2.getTurn()));

        this.listViewPlayers.getItems().clear();
        for (int i = 0; i < playersList.size(); i++)
        {
            this.listViewPlayers.getItems().add(buildListViewPlayerElement(playersList.get(i)));
        }
    }

    private HBox buildListViewPlayerElement(Player player)
    {
        HBox hboxResult = new HBox();
        hboxResult.setPrefHeight(20.0);
        hboxResult.setAlignment(Pos.CENTER_LEFT);

        Label labelPlayer = new Label(player.getTurn() + ". " + player.getUsername());
        labelPlayer.setFont(Font.font(16.0));
        labelPlayer.setPrefWidth(220.0);
        labelPlayer.setAlignment(Pos.CENTER_LEFT);
        hboxResult.getChildren().add(labelPlayer);

        if (player.getCharacter() != null)
        {
            Label labelCharacter = new Label();
            labelCharacter.setPrefWidth(20.0);
            labelCharacter.setPrefHeight(20.0);
            labelCharacter.setAlignment(Pos.CENTER_LEFT);
            labelCharacter.setStyle("-fx-background-color: " + player.getCharacter().getColor() + "; -fx-background-radius: 50; -fx-border-color: black; -fx-border-radius: 50;");
            hboxResult.getChildren().add(labelCharacter);
        }

        return hboxResult;
    }

    // Turn view ==============================================================
    @FXML
    public void showRollingDiceTurn(ActionEvent event) {
        this.showRollingDiceTurn(4);
    }

    private void showRollingDiceTurn(int diceValue)
    {
        if (diceValue < 1 || diceValue > 6)
            throw new IllegalArgumentException("Invalid dice value");

        CenteredAlert alert = new CenteredAlert(this.window, AlertType.INFORMATION, "Turn", "Rolling dice for your turn...");
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setHeaderText("");
        alert.getDialogPane().setMaxSize(400, 200);
        alert.centerAlert();
        HBox hboxRollingDice = new HBox();
        hboxRollingDice.setAlignment(Pos.CENTER);
        hboxRollingDice.setSpacing(50.0);
        ImageView imageViewDice = new ImageView(imageRollingDice);
        Label labelTurn = new Label("Rolling dice...");
        labelTurn.setFont(Font.font(24.0));
        hboxRollingDice.getChildren().add(imageViewDice);
        hboxRollingDice.getChildren().add(labelTurn);
        alert.setGraphic(hboxRollingDice);
        ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().getChildren().get(2);
        alert.getDialogPane().getChildren().remove(1);
        buttonBar.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished((ActionEvent e) -> {
            imageViewDice.setImage(diceImagesList.get(diceValue - 1));
            String suffix = diceValue == 1 ? "st" : diceValue == 2 ? "nd" : diceValue == 3 ? "rd" : "th";
            labelTurn.setText("Your turn is: " + diceValue + suffix);
            buttonBar.setDisable(false);
        });
        alert.setOnShowing((DialogEvent e) -> {
            this.setBlur(borderPaneGame, true);
            delay.play();
        });
        alert.setOnCloseRequest((DialogEvent e) -> {
            // TODO: Update playersList
            this.setBlur(borderPaneGame, false);
        });
        alert.show();
    }

    // Character Selection ====================================================
    private void updateSelectedCharacter()
    {
        Character currentCharacter = this.characters.get(selectedCharacter);

        imageViewCharacterSelection.setImage(this.mapCharacterImages.get(currentCharacter.getId()));
        labelCharacterSelectionName.setText(currentCharacter.getName());
        String styleNew = labelCharacterSelectionColor.getStyle().replaceFirst("(-fx-background-color:)[^;]+;", "-fx-background-color: " + currentCharacter.getColor() + ";");
        labelCharacterSelectionColor.setStyle(styleNew);
        labelCharacterSelectionDescription.setText(currentCharacter.getDescription());
        tooltipCharacterSelectionDescription.setText(currentCharacter.getDescription());

        buttonPreviousCharacter.setDisable(selectedCharacter <= 0);
        buttonNextCharacter.setDisable(selectedCharacter >= mapCharacterImages.keySet().size() - 1);
    }

    @FXML
    public void showCharacterSelectionView(ActionEvent event)
    {
        // Set label title for player
        this.labelCharacterSelectionTitle.setText("Choose your Character");

        // Set first character available

        this.vboxCharacterSelection.setVisible(true);
        this.setBlur(borderPaneGame, true);
    }

    @FXML
    public void previousCharacter(ActionEvent event)
    {
        if (selectedCharacter <= 0)
            return;

        selectedCharacter--;
        updateSelectedCharacter();
    }

    @FXML
    public void nextCharacter(ActionEvent event)
    {
        if (selectedCharacter >= mapCharacterImages.keySet().size())
            return;

        selectedCharacter++;
        updateSelectedCharacter();
    }

    @FXML
    public void selectCharacter(ActionEvent event)
    {
        // Select character for current player

        this.vboxCharacterSelection.setVisible(false);
        this.setBlur(borderPaneGame, false);
    }

    // Players List Full ======================================================
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

    private VBox buildVBoxPlayerFullElement(int turn, Character character, String username, boolean isPlayer)
    {
        VBox vboxResult = new VBox();
        vboxResult.setAlignment(Pos.TOP_CENTER);
        vboxResult.setPrefWidth(100);

        Label labelTurn = new Label(turn + (turn == 1 ? "st" : (turn == 2 ? "nd" : turn == 3 ? "rd" : "th")));
        labelTurn.setFont(Font.font(14));
        labelTurn.setTextFill(Color.WHITE);
        labelTurn.setAlignment(Pos.CENTER);

        HBox hboxCharacter = new HBox();
        VBox.setMargin(hboxCharacter, new Insets(10, 0, 30, 0));
        hboxCharacter.setSpacing(10);

        Image characterImage = mapCharacterImages.get(character.getId());
        ImageView imageViewCharacter = new ImageView(characterImage);
        imageViewCharacter.setFitWidth(125);
        imageViewCharacter.setFitHeight(125);
        DropShadow imageViewEffect = new DropShadow();
        imageViewEffect.setColor(Color.web(character.getColor()));
        imageViewEffect.setWidth(15);
        imageViewEffect.setHeight(15);
        imageViewEffect.setRadius(7);
        imageViewEffect.setOffsetX(0);
        imageViewEffect.setOffsetY(0);
        imageViewEffect.setSpread(1.0);
        imageViewCharacter.setEffect(imageViewEffect);
        hboxCharacter.getChildren().add(imageViewCharacter);

        Label labelUsername = new Label(username);
        labelUsername.setFont(Font.font("System", isPlayer ? FontWeight.BOLD : FontWeight.NORMAL, 14));
        labelUsername.setTextFill(Color.WHITE);
        labelUsername.setAlignment(Pos.TOP_CENTER);
        labelUsername.setWrapText(true);
        labelUsername.setPrefHeight(40.0);

        Label labelPlayingAs = new Label("playing as:");
        labelPlayingAs.setFont(Font.font(14));
        labelPlayingAs.setTextFill(Color.WHITE);
        labelPlayingAs.setAlignment(Pos.CENTER);

        Label labelCharacterName = new Label(character.getName());
        labelCharacterName.setFont(Font.font(14));
        labelCharacterName.setTextFill(Color.WHITE);
        labelCharacterName.setAlignment(Pos.CENTER);

        vboxResult.getChildren().addAll(labelTurn, hboxCharacter, labelUsername, labelPlayingAs, labelCharacterName);

        return vboxResult;
    }

    @FXML
    public void sendChatMessage(ActionEvent event)
    {
        // TODO
    }

    // Menu Controls ==========================================================
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
