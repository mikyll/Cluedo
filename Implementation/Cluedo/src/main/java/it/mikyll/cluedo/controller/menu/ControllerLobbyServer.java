package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.networking.ServerStream;
import it.mikyll.cluedo.model.networking.User;
import it.mikyll.cluedo.model.networking.message.IMessageHandler;
import it.mikyll.cluedo.model.networking.message.Message;
import it.mikyll.cluedo.model.networking.message.MessageType;
import it.mikyll.cluedo.model.settings.Settings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.UncheckedIOException;
import java.net.*;
import java.util.ArrayList;

public class ControllerLobbyServer implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxBackControls;
    @FXML private VBox vboxLobbyServer;
    @FXML private VBox vboxLobbySettingsControls;
    @FXML private VBox vboxLobbySettings;

    @FXML private Button buttonBack;

    // Lobby controls:
    @FXML private VBox vboxChat;

    @FXML private ListView<HBox> listViewUsers;
    private ArrayList<Label> listLabelUsername = new ArrayList<Label>();
    private ArrayList<Label> listLabelReady = new ArrayList<Label>();
    private ArrayList<Button> listButtonKick = new ArrayList<Button>();
    private ArrayList<Button> listButtonBan = new ArrayList<Button>();
    @FXML private TextArea textAreaChat;
    @FXML private TextField textFieldChat;
    @FXML private Button buttonChatSend;
    @FXML private Button buttonStartGame;
    @FXML private HBox hboxIPaddress;
    @FXML private Label labelLobbyLANaddress;

    // Lobby bottom-right controls:
    @FXML private Button buttonLobbySettings;

    // Lobby Settings controls:
    @FXML private Button buttonCloseLobbySettings;
    @FXML private ListView<HBox> listViewBannedUsers;
    @FXML private TextField textFieldBanUsername;
    @FXML private TextField textFieldBanAddress;
    @FXML private Button buttonBan;
    @FXML private Label labelLobbyPrivacy;
    @FXML private Button buttonLobbyPrivacy;

    private final Settings settings;
    private ServerStream server;
    private String username;

    public ControllerLobbyServer()
    {
        this.settings = Settings.getInstance();
    }

    public void initialize()
    {
        this.listViewUsers.setItems(FXCollections.observableArrayList());
        this.listViewBannedUsers.setItems(FXCollections.observableArrayList());

        // TODO
        /*Platform.runLater(() -> {
            this.vboxMainMenu.getScene().setOnKeyPressed(e -> {
                if(e.getCode() == KeyCode.ESCAPE)
                    this.selectBack(new ActionEvent());
            });
        });*/
    }

    public void start()
    {
        this.vboxBackControls.setVisible(true);
        this.vboxLobbyServer.setVisible(true);
        this.vboxLobbySettingsControls.setVisible(true);
        this.buttonLobbySettings.setDisable(false);

        this.clearLists();
        this.addUser(this.username, true, true);

        this.clearChat();
        this.vboxChat.setVisible(this.settings.isChatEnabled());
        this.addJoinMessage(new Message(MessageType.CHAT, this.username, ""));

        this.labelLobbyPrivacy.setId("privacyOpen");
        this.buttonLobbyPrivacy.setText("Open");

        this.buttonStartGame.setDisable(true);

        // Server setup
        this.server.setConnectRequestMessageHandler(connectRequestHandler);
        this.server.setChatMessageHandler(chatHandler);
        this.server.setReadyMessageHandler(readyHandler);
        this.server.setDisconnectMessageHandler(disconnectHandler);
        this.server.setGenericMessageHandler(genericHandler);

        this.server.startServer();

        if (isInternetConnectionAvailable())
        {
            this.setServerAddress(getPrivateIP());
        }
    }

    protected void setProperties(ServerStream server, String username)
    {
        this.server = server;
        this.username = username;
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back (Multiplayer)");

        this.server.sendClose();

        Navigator.switchView(NavEntry.MULTIPLAYER);
    }

    // Lobby functions ========================================================
    @FXML public void selectLobbySettings(ActionEvent event)
    {
        System.out.println("User selected Lobby Settings");

        this.buttonBack.setDisable(true);
        this.buttonLobbySettings.setDisable(true);

        this.textFieldChat.setDisable(true);
        this.buttonChatSend.setDisable(true);
        this.buttonStartGame.setDisable(true);

        this.textFieldBanUsername.setText("");
        this.textFieldBanUsername.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.textFieldBanAddress.setText("");
        this.textFieldBanAddress.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.buttonBan.setDisable(true);

        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(15.0);
        this.vboxLobbyServer.setEffect(blur);

        this.vboxLobbySettings.setVisible(true);
    }

    @FXML public void closeLobbySettings(ActionEvent event)
    {
        System.out.println("User closed Lobby Settings");

        this.vboxLobbySettings.setVisible(false);

        this.vboxLobbyServer.setEffect(null);

        this.textFieldChat.setDisable(false);
        this.buttonChatSend.setDisable(false);
        this.buttonBack.setDisable(false);
        this.buttonLobbySettings.setDisable(false);
        this.buttonStartGame.setDisable(this.server.canStart());
    }

    @FXML public void ban(ActionEvent event)
    {
        if (!this.checkEnableBanUser())
            return;

        this.banUser(this.textFieldBanUsername.getText(), this.textFieldBanAddress.getText());
    }
    @FXML public void toggleLobbyPrivacy(ActionEvent event)
    {
        if(this.buttonLobbyPrivacy.getText().contains("Open"))
        {
            this.labelLobbyPrivacy.setId("privacyClosed");
            this.buttonLobbyPrivacy.setText("Closed");

            this.server.setPrivacy(false);
        }
        else
        {
            this.labelLobbyPrivacy.setId("privacyOpen");
            this.buttonLobbyPrivacy.setText(" Open");

            this.server.setPrivacy(true);
        }
    }

    @FXML public void checkEnableSendChat(KeyEvent event)
    {
        if(this.textFieldChat.getText().length() > 100)
        {
            this.textFieldChat.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            this.buttonChatSend.setDisable(true);
        }
        else
        {
            this.textFieldChat.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
            this.buttonChatSend.setDisable(false);
        }
    }

    @FXML public void sendChatMessage(ActionEvent event)
    {
        if(!this.textFieldChat.getText().isEmpty() && this.textFieldChat.getText().length() <= 100)
        {
            this.server.sendChatMessage(this.textFieldChat.getText());
            this.addChatMessage(new Message(MessageType.CHAT, this.username, this.textFieldChat.getText()));

            this.textFieldChat.clear();
        }

    }

    // Start Game
    @FXML public void startMultiplayerGame(ActionEvent event)
    {
        System.out.println("User started the game");

        // TODO
    }

    // Lobby Settings =========================================================
    @FXML private boolean checkEnableBanUser()
    {
        boolean disableBanButton = false, errorUsername = false, errorIP = false;

        if(this.textFieldBanUsername.getText().isEmpty() &&
                this.textFieldBanAddress.getText().isEmpty())
        {
            disableBanButton = true;
        }
        if(!User.validateUsername(this.textFieldBanUsername.getText()))
        {
            errorUsername = true;
        }
        if(!User.validateIPv4(this.textFieldBanAddress.getText()))
        {
            errorIP = true;
            if(errorUsername)
                disableBanButton = true;
        }
        if(User.validateUsername(this.textFieldBanUsername.getText()))
        {
            if(!User.validateIPv4(this.textFieldBanAddress.getText()) &&
                    !this.textFieldBanAddress.getText().isEmpty())
                disableBanButton = true;
        }
        if(User.validateIPv4(this.textFieldBanAddress.getText()))
        {
            if(!User.validateUsername(this.textFieldBanUsername.getText()) &&
                    !this.textFieldBanUsername.getText().isEmpty())
                disableBanButton = true;
        }

        // You cannot ban yourself
        if(this.textFieldBanUsername.getText().equals(this.username))
        {
            disableBanButton = true;
            errorUsername = true;
        }

        this.buttonBan.setDisable(disableBanButton);
        this.textFieldBanUsername.setStyle(errorUsername ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.textFieldBanAddress.setStyle(errorIP ? "-fx-text-box-border: red; -fx-focus-color: red;" : "-fx-border-width: 0px; -fx-focus-color: #039ED3;");

        return !disableBanButton;
    }

    // Utilities ==============================================================
    public void closeConnection()
    {
        // TODO
        if(this.server != null)
        {
            this.server.sendClose();
            this.server = null;
        }
    }

    private void addUser(String username, boolean isHost, boolean isReady)
    {
        HBox el = this.buildUserServerElement(username, isHost, isReady);

        this.listViewUsers.getItems().add(el);

        this.listLabelUsername.add((Label) el.getChildren().get(0));
        this.listLabelReady.add((Label) el.getChildren().get(1));
        this.listButtonKick.add((Button) el.getChildren().get(2));
        this.listButtonBan.add((Button) el.getChildren().get(3));

        // Highlight user
        Label l = this.listLabelUsername.get(this.listLabelUsername.size() - 1);
        if(l.getText().equals(this.username))
            l.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14.0));
    }

    private void removeUser(String username)
    {
        for(int i = 0; i < this.listLabelUsername.size(); i++)
        {
            if(this.listLabelUsername.get(i).getText().equals(username))
            {
                this.listLabelUsername.remove(i);
                this.listLabelReady.remove(i);
                this.listButtonKick.remove(i);
                this.listButtonBan.remove(i);

                this.listViewUsers.getItems().remove(i);
            }
        }
    }

    private void setReady(String username, boolean ready)
    {
        for(int i = 1; i < this.listLabelUsername.size(); i++)
        {
            if(this.listLabelUsername.get(i).getText().equals(username))
            {
                this.listLabelReady.get(i).setStyle("-fx-background-radius: 30; -fx-background-color: " + (ready ? "lime" : "red"));
            }
        }

        if(server != null)
        {
            // Check if can enable START button
            this.buttonStartGame.setDisable(!server.canStart());
        }
    }

    private void kickUser(String username)
    {
        this.addKickMessage(new Message(MessageType.KICK, username, ""));

        this.removeUser(username);

        this.server.sendKickUser(username);

        this.buttonStartGame.setDisable(!this.server.canStart());
    }

    private void banUser(String username, String address)
    {
        if(username == null && address == null)
            return;


        if (this.isPresentInBanList(username, address))
            return;

        if(username != null)
        {
            this.addBanMessage(new Message(MessageType.BAN, username, ""));

            this.removeUser(username);
        }
        else
        {
            try {
                String sUsername = this.server.getUsernameFromAddress(InetAddress.getByName(address));

                this.removeUser(sUsername);
            } catch (UnknownHostException e) {System.out.println("Address not found");}
        }

        // Add to banned list (LobbySettings)
        this.listViewBannedUsers.getItems().add(this.buildBannedUserElement(username, address));

        this.server.sendBanUser(username, address);

        this.buttonStartGame.setDisable(!this.server.canStart());
    }

    private boolean isPresentInBanList(String username, String address)
    {
        for (int i = 0; i < this.listViewBannedUsers.getItems().size(); i++)
        {
            HBox hbox = this.listViewBannedUsers.getItems().get(i);
            Label lUsername = (Label) hbox.getChildren().get(0);
            Label lAddress = (Label) hbox.getChildren().get(1);
            if (username.equals(lUsername.getText()) && address.equals(lAddress.getText()))
                return true;
        }
        return false;
    }

    private void removeFromBanList(HBox row, String username, String address)
    {
        this.listViewBannedUsers.getItems().remove(row);

        if(username != null)
        {
            this.addRevokeBanMessage(new Message(MessageType.BAN, username, ""));
        }

        this.server.removeBan(username, address);
    }

    private void addChatMessage(Message message)
    {
        this.textAreaChat.appendText((this.textAreaChat.getText().isEmpty() ? "" : "\n") +
                message.getTimestamp() + " " + message.getUsername() + ": " + message.getContent());
    }
    private void addJoinMessage(Message message)
    {
        this.textAreaChat.appendText((this.textAreaChat.getText().isEmpty() ? "" : "\n") +
                message.getTimestamp() + " User '" + message.getUsername() + "' has joined the lobby");
    }
    private void addLeaveMessage(Message message)
    {
        this.textAreaChat.appendText((this.textAreaChat.getText().isEmpty() ? "" : "\n") +
                message.getTimestamp() + " User '" + message.getUsername() + "' has left the lobby");
    }
    private void addKickMessage(Message message)
    {
        this.textAreaChat.appendText((this.textAreaChat.getText().isEmpty() ? "" : "\n") +
                message.getTimestamp() + " User '" + message.getUsername() + "' has been kicked out");
    }
    private void addBanMessage(Message message)
    {
        this.textAreaChat.appendText((this.textAreaChat.getText().isEmpty() ? "" : "\n") +
                message.getTimestamp() + " User '" + message.getUsername() + "' has been banned from the lobby");
    }
    private void addRevokeBanMessage(Message message)
    {
        this.textAreaChat.appendText((this.textAreaChat.getText().isEmpty() ? "" : "\n") +
                message.getTimestamp() + " User '" + message.getUsername() + "' is no longer banned from the lobby");
    }
    // private addChatMessage()

    // close Lobby Settings

    // Component builders

    private HBox buildUserServerElement(String username, boolean isHost, boolean isReady)
    {
        HBox result = new HBox();
        result.setPrefHeight(30.0);
        result.setAlignment(Pos.CENTER_LEFT);
        result.setSpacing(5.0);

        Label lUsername = new Label(username);
        lUsername.setPrefWidth(200.0);
        lUsername.setFont(Font.font("System", 14));
        lUsername.setTextFill(Color.WHITE);

        Label lReady = new Label();
        lReady.setPrefSize(25.0, 25.0);
        if(isHost)
        {
            lReady.setId("host");
            Tooltip tR = new Tooltip("User '" + username + "' is the host");
            tR.setFont(Font.font(14.0));
            lReady.setTooltip(tR);
        }
        else lReady.setStyle("-fx-background-radius: 30; -fx-background-color: " + (isReady ? "lime" : "red"));

        Button bKick = new Button();
        bKick.setPrefSize(25.0, 25.0);
        bKick.setId("kick");
        // add callback
        Tooltip tK = new Tooltip("Kick user '" + username + "'");
        tK.setFont(Font.font(14.0));
        bKick.setTooltip(tK);
        bKick.setOnAction(e -> {
            this.kickUser(username);
        });

        Button bBan = new Button();
        bBan.setPrefSize(25.0, 25.0);
        bBan.setId("ban");
        // add callback
        Tooltip tB = new Tooltip("Ban user '" + username + "'");
        tB.setFont(Font.font(14.0));
        bBan.setTooltip(tB);
        bBan.setOnAction((ActionEvent e) -> {
            this.banUser(username, this.server.getAddressFromUsername(username).toString());
        });

        bKick.setVisible(!isHost);
        bBan.setVisible(!isHost);

        // TO-DO: highlight

        result.getChildren().addAll(lUsername, lReady, bKick, bBan);

        return result;
    }

    private HBox buildBannedUserElement(String username, String address)
    {
        HBox result = new HBox();
        result.setPrefWidth(390.0);
        result.setAlignment(Pos.CENTER_LEFT);
        result.setSpacing(5.0);

        Label lUsername = new Label(username == null ? "-" : username);
        lUsername.setPrefWidth(150.0);
        lUsername.setFont(Font.font("System", 14));
        lUsername.setTextFill(Color.WHITE);

        Label lAddress = new Label(address == null ? "-" : address);
        lAddress.setPrefWidth(150.0);
        lAddress.setFont(Font.font("System", 14));
        lAddress.setTextFill(Color.WHITE);

        Button bRevokeBan = new Button();
        bRevokeBan.setPrefSize(25.0, 25.0);
        bRevokeBan.setId("revokeBan");
        // add callback
        Tooltip tRB = new Tooltip("Revoke ban");
        tRB.setFont(Font.font(14.0));
        bRevokeBan.setTooltip(tRB);
        bRevokeBan.setOnAction((ActionEvent e) -> {
            this.removeFromBanList(result, username, address);
        });

        result.getChildren().addAll(lUsername, lAddress, bRevokeBan);

        return result;
    }

    // Message Handlers =======================================================

    public IMessageHandler connectRequestHandler = (Message msg) -> {
        System.out.println("ControllerLobbyServer: received CONNECT_REQUEST message");

        Platform.runLater(() -> {
            // add user to list
            this.addUser(msg.getUsername(), false, false);
            this.buttonStartGame.setDisable(true);

            // add chat message
            this.addJoinMessage(msg);
        });
    };
    public IMessageHandler chatHandler = (Message msg) -> {
        System.out.println("ControllerLobbyServer: received CHAT message");

        Platform.runLater(() -> {
            this.addChatMessage(msg);
        });
    };
    public IMessageHandler readyHandler = (Message msg) -> {
        System.out.println("ControllerLobbyServer: received READY message");

        Platform.runLater(() -> {
            this.setReady(msg.getUsername(), Boolean.parseBoolean(msg.getContent()));

            this.buttonStartGame.setDisable(!this.server.canStart());
        });
    };
    public IMessageHandler disconnectHandler = (Message msg) -> {
        System.out.println("ControllerLobbyServer: received DISCONNECT message");

        Platform.runLater(() -> {
            this.addLeaveMessage(msg);

            this.removeUser(msg.getUsername());
        });
    };
    public IMessageHandler genericHandler = (Message msg) -> {
        System.out.println("ControllerLobbyServer: received " + msg.getMsgType() + " message");
    };

    static public boolean isInternetConnectionAvailable()
    {
        boolean result = false;

        try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);

            result = true;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (UncheckedIOException e) {
            System.out.println("No Internet connection");
            e.printStackTrace();
        }

        return result;
    }

    private String getPrivateIP()
    {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    private void setServerAddress(String ip)
    {
        String address = ip;
        if (this.server != null) {
            address += " : " + this.server.getPort();
        }
        this.labelLobbyLANaddress.setText(address);
    }

    private void clearLists()
    {
        this.listViewUsers.getItems().clear();
        this.listViewBannedUsers.getItems().clear();

        this.listLabelUsername.clear();
        this.listLabelReady.clear();
        this.listButtonKick.clear();
        this.listButtonBan.clear();
    }

    private void clearChat()
    {
        this.textAreaChat.setText("");
        this.textFieldChat.setText("");
        this.textFieldChat.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.buttonChatSend.setDisable(false);
    }
}
