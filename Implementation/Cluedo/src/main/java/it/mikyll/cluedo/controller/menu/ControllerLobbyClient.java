package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.networking.ClientStream;
import it.mikyll.cluedo.model.networking.User;
import it.mikyll.cluedo.model.networking.message.IMessageHandler;
import it.mikyll.cluedo.model.networking.message.Message;
import it.mikyll.cluedo.model.networking.message.MessageType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ControllerLobbyClient implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxLobbyClient;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;

    // Lobby controls:
    @FXML private ListView<HBox> listViewUsers;
    private ArrayList<Label> listLabelUsername = new ArrayList<Label>();
    private ArrayList<Label> listLabelReady = new ArrayList<Label>();
    @FXML private TextArea textAreaChat;
    @FXML private TextField textFieldChat;
    @FXML private Button buttonChatSend;
    @FXML private Button buttonReady;
    @FXML private HBox hboxIPaddress;
    @FXML private Label labelLobbyLANaddress;

    private ClientStream client;
    private String username;

    public void initialize()
    {
        this.vboxBackControls.setVisible(true);
        this.vboxLobbyClient.setVisible(true);

        this.listViewUsers.setItems(FXCollections.observableArrayList());

        this.buttonReady.setVisible(true);
        this.buttonReady.setText(" Not ready");
        this.buttonReady.setStyle("-fx-background-color: red");

        this.clearLists();
        this.clearChat();

        // TODO
        if (this.client != null)
        {
            this.client.setUserJoinedMessageHandler(userJoinedHandler);
            this.client.setChatMessageHandler(chatHandler);
            this.client.setDisconnectMessageHandler(disconnectHandler);
            this.client.setReadyMessageHandler(readyHandler);
            this.client.setKickMessageHandler(kickHandler);
            this.client.setBanMessageHandler(banHandler);
            this.client.setGenericMessageHandler(banHandler);
        }
    }

    public void setProperties(ClientStream client, String username)
    {
        this.client = client;
        this.username = username;
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back (Multiplayer)");

        this.client.sendClose();

        Navigator.switchView(NavEntry.MULTIPLAYER);
    }

    @FXML public void toggleReady(ActionEvent event)
    {
        boolean ready = this.buttonReady.getText().trim().equalsIgnoreCase("Ready");
        ready = !ready;

        this.buttonReady.setText(ready ? "Ready" : "Not ready");
        this.buttonReady.setStyle("-fx-background-color: " + (ready ? "lime" : "red"));

        this.setReady(this.username, ready);

        this.client.sendReady(ready);

        // TO-DO: set a 5 sec timer that disables the button, so that users can't spam the toggle
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
            this.client.sendChatMessage(this.textFieldChat.getText());
            this.addChatMessage(new Message(MessageType.CHAT, this.username, this.textFieldChat.getText()));

            this.textFieldChat.clear();
        }

    }

    // Utilities ==============================================================
    public void closeConnection()
    {
        // TODO
        if(this.client != null)
        {
            this.client.sendClose();
            this.client = null;
        }
    }

    // it's called only by client, when it receives a user_list
    protected void setUsers(List<User> users)
    {
        this.clearLists();

        if(this.client != null)
        {
            this.addUser(users.get(0).getUsername(), true, true);
            for(int i = 1; i < users.size(); i++)
            {
                this.addUser(users.get(i).getUsername(), false, users.get(i).isReady());
            }
        }
    }

    private void addUser(String username, boolean isHost, boolean isReady)
    {
        HBox el = this.buildUserClientElement(username, isHost, isReady);

        this.listViewUsers.getItems().add(el);

        this.listLabelUsername.add((Label) el.getChildren().get(0));
        this.listLabelReady.add((Label) el.getChildren().get(1));

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
    }

    private void addChatMessage(Message message)
    {
        this.textAreaChat.appendText((this.textAreaChat.getText().isEmpty() ? "" : "\n") +
                message.getTimestamp() + " " + message.getUsername() + ": " + message.getContent());
    }
    protected void addJoinMessage(Message message)
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

    // close Lobby Settings

    // Component builders

    private HBox buildUserClientElement(String username, boolean isHost, boolean isReady)
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


        result.getChildren().addAll(lUsername, lReady);

        return result;
    }

    // Handle Lobby Messages ==================================================

    public IMessageHandler userJoinedHandler = (Message msg) -> {
        System.out.println("ControllerLobbyClient: received USER_JOINED message");

        Platform.runLater(() -> {
            // add to user list
            this.addUser(msg.getUsername(), false, false);

            // add chat message
            this.addJoinMessage(msg);
        });
    };
    public IMessageHandler chatHandler = (Message msg) -> {
        System.out.println("ControllerLobbyClient: received CHAT message");

        Platform.runLater(() -> {
            this.addChatMessage(msg);
        });
    };
    public IMessageHandler disconnectHandler = (Message msg) -> {
        System.out.println("ControllerLobbyClient: received DISCONNECT message");

        Platform.runLater(() -> {
            if (msg.getUsername().equals(this.username)) {
                this.selectBack(new ActionEvent());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Disconnected from server");
                alert.setTitle("Error Dialog");
                alert.setContentText(msg.getContent());
                alert.show();
            } else {
                this.addLeaveMessage(msg);
                this.removeUser(msg.getUsername());
            }
        });
    };
    public IMessageHandler readyHandler = (Message msg) -> {
        System.out.println("ControllerLobbyClient: received READY message");

        this.setReady(msg.getUsername(), Boolean.parseBoolean(msg.getContent()));
    };
    public IMessageHandler kickHandler = (Message msg) -> {
        System.out.println("ControllerLobbyClient: received KICK message");

        Platform.runLater(() -> {
            if (msg.getUsername().equals(this.username)) {
                this.selectBack(new ActionEvent());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Disconnected from server");
                alert.setTitle("Error Dialog");
                alert.setContentText(msg.getContent());
                alert.show();
            } else {
                this.removeUser(msg.getUsername());

                this.addKickMessage(msg);
            }
        });
    };
    public IMessageHandler banHandler = (Message msg) -> {
        System.out.println("ControllerLobbyClient: received BAN message");

        Platform.runLater(() -> {
            if (msg.getUsername().equals(this.username)) {
                this.selectBack(new ActionEvent());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Disconnected from server");
                alert.setTitle("Error Dialog");
                alert.setContentText(msg.getContent());
                alert.show();
            } else {
                this.removeUser(msg.getUsername());

                this.addBanMessage(msg);
            }
        });
    };
    public IMessageHandler genericHandler = (Message msg) -> {
        System.out.println("ControllerLobbyClient: received " + msg.getMsgType() + " message");
    };

    private void setServerAddress()
    {
        try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String privateIP = socket.getLocalAddress().getHostAddress();
            this.labelLobbyLANaddress.setText(privateIP);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void clearLists()
    {
        this.listViewUsers.getItems().clear();

        this.listLabelUsername.clear();
        this.listLabelReady.clear();
    }

    private void clearChat()
    {
        this.textAreaChat.setText("");
        this.textFieldChat.setText("");
        this.textFieldChat.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
        this.buttonChatSend.setDisable(false);
    }
}
