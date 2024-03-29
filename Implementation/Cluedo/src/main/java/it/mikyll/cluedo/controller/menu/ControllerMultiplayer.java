package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.networking.ClientStream;
import it.mikyll.cluedo.model.networking.ServerStream;
import it.mikyll.cluedo.model.networking.User;
import it.mikyll.cluedo.model.networking.message.IMessageHandler;
import it.mikyll.cluedo.model.networking.message.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.*;
import java.util.regex.Pattern;

public class ControllerMultiplayer implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxBackControls;
    @FXML private VBox vboxMultiPlayer;
    @FXML private VBox vboxCreateNewLobby;
    @FXML private VBox vboxJoinExistingLobby;

    @FXML private Button buttonBack;

    // MultiPlayer controls:
    @FXML private Button buttonSelectCreateNewLobby;
    @FXML private Button buttonSelectJoinExistingLobby;

    // Create New Lobby controls:
    @FXML private TextField textFieldUsernameCreate;
    @FXML private Label labelErrorUsernameCreate;
    @FXML private Spinner<Integer> spinnerLobbySizeMin;
    @FXML private Spinner<Integer> spinnerLobbySizeMax;
    @FXML private TextField textFieldPortCreate;
    @FXML private Label labelErrorPortCreate;
    @FXML private Button buttonCreateLobby;

    // Join Existing Lobby controls:
    @FXML private TextField textFieldUsernameJoin;
    @FXML private Label labelErrorUsernameJoin;
    @FXML private TextField textFieldIP;
    @FXML private Label labelErrorIP;
    @FXML private TextField textFieldPortJoin;
    @FXML private Label labelErrorPortJoin;
    @FXML private HBox hboxConnectionJoin;
    @FXML private Button buttonJoinLobby;

    private static final Pattern PATTERN_IP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public ControllerMultiplayer() {}

    public void initialize()
    {
        this.textFieldPortCreate.setPromptText("default: " + ServerStream.DEFAULT_PORT);
        this.textFieldPortJoin.setPromptText("default: " + ServerStream.DEFAULT_PORT);

        this.spinnerLobbySizeMin.valueProperty().addListener((changed, oldval, newval) -> {
            this.spinnerLobbySizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(newval, 6, this.spinnerLobbySizeMax.getValue()));
        });
        this.spinnerLobbySizeMax.valueProperty().addListener((changed, oldval, newval) -> {
            this.spinnerLobbySizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, newval, this.spinnerLobbySizeMin.getValue()));
        });
    }

    public void start()
    {
        System.out.println("User selected Multiplayer");

        this.vboxBackControls.setVisible(true);
        this.vboxMultiPlayer.setVisible(true);
        this.vboxCreateNewLobby.setVisible(false);
        this.vboxJoinExistingLobby.setVisible(false);
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        if (vboxMultiPlayer.isVisible())
        {
            System.out.println("User selected Back (Main)");
            Navigator.switchView(NavEntry.MAIN);
        }
        else if (vboxCreateNewLobby.isVisible())
        {
            System.out.println("User selected Back (Multiplayer)");
            vboxCreateNewLobby.setVisible(false);
            vboxMultiPlayer.setVisible(true);
        }
        else if (vboxJoinExistingLobby.isVisible())
        {
            System.out.println("User selected Back (Multiplayer)");
            vboxJoinExistingLobby.setVisible(false);
            vboxMultiPlayer.setVisible(true);
        }
    }

    @FXML
    public void selectCreateNewLobby(ActionEvent event)
    {
        System.out.println("User selected Create New Lobby");

        this.vboxMultiPlayer.setVisible(false);
        this.vboxCreateNewLobby.setVisible(true);

        // Reset fields
        this.textFieldUsernameCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.textFieldUsernameCreate.requestFocus();
        this.labelErrorUsernameCreate.setVisible(false);

        // Reset spinner
        this.spinnerLobbySizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 2));
        this.spinnerLobbySizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 6));

        this.textFieldPortCreate.setText("");
        this.textFieldPortCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.labelErrorPortCreate.setVisible(false);

        this.buttonCreateLobby.setDisable(!User.validateUsername(this.textFieldUsernameCreate.getText()));
    }

    @FXML
    public void selectJoinExistingLobby(ActionEvent event)
    {
        System.out.println("User selected Join Existing Lobby");

        this.vboxMultiPlayer.setVisible(false);
        this.vboxJoinExistingLobby.setVisible(true);

        // Reset fields
        this.textFieldUsernameJoin.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.textFieldUsernameJoin.requestFocus();
        this.labelErrorUsernameJoin.setVisible(false);

        this.textFieldIP.setText("");
        this.textFieldIP.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.labelErrorIP.setVisible(false);

        this.textFieldPortJoin.setText("");
        this.textFieldPortCreate.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.labelErrorPortJoin.setVisible(false);

        this.hboxConnectionJoin.setVisible(false);

        this.buttonJoinLobby.setDisable(!User.validateUsername(this.textFieldUsernameJoin.getText()));
    }

    // Create New Lobby functions =============================================

    private boolean validatePort(String port)
    {
        boolean result;
        int p;

        try {
            p = Integer.parseInt(port);
            result = (p > 1023 && p < 65536);
        } catch(Exception e) {
            result = false;
        }

        return result;
    }

    @FXML
    private boolean checkEnableCreateNewLobby()
    {
        boolean disableCreateButton = false, errorUsername = false, errorPort = false;

        if(!User.validateUsername(this.textFieldUsernameCreate.getText()))
        {
            disableCreateButton = true;
            errorUsername = true;
        }

        if(!(this.validatePort(this.textFieldPortCreate.getText()) || this.textFieldPortCreate.getText().isEmpty()))
        {
            disableCreateButton = true;
            errorPort = true;
        }

        this.buttonCreateLobby.setDisable(disableCreateButton);
        this.labelErrorUsernameCreate.setVisible(errorUsername);
        this.textFieldUsernameCreate.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.labelErrorPortCreate.setVisible(errorPort);
        this.textFieldPortCreate.setStyle(errorPort ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");

        return !disableCreateButton;
    }

    @FXML
    public void createLobby(ActionEvent event)
    {
        System.out.println("User selected Create Lobby");

        if (!this.checkEnableCreateNewLobby())
            return;

        if(this.textFieldPortCreate.getText().isEmpty())
            this.textFieldPortCreate.setText("" + ServerStream.DEFAULT_PORT);

        String username = this.textFieldUsernameCreate.getText();

        // create new room -> start server (if OK switch to Server Room View)
        try{
            ServerStream server = new ServerStream(
                username,
                Integer.parseInt(this.textFieldPortCreate.getText()),
                this.spinnerLobbySizeMin.getValue(),
                this.spinnerLobbySizeMax.getValue(),
                true
            );

            this.vboxMultiPlayer.setVisible(true);
            this.vboxCreateNewLobby.setVisible(false);

            ControllerLobbyServer ctrlLS = (ControllerLobbyServer) Navigator.getController(NavEntry.LOBBY_SERVER);
            ctrlLS.setProperties(server, username);

            // Go to Lobby
            Navigator.switchView(NavEntry.LOBBY_SERVER);
        } catch (IOException e) {
            System.out.println("Server: ServerSocket creation failed");
            if(e instanceof BindException)
            {
                System.out.println("Server: another socket is already binded to this address and port");
                try(final DatagramSocket socket = new DatagramSocket()) {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    String privateIP = socket.getLocalAddress().getHostAddress();

                    Alert alert = new Alert(Alert.AlertType.ERROR, "Room creation failed");
                    alert.setTitle("Error Dialog");
                    alert.setContentText("Another socket is already binded to " + privateIP + ":" + this.textFieldPortCreate.getText());
                    alert.show();
                } catch (SocketException e1) {
                    e1.printStackTrace();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    // Join Existing Lobby functions ==========================================
    private boolean validateIPv4(String address)
    {
        return PATTERN_IP.matcher(address).matches();
    }

    @FXML
    private boolean checkEnableJoinExistingLobby()
    {
        boolean disableJoinButton = false, errorUsername = false, errorIP = false, errorPort = false;

        if(!User.validateUsername(this.textFieldUsernameJoin.getText()))
        {
            disableJoinButton = true;
            errorUsername = true;
        }

        if(!(this.validateIPv4(this.textFieldIP.getText()) || this.textFieldIP.getText().isEmpty()))
        {
            disableJoinButton = true;
            errorIP = true;
        }

        if(!(this.validatePort(this.textFieldPortJoin.getText()) || this.textFieldPortJoin.getText().isEmpty()))
        {
            disableJoinButton = true;
            errorPort = true;
        }

        this.buttonJoinLobby.setDisable(disableJoinButton);
        this.labelErrorUsernameJoin.setVisible(errorUsername);
        this.textFieldUsernameJoin.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.labelErrorIP.setVisible(errorIP);
        this.textFieldIP.setStyle(errorIP ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.labelErrorPortJoin.setVisible(errorPort);
        this.textFieldPortJoin.setStyle(errorPort ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");

        return !disableJoinButton;
    }

    @FXML
    public void joinLobby(ActionEvent event)
    {
        if (!this.checkEnableJoinExistingLobby())
            return;

        System.out.println("User is trying to join an existing lobby");

        if(this.textFieldIP.getText().isEmpty())
            this.textFieldIP.setText("127.0.0.1");
        if(this.textFieldPortJoin.getText().isEmpty())
            this.textFieldPortJoin.setText("" + ServerStream.DEFAULT_PORT);

        String username = this.textFieldUsernameJoin.getText();

        this.hboxConnectionJoin.setVisible(true);

        // connect to existing room -> start client (if OK switch to Client Room View)
        ClientStream client = new ClientStream(
                username,
                this.textFieldIP.getText(),
                Integer.parseInt(this.textFieldPortJoin.getText()));

        ControllerLobbyClient ctrlLC = (ControllerLobbyClient) Navigator.getController(NavEntry.LOBBY_CLIENT);
        ctrlLC.setProperties(client, username);

        client.setConnectRefusedMessageHandler(connectRefusedHandler);
        client.setConnectAcceptedMessageHandler(connectAcceptedHandler);

        client.startClient();
    }

    public IMessageHandler connectRefusedHandler = (Message msg) -> {
        System.out.println("ControllerMultiplayer: received CONNECT_REFUSED message");

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failed");
            alert.setTitle("Error Dialog");
            alert.setContentText(msg.getContent());
            alert.show();
            this.hboxConnectionJoin.setVisible(false);
        });
    };
    public IMessageHandler connectAcceptedHandler = (Message msg) -> {
        System.out.println("ControllerMultiplayer: received CONNECT_ACCEPTED message");

        // Go to Lobby
        Platform.runLater(() -> {
            ControllerLobbyClient ctrlLC = (ControllerLobbyClient) Navigator.getController(NavEntry.LOBBY_CLIENT);
            ctrlLC.setUsersList(User.stringToUserList(msg.getContent()));

            // add chat message
            msg.setUsername(this.textFieldUsernameJoin.getText());
            ctrlLC.addJoinMessage(msg);

            Navigator.switchView(NavEntry.LOBBY_CLIENT);
        });
    };
}
