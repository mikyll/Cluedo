package it.mikyll.cluedo.controller.game;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.MurderEnvelope;
import it.mikyll.cluedo.model.game.actions.*;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerArtificial;
import it.mikyll.cluedo.model.game.player.PlayerHuman;
import it.mikyll.cluedo.model.networking.ClientStream;
import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import it.mikyll.cluedo.model.sounds.MusicTrack;
import it.mikyll.cluedo.persistence.AssetLoader;
import it.mikyll.cluedo.view.gui.javafx.CenteredAlert;
import it.mikyll.cluedo.view.gui.javafx.BoardView;
import it.mikyll.cluedo.view.gui.javafx.ClueSheet;
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
import javafx.util.StringConverter;

import java.util.*;

public class ControllerGame implements IController {
    // Pause between two actions of an artificial player, so that humans can follow them
    private static final Duration BOT_DELAY = Duration.millis(800);
    private static final double MIN_RIGHT_PANE_WIDTH = 250.0;

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
    // Board, pawns and reachable cells
    private BoardView boardView;

    // Top & bottom bars
    @FXML private Label labelCurrentTurn;
    @FXML private Label labelPlayerInfo;
    @FXML private Label labelDice;
    @FXML private ProgressIndicator progressIndicatorBot;
    @FXML private Button buttonRollDice;
    @FXML private Button buttonOpenClueQuestion;
    @FXML private Button buttonFinalAccusation;
    @FXML private Button buttonEndTurn;

    // Left pane
    @FXML private VBox vboxLeft;
    @FXML private ListView<HBox> listViewPlayers;
    @FXML private TextArea textAreaChat;
    @FXML private TextField textFieldChat;
    @FXML private Button buttonChatSend;

    // Right pane
    @FXML private TabPane tabPaneRight;
    @FXML private Tab tabObjective;
    @FXML private ListView<String> listViewMyClues;
    @FXML private Tab tabClueSheet;
    @FXML private AnchorPane anchorPaneClueSheet;
    @FXML private Tab tabNotepad;
    @FXML private TextArea textAreaNotepad;

    // Clue question pane (also used for the final accusation)
    @FXML private VBox vboxClueQuestion;
    @FXML private Label labelClueQuestionTitle;
    @FXML private ComboBox<Character> comboBoxSuspect;
    @FXML private ComboBox<Weapon> comboBoxWeapon;
    @FXML private Label labelSelectRoom;
    @FXML private ComboBox<Room> comboBoxRoom;
    @FXML private Label labelQuestionWho;
    @FXML private Label labelQuestionWhat;
    @FXML private Label labelQuestionWhere;
    @FXML private Button buttonAskClue;
    private boolean finalAccusationMode;

    // Menu controls
    @FXML private Button buttonResume;

    private Window window;

    private GameCluedo game;
    private ClientStream client;

    private int playerTurn = 1;

    // True once the first turn has begun
    private boolean started;
    // Player whose private information (clues, clue sheet, notepad) is shown. Null when hidden.
    private Player viewer;
    private final Map<Player, ClueSheet> clueSheets = new HashMap<>();
    private PauseTransition botTimer;

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
        // The right pane takes the width left beside a square board, also when the window is resized
        anchorPaneRoot.widthProperty().addListener(e -> fitRightPane());
        anchorPaneCenter.heightProperty().addListener(e -> fitRightPane());

        this.vboxWaitingCharacterSelection.setVisible(false);
        this.vboxCharacterSelection.setVisible(false);
        this.vboxPlayersList.setVisible(false);
        this.vboxMenu.setVisible(false);
        this.vboxClueQuestion.setVisible(false);

        // Clue question pane
        setClueConverter(comboBoxSuspect);
        setClueConverter(comboBoxWeapon);
        setClueConverter(comboBoxRoom);
        comboBoxSuspect.valueProperty().addListener(e -> updateQuestionLabels());
        comboBoxWeapon.valueProperty().addListener(e -> updateQuestionLabels());
        comboBoxRoom.valueProperty().addListener(e -> updateQuestionLabels());

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

        // Get board size (the board is square)
        double boardWidth = Math.min(anchorPaneCenter.getWidth(), anchorPaneCenter.getHeight());

        // Adjust left pane
        this.textAreaChat.setPrefHeight(this.vboxLeft.getHeight() - 50 - this.listViewPlayers.getHeight());

        // Adjust right pane width
        fitRightPane();

        // Character Selection pane
        availableCharacters = new ArrayList<>(this.characters);
        vboxCharacterSelection.setPrefWidth(anchorPaneRoot.getWidth() - (vboxLeft.getWidth() + boardWidth));
        selectedCharacter = 0;
        updateSelectedCharacter();

        if (game != null)
        {
            // Single Player: characters have already been assigned, show the turn order and start
            this.textAreaChat.clear();
            refresh();
            showPlayersListView(new ActionEvent());
        }
        else
        {
            showCharacterSelectionView(new ActionEvent());
        }

        // NB: in Single Player we won't show the turn animation, since there might be multiple human players
    }

    public void setProperties(ClientStream client, List<Player> playersList)
    {
        initPlayersListView(new ArrayList<>(playersList));

        hboxListPlayersFull.getChildren().clear();
        for(int i = 0; i < playersList.size(); i++)
        {
            Player player = playersList.get(i);
            hboxListPlayersFull.getChildren().add(buildVBoxPlayerFullElement(player.getTurn(), player.getCharacter(), player.getUsername(), i == 0));
        }
    }

    // Prepares the view for a new game (single player)
    public void setGame(GameCluedo game)
    {
        stopBot();
        this.game = game;
        this.started = false;
        this.viewer = null;

        // Board, drawn from board.json: it fills the center pane
        this.boardView = new BoardView(game.getBoard(), this.characters);
        AnchorPane.setTopAnchor(boardView, 0.0);
        AnchorPane.setBottomAnchor(boardView, 0.0);
        AnchorPane.setLeftAnchor(boardView, 0.0);
        AnchorPane.setRightAnchor(boardView, 0.0);
        this.anchorPaneCenter.getChildren().setAll(boardView);

        // One clue sheet per player, with their own clues already ticked
        this.clueSheets.clear();
        for (Player player : game.getPlayers())
        {
            ClueSheet clueSheet = new ClueSheet(game.getTotalCluesList());
            for (Clue clue : player.getClues())
                clueSheet.setChecked(clue, true);
            this.clueSheets.put(player, clueSheet);
        }

        // Clue question choices
        List<Character> characterClues = new ArrayList<>();
        List<Weapon> weaponClues = new ArrayList<>();
        for (Clue clue : game.getTotalCluesList())
        {
            if (clue instanceof Character)
                characterClues.add((Character) clue);
            else if (clue instanceof Weapon)
                weaponClues.add((Weapon) clue);
        }
        this.comboBoxSuspect.getItems().setAll(characterClues);
        this.comboBoxWeapon.getItems().setAll(weaponClues);
        this.comboBoxRoom.getItems().setAll(game.getBoard().getClueRooms());

        // With a single human player, their information is always shown
        List<Player> humans = getHumanPlayers();
        setViewer(humans.size() == 1 ? humans.get(0) : null);
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

        boolean isCurrent = game != null && !game.isOver() && game.getCurrentPlayer().equals(player);
        String suffix = (player instanceof PlayerArtificial ? " (bot)" : "") + (player.isEliminated() ? " - out" : "");
        Label labelPlayer = new Label(player.getTurn() + ". " + player.getUsername() + suffix);
        labelPlayer.setFont(Font.font("System", isCurrent ? FontWeight.BOLD : FontWeight.NORMAL, 16.0));
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

        // The first time, the list shows the turn order: then the game begins
        if (game != null && !started)
        {
            started = true;
            beginTurn();
        }
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

    // Turns ==================================================================
    private void beginTurn()
    {
        if (game.isOver())
        {
            showGameOver();
            return;
        }

        Player player = game.getCurrentPlayer();
        log("Turn of " + player.getUsername() + " (" + player.getCharacter().getName() + ")");

        if (player instanceof PlayerArtificial)
        {
            if (getHumanPlayers().size() > 1)
                setViewer(null);
            refresh();
            scheduleBotStep();
            return;
        }

        // Hot-seat: hide the previous player's information until the next one takes the device
        if (getHumanPlayers().size() > 1)
        {
            setViewer(null);
            refresh();
            showMessage("Next turn", "Pass the device to " + player.getUsername() + ", then press OK.");
            setViewer(player);
        }
        refresh();
    }

    private boolean isHumanTurn()
    {
        Player current = game.getCurrentPlayer();
        return !game.isOver() && current instanceof PlayerHuman && current.equals(viewer);
    }

    @FXML
    public void rollDice(ActionEvent event)
    {
        Player player = game.getCurrentPlayer();
        ActionRollDice action = new ActionRollDice(game, player);
        if (!isHumanTurn() || !action.isAllowed())
            return;

        action.execute();
        log(player.getUsername() + " rolled " + action.getResult());
        refresh();
    }

    private void moveTo(int y, int x)
    {
        Player player = game.getCurrentPlayer();
        ActionMove action = new ActionMove(game, player, game.getDiceResult(), player.getPosition(), new int[] {y, x});
        if (!isHumanTurn() || !action.isAllowed())
            return;

        action.execute();
        logMove(player);
        refresh();
    }

    @FXML
    public void endTurn(ActionEvent event)
    {
        ActionEndTurn action = new ActionEndTurn(game, game.getCurrentPlayer());
        if (!isHumanTurn() || !action.isAllowed())
            return;

        action.execute();
        beginTurn();
    }

    // Clue question & final accusation =======================================
    @FXML
    public void showClueQuestionView(ActionEvent event)
    {
        if (!isHumanTurn() || !game.canAskClue(game.getCurrentPlayer()))
            return;

        this.finalAccusationMode = false;
        this.labelClueQuestionTitle.setText("Ask a Clue Question");
        this.buttonAskClue.setText("Ask");
        this.comboBoxRoom.setValue(game.getRoomOf(game.getCurrentPlayer()));
        setRoomSelectionVisible(false);
        openClueQuestionView();
    }

    @FXML
    public void showFinalAccusationView(ActionEvent event)
    {
        if (!isHumanTurn() || !game.canMakeFinalAccusation(game.getCurrentPlayer()))
            return;

        this.finalAccusationMode = true;
        this.labelClueQuestionTitle.setText("Final Accusation");
        this.buttonAskClue.setText("Accuse");
        setRoomSelectionVisible(true);
        openClueQuestionView();
    }

    private void openClueQuestionView()
    {
        if (comboBoxSuspect.getValue() == null)
            comboBoxSuspect.getSelectionModel().selectFirst();
        if (comboBoxWeapon.getValue() == null)
            comboBoxWeapon.getSelectionModel().selectFirst();
        if (comboBoxRoom.getValue() == null)
            comboBoxRoom.getSelectionModel().selectFirst();
        updateQuestionLabels();

        this.vboxClueQuestion.setVisible(true);
        this.setBlur(borderPaneGame, true);
    }

    @FXML
    public void closeClueQuestionView(ActionEvent event)
    {
        this.vboxClueQuestion.setVisible(false);
        this.setBlur(borderPaneGame, false);
    }

    @FXML
    public void confirmClueQuestion(ActionEvent event)
    {
        Player player = game.getCurrentPlayer();
        Character character = comboBoxSuspect.getValue();
        Weapon weapon = comboBoxWeapon.getValue();
        Room room = comboBoxRoom.getValue();
        if (character == null || weapon == null || room == null)
            return;

        closeClueQuestionView(event);

        if (!finalAccusationMode)
        {
            ActionAskClue action = new ActionAskClue(game, player, character, weapon);
            if (!isHumanTurn() || !action.isAllowed())
                return;

            action.execute();
            handleClueQuestion(action.getResult());
            refresh();
            return;
        }

        ActionFinalAccusation action = new ActionFinalAccusation(game, player, room, character, weapon);
        if (!isHumanTurn() || !action.isAllowed())
            return;

        action.execute();
        logFinalAccusation(player, character, weapon, room, action.isCorrect());
        if (game.isOver())
        {
            refresh();
            showGameOver();
            return;
        }
        showMessage("Final accusation", "Wrong! You are out of the game, but you will still answer the clue questions.");
        beginTurn();
    }

    private void setRoomSelectionVisible(boolean value)
    {
        this.labelSelectRoom.setVisible(value);
        this.labelSelectRoom.setManaged(value);
        this.comboBoxRoom.setVisible(value);
        this.comboBoxRoom.setManaged(value);
    }

    private void updateQuestionLabels()
    {
        labelQuestionWho.setText("Was it " + (comboBoxSuspect.getValue() == null ? "..." : comboBoxSuspect.getValue().getName()));
        labelQuestionWhat.setText("with the " + (comboBoxWeapon.getValue() == null ? "..." : comboBoxWeapon.getValue().getName()));
        labelQuestionWhere.setText("in the " + (comboBoxRoom.getValue() == null ? "..." : comboBoxRoom.getValue().getName()) + "?");
    }

    /*
     * The answerer shows a clue to the asker. Humans choose which clue to show, bots choose by themselves.
     * In hot-seat, the device is passed to the answerer and then back to the asker.
     */
    private void handleClueQuestion(ClueQuestion question)
    {
        Player asker = question.getAsker();
        log(asker.getUsername() + " asks: " + question.getCharacter().getName() + ", " + question.getWeapon().getName()
                + ", " + question.getRoom().getName());

        if (!question.hasAnswerer())
        {
            log("Nobody could answer");
            if (asker instanceof PlayerHuman)
                showMessage("Clue question", "Nobody could answer!");
            return;
        }

        Player answerer = question.getAnswerer();
        Clue clue;
        if (answerer instanceof PlayerArtificial)
        {
            clue = ((PlayerArtificial) answerer).chooseClueToShow(question);
        }
        else
        {
            if (getHumanPlayers().size() > 1)
                passDeviceTo(answerer, asker.getUsername() + " asked a clue question that you can answer.");
            clue = askClueToShow(answerer, question);
        }
        new ActionShowClue(game, answerer, clue).execute();
        log(answerer.getUsername() + " showed a clue to " + asker.getUsername());

        if (asker instanceof PlayerHuman)
        {
            if (getHumanPlayers().size() > 1)
                passDeviceTo(asker, answerer.getUsername() + " answered your clue question.");
            clueSheets.get(asker).setChecked(clue, true);
            showMessage("Clue question", answerer.getUsername() + " showed you: " + clue.getName());
        }
        else if (getHumanPlayers().size() > 1)
        {
            // A bot asked: hide the answerer's information again
            setViewer(null);
        }
    }

    private void passDeviceTo(Player player, String reason)
    {
        setViewer(null);
        refresh();
        showMessage("Clue question", reason + "\nPass the device to " + player.getUsername() + ", then press OK.");
        setViewer(player);
        refresh();
    }

    private Clue askClueToShow(Player answerer, ClueQuestion question)
    {
        Map<String, Clue> options = new LinkedHashMap<>();
        for (Clue clue : question.getAnswerOptions())
            options.put(clue.getName(), clue);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.keySet().iterator().next(), options.keySet());
        dialog.initOwner(window);
        dialog.setTitle("Clue question");
        dialog.setHeaderText(question.getAsker().getUsername() + " asks: " + question.getCharacter().getName() + ", "
                + question.getWeapon().getName() + ", " + question.getRoom().getName());
        dialog.setContentText(answerer.getUsername() + ", choose the clue to show:");

        // A clue must be shown: if the dialog is closed, the selected one is used
        Optional<String> result = dialog.showAndWait();
        return options.get(result.orElse(dialog.getSelectedItem()));
    }

    // Artificial players =====================================================
    private void scheduleBotStep()
    {
        stopBot();
        botTimer = new PauseTransition(BOT_DELAY);
        // Dialogs can't be shown while an animation is running: run the step right after it
        botTimer.setOnFinished(e -> Platform.runLater(this::botStep));
        botTimer.play();
    }

    private void stopBot()
    {
        if (botTimer != null)
            botTimer.stop();
        botTimer = null;
    }

    private void botStep()
    {
        if (game == null || game.isOver() || !(game.getCurrentPlayer() instanceof PlayerArtificial))
            return;

        PlayerArtificial bot = (PlayerArtificial) game.getCurrentPlayer();
        GameAction action = bot.nextAction(game);
        action.execute();

        if (action instanceof ActionRollDice)
            log(bot.getUsername() + " rolled " + ((ActionRollDice) action).getResult());
        else if (action instanceof ActionMove)
            logMove(bot);
        else if (action instanceof ActionAskClue)
            handleClueQuestion(((ActionAskClue) action).getResult());
        else if (action instanceof ActionFinalAccusation)
        {
            boolean correct = ((ActionFinalAccusation) action).isCorrect();
            log(bot.getUsername() + " makes the final accusation: " + (correct ? "correct!" : "wrong, out of the game"));
        }

        if (game.isOver())
        {
            refresh();
            showGameOver();
        }
        else if (game.getCurrentPlayer().equals(bot))
        {
            refresh();
            scheduleBotStep();
        }
        else
        {
            beginTurn();
        }
    }

    // Game over ==============================================================
    private void showGameOver()
    {
        stopBot();
        MurderEnvelope envelope = game.getMurderEnvelope();
        Player winner = game.getWinner();
        String result = (winner == null ? "Nobody solved the mystery." : winner.getUsername() + " solved the mystery!")
                + "\n\nIt was " + envelope.getMurderer().getName() + ", with the " + envelope.getWeapon().getName()
                + ", in the " + envelope.getRoom().getName() + ".";
        log("Game over. " + result.replace("\n", " "));
        refresh();
        showMessage("Game over", result);
    }

    // Board ==================================================================

    // Draws the pawns and, during a human turn, the reachable cells (click one of them to move there)
    private void drawBoard()
    {
        if (game == null || boardView == null)
            return;

        Player current = game.getCurrentPlayer();
        int[][] destinations = isHumanTurn() && game.canMove(current) ? game.getCurrentDestinations() : null;
        boardView.update(game.getPlayers(), game.isOver() ? null : current, destinations, this::moveTo);
    }

    // Refreshes labels, buttons, players list, private information and board
    private void refresh()
    {
        if (game == null)
            return;

        Player current = game.getCurrentPlayer();
        boolean humanTurn = isHumanTurn();

        labelCurrentTurn.setText(game.isOver() ? "Game over" : "Turn: " + current.getUsername() + " (" + current.getCharacter().getName() + ")");
        labelDice.setText(game.getDiceResult() < 0 ? "-" : String.valueOf(game.getDiceResult()));
        progressIndicatorBot.setVisible(!game.isOver() && current instanceof PlayerArtificial);

        buttonRollDice.setDisable(!humanTurn || !game.canRollDices(current));
        buttonOpenClueQuestion.setDisable(!humanTurn || !game.canAskClue(current));
        buttonFinalAccusation.setDisable(!humanTurn || !game.canMakeFinalAccusation(current));
        buttonEndTurn.setDisable(!humanTurn || !game.canEndTurn(current));

        // Player info and hints
        if (viewer == null)
        {
            labelPlayerInfo.setText("");
        }
        else
        {
            Room room = game.getRoomOf(viewer);
            String info = viewer.getUsername() + " (" + viewer.getCharacter().getName() + ")" + (viewer.isEliminated() ? " - out of the game" : "")
                    + "\nPosition: " + (room == null ? "corridor" : room.getName());
            if (humanTurn && game.canMove(current))
                info += "\nClick a green cell to move" + (game.getDiceResult() < 0 ? " (trapdoor) or roll the dice" : "");
            else if (humanTurn && game.canAskClue(current))
                info += "\nYou can ask a clue question";
            labelPlayerInfo.setText(info);
        }

        initPlayersListView(new ArrayList<>(game.getPlayers()));
        drawBoard();
    }

    // Shows the clues, clue sheet and notepad of the given player (null hides them)
    private void setViewer(Player player)
    {
        if (viewer != null)
            viewer.setNotebook(textAreaNotepad.getText());

        this.viewer = player;

        listViewMyClues.getItems().clear();
        anchorPaneClueSheet.getChildren().clear();
        textAreaNotepad.setText(player == null ? "" : player.getNotebook());
        textAreaNotepad.setDisable(player == null);
        if (player == null)
            return;

        for (Clue clue : player.getClues())
            listViewMyClues.getItems().add(clue.getName());

        ClueSheet clueSheet = clueSheets.get(player);
        ScrollPane scrollPane = new ScrollPane(clueSheet);
        scrollPane.setPadding(new Insets(5.0));
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        anchorPaneClueSheet.getChildren().add(scrollPane);
    }

    private void fitRightPane()
    {
        double width = anchorPaneRoot.getWidth() - vboxLeft.getWidth() - anchorPaneCenter.getHeight();
        this.tabPaneRight.setPrefWidth(Math.max(MIN_RIGHT_PANE_WIDTH, width));
    }

    @FXML
    public void showClueSheetTab(ActionEvent event)
    {
        tabPaneRight.getSelectionModel().select(tabClueSheet);
    }

    @FXML
    public void showNotepadTab(ActionEvent event)
    {
        tabPaneRight.getSelectionModel().select(tabNotepad);
    }

    // Utilities ==============================================================
    private List<Player> getHumanPlayers()
    {
        List<Player> result = new ArrayList<>();
        if (game != null)
        {
            for (Player player : game.getPlayers())
            {
                if (player instanceof PlayerHuman)
                    result.add(player);
            }
        }
        return result;
    }

    private void logMove(Player player)
    {
        Room room = game.getRoomOf(player);
        log(player.getUsername() + (room == null ? " moved" : " entered the " + room.getName()));
    }

    private void logFinalAccusation(Player player, Character character, Weapon weapon, Room room, boolean correct)
    {
        log(player.getUsername() + " makes the final accusation: " + character.getName() + ", " + weapon.getName() + ", "
                + room.getName() + (correct ? " - correct!" : " - wrong, out of the game"));
    }

    // Game events are shown in the chat area
    private void log(String text)
    {
        textAreaChat.appendText("> " + text + "\n");
    }

    private void showMessage(String title, String text)
    {
        // Buttons and board must reflect the current state while the dialog is open
        refresh();
        CenteredAlert alert = new CenteredAlert(this.window, AlertType.INFORMATION, title, text);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static <T extends Clue> void setClueConverter(ComboBox<T> comboBox)
    {
        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T clue) {
                return clue == null ? "" : clue.getName();
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
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
        stopBot();
        if (viewer != null)
            viewer.setNotebook(textAreaNotepad.getText());
        this.game = null;
        this.vboxMenu.setVisible(false);
        Navigator.switchView(NavEntry.MAIN);
    }

    private void setBlur(Node n, boolean value)
    {
        GaussianBlur effect = new GaussianBlur();
        effect.setRadius(15.0);
        n.setEffect(value ? effect : null);
    }
}
