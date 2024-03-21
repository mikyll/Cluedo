module it.unibo.is20.g22.cluedo {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.unibo.is20.g22.cluedo to javafx.fxml;
    exports it.unibo.is20.g22.cluedo;
}