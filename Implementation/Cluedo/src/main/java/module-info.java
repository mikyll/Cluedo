module it.mikyll.cluedo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens it.mikyll.cluedo.controller;

    exports it.mikyll.cluedo.application;
    opens it.mikyll.cluedo.application to javafx.fxml;

    // test
    opens it.mikyll.cluedo.controller.menu to javafx.fxml;
    opens it.mikyll.cluedo.controller.navigation to javafx.fxml;
}