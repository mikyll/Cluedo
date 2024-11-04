package it.mikyll.cluedo.view.gui.javafx;

import it.mikyll.cluedo.model.game.clues.Clue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ClueSheet extends VBox {
    private ObservableList<HBox> list;

    /*
    - 3 colonne
    - ciascuna composta da una lista di clues (checkbox + label)
     */
    public ClueSheet(List<Clue> clues) {
        HBox hboxClueElement;

        this.list = FXCollections.observableArrayList();
    }

    public void initClues() {

    }
}
