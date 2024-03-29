package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ControllerAbout implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxAbout;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;

    @FXML private Label labelAppName;
    @FXML private Label labelAppVersion;

    @FXML private ImageView imageViewContributor1;
    @FXML private ImageView imageViewContributor2;
    @FXML private ImageView imageViewContributor3;

    public void initialize()
    {
        this.vboxAbout.setVisible(true);
        this.vboxBackControls.setVisible(true);

        double radiusP = imageViewContributor1.getFitWidth() / 2;
        imageViewContributor1.setClip(new Circle(radiusP, radiusP, radiusP));
        imageViewContributor2.setClip(new Circle(radiusP, radiusP, radiusP));
        imageViewContributor3.setClip(new Circle(radiusP, radiusP, radiusP));
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

    @FXML
    public void openSoftwareEngineering(ActionEvent event)
    {
        Navigator.openURL("https://www.unibo.it/en/study/phd-professional-masters-specialisation-schools-and-other-programmes/course-unit-catalogue/course-unit/2020/434708");
        // ITA
        //openURL("https://www.unibo.it/it/studiare/dottorati-master-specializzazioni-e-altra-formazione/insegnamenti/insegnamento/2020/434708");
    }
    @FXML
    public void openAlmaMater(ActionEvent event)
    {
        Navigator.openURL("https://www.unibo.it/en/");
        //openURL("https://www.unibo.it/it");
    }

    @FXML
    public void openGitHubContributor1(MouseEvent event)
    {
        Navigator.openURL("https://github.com/mikyll");
    }
    @FXML
    public void openGitHubContributor2(MouseEvent event)
    {
        Navigator.openURL("https://github.com/TankyThunderpaw");
    }
    @FXML
    public void openGitHubContributor3(MouseEvent event)
    {
        Navigator.openURL("https://github.com/enricosarneri");
    }

    @FXML
    public void openLinkedinContributor1(MouseEvent event)
    {
        Navigator.openURL("https://www.linkedin.com/in/michele-righi/");
    }
    @FXML
    public void openLinkedinContributor2(MouseEvent event)
    {
        Navigator.openURL("https://www.linkedin.com/in/lorenzo-righi-5b4468151/");
    }
    @FXML
    public void openLinkedinContributor3(MouseEvent event)
    {
        Navigator.openURL("https://www.linkedin.com/in/enricosarneri/");
    }

    @FXML
    public void openStackoverflowContributor1(MouseEvent event)
    {
        Navigator.openURL("https://stackoverflow.com/users/19544859/mikyll98");
    }
}
