package it.mikyll.cluedo.view.gui.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CenteredAlert extends Alert {

    public CenteredAlert(Window window, AlertType alertType, String title, String content) {
        super(alertType);

        this.setTitle(title);
        this.setContentText(content);
        centerAlert(window, this);
    }

    public CenteredAlert(Window window, AlertType alertType, String title, String content, ButtonType... buttonTypes) {
        super(alertType, content, buttonTypes);

        this.setTitle(title);
        centerAlert(window, this);
    }

    public CenteredAlert(Window window, AlertType alertType, String s, ButtonType... buttonTypes) {
        super(alertType, s, buttonTypes);

        centerAlert(window, this);
    }

    public CenteredAlert(Window window, AlertType alertType) {
        super(alertType);

        centerAlert(window, this);
    }

    private void centerAlert(Window window, Alert alert) {
        Stage parentStage = (Stage) window;
        this.initOwner(parentStage);
        this.setOnShown(e -> {
            // Get the dimensions of the primary stage (main window)
            double primaryStageX = parentStage.getX();
            double primaryStageY = parentStage.getY();
            double primaryStageWidth = parentStage.getWidth();
            double primaryStageHeight = parentStage.getHeight();

            // Get the dimensions of the alert window
            Stage alertStage = (Stage) this.getDialogPane().getScene().getWindow();
            double alertWidth = alertStage.getWidth();
            double alertHeight = alertStage.getHeight();

            // Calculate the position for the alert to be centered on the main window
            double alertX = primaryStageX + (primaryStageWidth - alertWidth) / 2;
            double alertY = primaryStageY + (primaryStageHeight - alertHeight) / 2;

            // Set the position of the alert
            alertStage.setX(alertX);
            alertStage.setY(alertY);
        });
    }
}
