package it.mikyll.cluedo.view.gui.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CenteredAlert extends Alert {
    private final Window window;

    public CenteredAlert(Window window, AlertType alertType, String title, String content) {
        super(alertType);

        this.window = window;
        this.setTitle(title);
        this.setContentText(content);
        centerAlert();
    }

    public CenteredAlert(Window window, AlertType alertType, String title, String content, ButtonType... buttonTypes) {
        super(alertType, content, buttonTypes);

        this.window = window;
        this.setTitle(title);
        centerAlert();
    }

    public void centerAlert() {
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
            alertStage.setAlwaysOnTop(true);

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
