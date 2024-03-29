package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ControllerAbout implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxAbout;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;

    @FXML private ImageView imageViewAuthorPicture;

    public void initialize()
    {
        this.vboxAbout.setVisible(true);
        this.vboxBackControls.setVisible(true);

        double radiusP = imageViewAuthorPicture.getFitWidth() / 2;
        imageViewAuthorPicture.setClip(new Circle(radiusP, radiusP, radiusP));
    }

    public void start()
    {
        System.out.println("User selected About");
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back");

        Navigator.switchView(NavEntry.MAIN);
    }


}
