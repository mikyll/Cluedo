package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.settings.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ControllerAbout implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxAbout;
    @FXML private VBox vboxBackControls;

    @FXML private Button buttonBack;

    @FXML private VBox vboxDescription;
    @FXML private Label labelAppName;
    @FXML private Label labelAppVersion;
    @FXML private Text textAppName;
    @FXML private ImageView imageViewContributor1;
    @FXML private ImageView imageViewContributor2;
    @FXML private ImageView imageViewContributor3;

    @FXML private VBox vboxLicensePlaceholder;
    @FXML private VBox vboxLicense;
    @FXML private TextArea textAreaLicense;

    @FXML private VBox vboxCreditsPlaceholder;
    @FXML private VBox vboxCredits;
    @FXML private ListView<HBox> listViewCredits;

    private String license= "";

    public ControllerAbout()
    {
        // Load license from file
        try (InputStream is = ControllerAbout.class.getResourceAsStream(Settings.RESOURCES_PATH + "docs/LICENSE");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            license = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (NullPointerException | IOException e) {
            System.out.println("Error loading license: " + e.getMessage());
        }
    }

    public void initialize()
    {
        this.vboxAbout.setVisible(true);
        this.vboxBackControls.setVisible(true);

        this.vboxLicensePlaceholder.setVisible(false);
        this.vboxLicensePlaceholder.getChildren().clear();
        this.vboxCreditsPlaceholder.setVisible(false);
        this.vboxCreditsPlaceholder.getChildren().clear();

        labelAppName.setText(Settings.APP_TITLE);
        labelAppVersion.setText(Settings.APP_VERSION);
        textAppName.setText(Settings.APP_TITLE);
        textAreaLicense.setText(license);

        showDescriptionPanel();

        double radiusP = imageViewContributor1.getFitWidth() / 2;
        imageViewContributor1.setClip(new Circle(radiusP, radiusP, radiusP));
        imageViewContributor2.setClip(new Circle(radiusP, radiusP, radiusP));
        imageViewContributor3.setClip(new Circle(radiusP, radiusP, radiusP));
    }

    public void start()
    {
        System.out.println("User selected About");

        showDescriptionPanel();
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back");

        if (vboxDescription.isVisible())
            Navigator.switchView(NavEntry.MAIN);
        else if (vboxLicense.isVisible() || vboxCredits.isVisible())
            showDescriptionPanel();
    }

    private void showDescriptionPanel()
    {
        vboxLicense.setVisible(false);
        vboxCredits.setVisible(false);
        vboxDescription.setVisible(true);
        this.vboxAbout.getChildren().setAll(labelAppName, labelAppVersion, vboxDescription);
    }

    private void showLicensePanel()
    {
        vboxDescription.setVisible(false);
        vboxCredits.setVisible(false);
        vboxLicense.setVisible(true);
        this.vboxAbout.getChildren().setAll(labelAppName, labelAppVersion, vboxLicense);
    }

    private void showCreditsPanel()
    {
        vboxDescription.setVisible(false);
        vboxLicense.setVisible(false);
        vboxCredits.setVisible(true);
        this.vboxAbout.getChildren().setAll(labelAppName, labelAppVersion, vboxLicense);
    }

    @FXML
    public void openCluedo(MouseEvent event)
    {
        Navigator.openURL("https://en.wikipedia.org/wiki/Cluedo");
    }
    @FXML
    public void openLicense(MouseEvent event)
    {
        if (license.isEmpty())
            Navigator.openURL("https://github.com/mikyll/ROQuiz/blob/main/LICENSE");
        else showLicensePanel();
    }
    @FXML
    public void openSoftwareEngineering(MouseEvent event)
    {
        Navigator.openURL("https://www.unibo.it/en/study/phd-professional-masters-specialisation-schools-and-other-programmes/course-unit-catalogue/course-unit/2020/434708");
        // ITA
        //openURL("https://www.unibo.it/it/studiare/dottorati-master-specializzazioni-e-altra-formazione/insegnamenti/insegnamento/2020/434708");
    }
    @FXML
    public void openAlmaMater(MouseEvent event)
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

    private HBox buildCreditListViewElement(String entry, String author, String url)
    {
        HBox hboxResult = new HBox();

        return hboxResult;
    }
}
