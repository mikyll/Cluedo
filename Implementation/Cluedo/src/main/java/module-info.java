module it.mikyll.cluedo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens it.mikyll.cluedo.controller;

    opens it.mikyll.cluedo to javafx.fxml;
    exports it.mikyll.cluedo;
    exports it.mikyll.cluedo.application;
    opens it.mikyll.cluedo.application to javafx.fxml;
}