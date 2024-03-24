package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ControllerAbout implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxAbout;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;


    public void initialize()
    {
        this.vboxAbout.setVisible(true);
        this.vboxBackControls.setVisible(true);
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back");

        Navigator.switchView(NavEntry.MAIN);
    }


}
