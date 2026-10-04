package it.mikyll.cluedo.view.gui.javafx;

import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.board.CellType;
import it.mikyll.cluedo.model.game.board.Door;
import it.mikyll.cluedo.model.game.board.StartingPoint;
import it.mikyll.cluedo.model.game.board.Trapdoor;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.player.Player;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/*
 * The board, drawn cell by cell from board.json: corridors, rooms with their names, doors, starting squares
 * and trapdoors. Pawns and reachable cells are drawn on top. It fills the available space, keeping cells square.
 */
public class BoardView extends Pane {
    private static final Color COLOR_CORRIDOR = Color.rgb(255, 216, 0);
    private static final Color COLOR_CORRIDOR_BORDER = Color.rgb(178, 148, 0);
    private static final Color COLOR_ROOM = Color.BLACK;
    private static final Color COLOR_DOOR = Color.RED;
    private static final Color[] COLORS_TRAPDOOR = {Color.rgb(0, 251, 255), Color.rgb(255, 0, 166)};
    private static final Color COLOR_REACHABLE = Color.rgb(0, 200, 0, 0.45);
    private static final Color COLOR_REACHABLE_BORDER = Color.rgb(0, 120, 0, 0.8);

    private final Board board;
    // Character id -> color
    private final Map<String, Color> characterColors = new HashMap<>();

    private List<Player> players = List.of();
    private Player currentPlayer;
    private int[][] destinations;
    private BiConsumer<Integer, Integer> onCellClicked;

    public BoardView(Board board, List<Character> characters) {
        this.board = board;
        for (Character character : characters)
            this.characterColors.put(character.getId(), Color.web(character.getColor()));

        this.setMinSize(0, 0);
        this.widthProperty().addListener(e -> draw());
        this.heightProperty().addListener(e -> draw());
    }

    /*
     * Updates the pawns and the reachable cells (null if none): clicking a reachable cell calls onCellClicked(row, column).
     */
    public void update(List<Player> players, Player currentPlayer, int[][] destinations, BiConsumer<Integer, Integer> onCellClicked) {
        this.players = players;
        this.currentPlayer = currentPlayer;
        this.destinations = destinations;
        this.onCellClicked = onCellClicked;
        draw();
    }

    private void draw() {
        this.getChildren().clear();

        int rows = board.getSize()[0];
        int cols = board.getSize()[1];
        double cell = Math.floor(Math.min(getWidth() / cols, getHeight() / rows));
        if (cell <= 0)
            return;
        // Keep the board centered horizontally
        double offsetX = Math.floor((getWidth() - cell * cols) / 2);

        drawCells(cell, offsetX);
        drawDoors(cell, offsetX);
        drawRoomNames(cell, offsetX);
        drawDestinations(cell, offsetX);
        drawPawns(cell, offsetX);
    }

    private void drawCells(double cell, double offsetX) {
        CellType[][] cells = board.getCells();
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                if (cells[y][x].equals(CellType.NONE))
                    continue;

                Rectangle rectangle = new Rectangle(offsetX + x * cell, y * cell, cell, cell);
                if (board.getRoomAt(y, x) != null) {
                    rectangle.setFill(COLOR_ROOM);
                }
                else {
                    rectangle.setFill(COLOR_CORRIDOR);
                    rectangle.setStroke(COLOR_CORRIDOR_BORDER);
                    rectangle.setStrokeWidth(Math.max(1.0, cell * 0.05));
                }
                this.getChildren().add(rectangle);
            }
        }

        // Starting squares: the color of the character, with an X
        for (StartingPoint start : board.getStartingPoints()) {
            int y = start.getPosition()[0];
            int x = start.getPosition()[1];
            Rectangle square = new Rectangle(offsetX + x * cell, y * cell, cell, cell);
            square.setFill(characterColors.getOrDefault(start.getCharacter(), Color.GRAY));
            square.setStroke(Color.BLACK);
            double margin = cell * 0.3;
            Line line1 = new Line(offsetX + x * cell + margin, y * cell + margin, offsetX + (x + 1) * cell - margin, (y + 1) * cell - margin);
            Line line2 = new Line(offsetX + (x + 1) * cell - margin, y * cell + margin, offsetX + x * cell + margin, (y + 1) * cell - margin);
            line1.setStrokeWidth(Math.max(1.0, cell * 0.08));
            line2.setStrokeWidth(Math.max(1.0, cell * 0.08));
            this.getChildren().addAll(square, line1, line2);
        }

        // Trapdoors: each pair has its own color
        List<Trapdoor> trapdoors = board.getTrapdoors();
        for (int i = 0; i < trapdoors.size(); i++) {
            for (int[] pos : List.of(trapdoors.get(i).getPeer1(), trapdoors.get(i).getPeer2())) {
                Rectangle square = new Rectangle(offsetX + pos[1] * cell, pos[0] * cell, cell, cell);
                square.setFill(COLORS_TRAPDOOR[i % COLORS_TRAPDOOR.length]);
                square.setStroke(Color.BLACK);
                this.getChildren().add(square);
            }
        }
    }

    // Doors: a red line on the side of the cell that leads into the room
    private void drawDoors(double cell, double offsetX) {
        for (Door door : board.getDoors()) {
            int y = door.getPosition()[0];
            int x = door.getPosition()[1];
            double left = offsetX + x * cell;
            double top = y * cell;
            Line line;
            switch (door.getDirection().toLowerCase()) {
                case "up":    line = new Line(left, top, left + cell, top); break;
                case "down":  line = new Line(left, top + cell, left + cell, top + cell); break;
                case "left":  line = new Line(left, top, left, top + cell); break;
                default:      line = new Line(left + cell, top, left + cell, top + cell); break;
            }
            line.setStroke(COLOR_DOOR);
            line.setStrokeWidth(Math.max(2.0, cell * 0.12));
            this.getChildren().add(line);
        }
    }

    /*
     * Each room name is a label on the topmost row of the room that is wide enough to contain it.
     * If no row is wide enough, the font is reduced to fit the widest one.
     */
    private void drawRoomNames(double cell, double offsetX) {
        for (Room room : board.getRooms()) {
            String name = room.getName().toUpperCase();
            double fontSize = cell * 0.75;
            double padding = cell * 0.15;

            // Longest horizontal run of cells of this room, for each row
            int bestRow = -1;
            int bestCol = -1;
            int widestRow = -1;
            int widestCol = -1;
            int widestLength = 0;
            double textWidth = measure(name, fontSize);
            for (int y = 0; y < board.getSize()[0] && bestRow < 0; y++) {
                int x = 0;
                while (x < board.getSize()[1]) {
                    if (!room.equals(board.getRoomAt(y, x))) {
                        x++;
                        continue;
                    }
                    int start = x;
                    while (x < board.getSize()[1] && room.equals(board.getRoomAt(y, x)))
                        x++;
                    int length = x - start;
                    if (length > widestLength) {
                        widestLength = length;
                        widestRow = y;
                        widestCol = start;
                    }
                    if (length * cell - 2 * padding >= textWidth) {
                        bestRow = y;
                        bestCol = start;
                        break;
                    }
                }
            }
            if (bestRow < 0) {
                bestRow = widestRow;
                bestCol = widestCol;
                fontSize *= (widestLength * cell - 2 * padding) / textWidth;
            }

            Label label = new Label(name);
            label.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            label.setTextFill(Color.WHITE);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setPrefHeight(cell);
            label.setLayoutX(offsetX + bestCol * cell + padding);
            label.setLayoutY(bestRow * cell);
            label.setMouseTransparent(true);
            this.getChildren().add(label);
        }
    }

    private static double measure(String text, double fontSize) {
        Text measure = new Text(text);
        measure.setFont(Font.font("System", FontWeight.BOLD, fontSize));
        return measure.getLayoutBounds().getWidth();
    }

    private void drawDestinations(double cell, double offsetX) {
        if (destinations == null)
            return;

        for (int y = 0; y < destinations.length; y++) {
            for (int x = 0; x < destinations[y].length; x++) {
                if (destinations[y][x] <= 0)
                    continue;

                Rectangle rectangle = new Rectangle(offsetX + x * cell, y * cell, cell, cell);
                rectangle.setFill(COLOR_REACHABLE);
                rectangle.setStroke(COLOR_REACHABLE_BORDER);
                rectangle.setCursor(Cursor.HAND);
                final int row = y;
                final int col = x;
                rectangle.setOnMouseClicked(e -> {
                    if (onCellClicked != null)
                        onCellClicked.accept(row, col);
                });
                this.getChildren().add(rectangle);
            }
        }
    }

    private void drawPawns(double cell, double offsetX) {
        for (Player player : players) {
            int[] pos = player.getPosition();
            if (pos == null || player.getCharacter() == null)
                continue;

            Circle pawn = new Circle(offsetX + (pos[1] + 0.5) * cell, (pos[0] + 0.5) * cell, cell * 0.38,
                    characterColors.getOrDefault(player.getCharacter().getId(), Color.GRAY));
            pawn.setStroke(Color.BLACK);
            pawn.setStrokeWidth(player.equals(currentPlayer) ? Math.max(2.0, cell * 0.12) : 1.0);
            pawn.setOpacity(player.isEliminated() ? 0.5 : 1.0);
            pawn.setMouseTransparent(true);
            this.getChildren().add(pawn);
        }
    }
}
