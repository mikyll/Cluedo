package it.mikyll.cluedo.controller.menu;

import it.mikyll.cluedo.controller.navigation.IController;
import it.mikyll.cluedo.controller.navigation.NavEntry;
import it.mikyll.cluedo.controller.navigation.Navigator;
import it.mikyll.cluedo.model.settings.Settings;
import it.mikyll.cluedo.model.sounds.MusicPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ControllerRulesHelp implements IController {
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private VBox vboxBackControls;
    @FXML private VBox vboxRulesHelp;

    @FXML private Button buttonBack;

    @FXML private Button buttonPreviousPage;
    @FXML private Button buttonNextPage;
    @FXML private ImageView imageViewRules;
    @FXML private Label labelPageNumber;

    private List<Image> rulePages;
    private int iPage;

    public ControllerRulesHelp()
    {
        rulePages = new ArrayList<>();

        for (int i = 1; i <= 8; i++)
        {
            Image image = new Image(ControllerRulesHelp.class.getResource(Settings.RESOURCES_PATH + "images/rules/en/page" + i + ".png").toString());
            rulePages.add(image);
        }
    }

    public void initialize()
    {
        this.vboxBackControls.setVisible(true);
        this.vboxRulesHelp.setVisible(true);

        this.iPage = 0;
        setRulePageImage(this.iPage);
        this.buttonPreviousPage.setDisable(true);
        this.buttonNextPage.setDisable(false);
    }

    @FXML
    public void selectBack(ActionEvent event)
    {
        System.out.println("User selected Back (Main)");

        Navigator.switchView(NavEntry.MAIN);
    }

    @FXML
    public void selectPreviousPage(ActionEvent event)
    {
        if (this.iPage <= 0)
            return;

        this.buttonNextPage.setDisable(false);
        this.iPage--;
        setRulePageImage(this.iPage);

        if (this.iPage <= 0)
            this.buttonPreviousPage.setDisable(true);
    }

    @FXML
    public void selectNextPage(ActionEvent event)
    {
        if (this.iPage >= this.rulePages.size() - 1)
            return;

        this.buttonPreviousPage.setDisable(false);

        this.iPage++;
        setRulePageImage(this.iPage);

        if (this.iPage >= this.rulePages.size() - 1)
            this.buttonNextPage.setDisable(true);
    }

    private void setRulePageImage(int index)
    {
        this.imageViewRules.setImage(this.rulePages.get(index));
        this.labelPageNumber.setText((index + 1) + "/" + this.rulePages.size());
    }
}
