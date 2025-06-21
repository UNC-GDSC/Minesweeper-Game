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
import java.util.Stack;

/**
 * A JavaFX implementation of Minesweeper.
 * <p>
 * Creates a ROWS×COLS grid with MINES mines placed at random.
 * Left-click to reveal a cell, right-click to toggle a flag.
 * Flood-fill reveals empty regions; dialogs notify win/lose.
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initModel();
        GridPane grid = createGridPane();
        primaryStage.setTitle("MinesweeperFX");
        primaryStage.setScene(new Scene(grid));
        primaryStage.show();
    }

    /** Initialize game model: cells, mines, and adjacent counts. */
    private void initModel() {
        board = new Cell[ROWS][COLS];
        buttons = new Button[ROWS][COLS];
        createEmptyCells();
        placeMines();
        computeAdjacencies();
    }

    /** Create blank cells for the grid. */
    private void createEmptyCells() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = new Cell();
            }
        }
    }

    /** Randomly place MINES mines on the board. */
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

    /** Compute adjacent-mine count for each cell. */
    private void computeAdjacencies() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c].setAdjacentMines(countAdjacent(r, c));
            }
        }
    }

    /**
     * Count how many mines are adjacent to (row, col).
     *
     * @return number of adjacent mines
     */
    private int countAdjacent(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int rr = row + dr, cc = col + dc;
                if (rr >= 0 && rr < ROWS && cc >= 0 && cc < COLS && board[rr][cc].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Build and return the GridPane of buttons. */
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
     * Create a Button for one cell, with mouse handler for clicks.
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
     * Toggle a flag on the specified cell.
     */
    private void toggleFlag(int row, int col) {
        Cell cell = board[row][col];
        if (cell.isRevealed()) return;
        cell.setFlagged(!cell.isFlagged());
        buttons[row][col].setText(cell.isFlagged() ? "F" : "");
    }

    /**
     * Reveal a cell: show mine, number, or flood-fill.
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

    /** Reveal a mine with red background. */
    private void showMine(int row, int col) {
        Button btn = buttons[row][col];
        btn.setText("M");
        btn.setStyle("-fx-background-color: red;");
    }

    /**
     * Flood-fill reveal of all connected zero-adjacent cells.
     */
    private void floodFillZeros(int row, int col) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{row, col});
        while (!stack.isEmpty()) {
            int[] p = stack.pop();
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int r = p[0] + dr, c = p[1] + dc;
                    if (r < 0 || r >= ROWS || c < 0 || c >= COLS) continue;
                    Cell nbr = board[r][c];
                    if (!nbr.isRevealed() && !nbr.isFlagged()) {
                        nbr.setRevealed(true);
                        buttons[r][c].setDisable(true);
                        cellsRevealed++;
                        if (nbr.isMine()) continue;
                        if (nbr.getAdjacentMines() > 0) {
                            buttons[r][c].setText(String.valueOf(nbr.getAdjacentMines()));
                        } else {
                            buttons[r][c].setText("");
                            stack.push(new int[]{r, c});
                        }
                    }
                }
            }
        }
    }

    /** Check for win condition and end game if met. */
    private void checkWin() {
        if (cellsRevealed == ROWS * COLS - MINES) {
            endGame(true);
        }
    }

    /**
     * End the game: reveal all mines and show alert.
     *
     * @param won true if player cleared all non-mines
     */
    private void endGame(boolean won) {
        gameOver = true;
        revealAllMines();
        String title = won ? "You Win" : "Game Over";
        String msg   = won ? "Congratulations! You've cleared the board!" : "You hit a mine! Game Over.";
        Alert.AlertType type = won ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        showAlert(title, msg, type);
    }

    /** Reveal every mine on the board. */
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

        boolean isMine()             { return mine; }
        void    setMine(boolean m)   { mine = m; }
        boolean isRevealed()         { return revealed; }
        void    setRevealed(boolean r){ revealed = r; }
        boolean isFlagged()          { return flagged; }
        void    setFlagged(boolean f){ flagged = f; }
        int     getAdjacentMines()   { return adjacentMines; }
        void    setAdjacentMines(int a){ adjacentMines = a; }
    }
}
