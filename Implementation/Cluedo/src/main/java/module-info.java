module it.mikyll.cluedo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens it.mikyll.cluedo.controller;

    exports it.mikyll.cluedo.application;
    opens it.mikyll.cluedo.application to javafx.fxml;
}