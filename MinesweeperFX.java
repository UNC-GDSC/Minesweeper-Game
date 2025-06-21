import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;

/**
 * A JavaFX implementation of Minesweeper.
 * <p>
 * Creates a ROWS×COLS grid with MINES mines placed at random.
 * Left-click to reveal a cell, right-click to toggle a flag.
 * Flood‐fill reveals empty regions; dialogs notify win/lose.
 * Uses an unsynchronized Deque in place of Stack for flood‐fill.
 * </p>
 */
public class MinesweeperFX extends Application {

    /** Number of rows in the board. */
    private static final int ROWS = 16;
    /** Number of columns in the board. */
    private static final int COLS = 16;
    /** Total number of mines to place. */
    private static final int MINES = 40;
    /** Shared Random instance for mine placement. */
    private static final Random RANDOM = new Random();

    private Cell[][] board;
    private Button[][] buttons;
    private boolean gameOver;
    private int cellsRevealed;

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage primaryStage) {
        initModel();
        GridPane grid = createGridPane();
        primaryStage.setTitle("MinesweeperFX");
        primaryStage.setScene(new Scene(grid));
        primaryStage.show();
    }

    /**
     * Initialize game model: create cells array, place mines, compute adjacency counts.
     */
    private void initModel() {
        board   = new Cell[ROWS][COLS];
        buttons = new Button[ROWS][COLS];
        createEmptyCells();
        placeMines();
        computeAdjacencies();
    }

    /**
     * Create blank Cell objects for every grid coordinate.
     */
    private void createEmptyCells() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = new Cell();
            }
        }
    }

    /**
     * Randomly place {@value #MINES} mines using a shared Random.
     */
    private void placeMines() {
        int placed = 0;
        while (placed < MINES) {
            int r = RANDOM.nextInt(ROWS), c = RANDOM.nextInt(COLS);
            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                placed++;
            }
        }
    }

    /**
     * Compute and store adjacent-mine counts for each cell.
     */
    private void computeAdjacencies() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c].setAdjacentMines(countAdjacent(r, c));
            }
        }
    }

    /**
     * Count how many mines surround the cell at (row,col).
     *
     * @param row the row index
     * @param col the column index
     * @return number of adjacent mines
     */
    private int countAdjacent(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int rr = row + dr, cc = col + dc;
                if (rr >= 0 && rr < ROWS
                        && cc >= 0 && cc < COLS
                        && board[rr][cc].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Build and return the GridPane of Buttons representing the board.
     *
     * @return configured GridPane
     */
    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setHgap(1);
        grid.setVgap(1);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Button btn = createCellButton(r, c);
                buttons[r][c] = btn;
                grid.add(btn, c, r);
            }
        }
        return grid;
    }

    /**
     * Create a Button for a single cell, wired with mouse event handlers.
     *
     * @param row the cell's row index
     * @param col the cell's column index
     * @return configured Button
     */
    private Button createCellButton(int row, int col) {
        Button btn = new Button();
        btn.setPrefSize(35, 35);
        btn.setFont(Font.font("Arial", 14));
        btn.setOnMouseClicked(e -> {
            if (gameOver) return;
            if (e.getButton() == MouseButton.SECONDARY) {
                toggleFlag(row, col);
            } else if (e.getButton() == MouseButton.PRIMARY) {
                revealCell(row, col);
            }
        });
        return btn;
    }

    /**
     * Toggle a flag on the specified cell (right-click).
     *
     * @param row the cell's row index
     * @param col the cell's column index
     */
    private void toggleFlag(int row, int col) {
        Cell cell = board[row][col];
        if (cell.isRevealed()) return;
        cell.setFlagged(!cell.isFlagged());
        buttons[row][col].setText(cell.isFlagged() ? "F" : "");
    }

    /**
     * Reveal a cell (left-click).  
     * If it's a mine, end game;  
     * if zero adjacent mines, flood-fill;  
     * otherwise display count.
     *
     * @param row the cell's row index
     * @param col the cell's column index
     */
    private void revealCell(int row, int col) {
        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) return;

        cell.setRevealed(true);
        buttons[row][col].setDisable(true);
        cellsRevealed++;

        if (cell.isMine()) {
            showMine(row, col);
            endGame(false);
        } else if (cell.getAdjacentMines() > 0) {
            buttons[row][col].setText(String.valueOf(cell.getAdjacentMines()));
            checkWin();
        } else {
            floodFillZeros(row, col);
            checkWin();
        }
    }

    /**
     * Display a mine at the given cell with a red background.
     *
     * @param row the cell's row index
     * @param col the cell's column index
     */
    private void showMine(int row, int col) {
        Button btn = buttons[row][col];
        btn.setText("M");
        btn.setStyle("-fx-background-color: red;");
    }

    /**
     * Flood-fill reveal of all connected zero-adjacent cells.
     * <p>
     * Uses an unsynchronized Deque for the stack mechanism.
     * </p>
     *
     * @param startRow the starting cell's row index
     * @param startCol the starting cell's column index
     */
    private void floodFillZeros(int startRow, int startCol) {
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{startRow, startCol});

        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            for (int[] neighbor : getValidNeighbors(pos[0], pos[1])) {
                processNeighbor(neighbor[0], neighbor[1], stack);
            }
        }
    }

    /**
     * Return a list of valid neighbor coordinates for flood-fill.
     *
     * @param row the current cell's row index
     * @param col the current cell's column index
     * @return list of {row,col} pairs for each neighbor
     */
    private List<int[]> getValidNeighbors(int row, int col) {
        List<int[]> neighbors = new ArrayList<>(8);
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = row + dr, c = col + dc;
                if ((dr != 0 || dc != 0)
                        && r >= 0 && r < ROWS
                        && c >= 0 && c < COLS) {
                    neighbors.add(new int[]{r, c});
                }
            }
        }
        return neighbors;
    }

    /**
     * Reveal or enqueue a neighbor cell during flood-fill.
     *
     * @param r     neighbor row index
     * @param c     neighbor column index
     * @param stack flood-fill deque
     */
    private void processNeighbor(int r, int c, Deque<int[]> stack) {
        Cell nbr = board[r][c];
        if (nbr.isRevealed() || nbr.isFlagged()) return;

        nbr.setRevealed(true);
        buttons[r][c].setDisable(true);
        cellsRevealed++;

        if (nbr.isMine()) {
            return;  // don't expand from a mine
        }

        int count = nbr.getAdjacentMines();
        if (count > 0) {
            buttons[r][c].setText(String.valueOf(count));
        } else {
            buttons[r][c].setText("");
            stack.push(new int[]{r, c});
        }
    }

    /**
     * Check for win condition (all non-mine cells revealed) and end game if met.
     */
    private void checkWin() {
        if (cellsRevealed == ROWS * COLS - MINES) {
            endGame(true);
        }
    }

    /**
     * End the game: reveal all mines and show an alert dialog.
     *
     * @param won true if player won, false if hit a mine
     */
    private void endGame(boolean won) {
        gameOver = true;
        revealAllMines();
        String title = won ? "You Win" : "Game Over";
        String msg   = won
                ? "Congratulations! You've cleared the board!"
                : "You hit a mine! Game Over.";
        Alert.AlertType type = won
                ? Alert.AlertType.INFORMATION
                : Alert.AlertType.ERROR;
        showAlert(title, msg, type);
    }

    /**
     * Reveal all mines on the board (called at game end).
     */
    private void revealAllMines() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c].isMine()) {
                    buttons[r][c].setText("M");
                    buttons[r][c].setDisable(true);
                }
            }
        }
    }

    /**
     * Show a JavaFX alert on the UI thread.
     *
     * @param title   dialog title
     * @param message dialog message
     * @param type    alert type
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Model for each cell in the Minesweeper grid.
     */
    private static class Cell {
        private boolean mine;
        private boolean revealed;
        private boolean flagged;
        private int adjacentMines;

        boolean isMine()               { return mine; }
        void    setMine(boolean m)     { mine = m; }
        boolean isRevealed()           { return revealed; }
        void    setRevealed(boolean r) { revealed = r; }
        boolean isFlagged()            { return flagged; }
        void    setFlagged(boolean f)  { flagged = f; }
        int     getAdjacentMines()     { return adjacentMines; }
        void    setAdjacentMines(int a){ adjacentMines = a; }
    }
}
