module it.mikyll.cluedo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.google.gson;

    exports it.mikyll.cluedo.model.settings to com.google.gson;
    opens it.mikyll.cluedo.model.settings to com.google.gson;
    exports it.mikyll.cluedo.model.localization to com.google.gson;
    opens it.mikyll.cluedo.model.localization to com.google.gson;
    exports it.mikyll.cluedo.model.game.clues to com.google.gson;
    opens it.mikyll.cluedo.model.game.clues to com.google.gson;

    opens it.mikyll.cluedo.controller;

    exports it.mikyll.cluedo.application;
    opens it.mikyll.cluedo.application to javafx.fxml;

    opens it.mikyll.cluedo.controller.game to javafx.fxml;
    opens it.mikyll.cluedo.controller.navigation to javafx.fxml;
    opens it.mikyll.cluedo.controller.menu;
    exports it.mikyll.cluedo.model.game.board to com.google.gson;
    opens it.mikyll.cluedo.model.game.board to com.google.gson;
}