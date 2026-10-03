package it.mikyll.cluedo.view.gui.javafx;

import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.ClueType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Clue sheet of a player: a checkbox for each clue, grouped by type (characters, weapons, rooms).
 * The player ticks the clues that can't be in the murder envelope.
 */
public class ClueSheet extends VBox {
    private final List<Clue> clues;
    private final Map<Clue, CheckBox> checkBoxes;

    public ClueSheet(List<Clue> clues) {
        this.clues = clues;
        this.checkBoxes = new LinkedHashMap<>();
        this.setSpacing(3.0);

        initClues();
    }

    public void initClues() {
        this.getChildren().clear();
        this.checkBoxes.clear();

        addSection("Who?", ClueType.CHARACTER);
        addSection("What?", ClueType.WEAPON);
        addSection("Where?", ClueType.ROOM);
    }

    private void addSection(String title, ClueType type) {
        Label labelTitle = new Label(title);
        labelTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        this.getChildren().add(labelTitle);

        for (Clue clue : clues) {
            if (clue.getType().equals(type)) {
                CheckBox checkBox = new CheckBox(clue.getName());
                this.checkBoxes.put(clue, checkBox);
                this.getChildren().add(checkBox);
            }
        }
    }

    public void setChecked(Clue clue, boolean value) {
        CheckBox checkBox = checkBoxes.get(clue);
        if (checkBox != null)
            checkBox.setSelected(value);
    }
}
