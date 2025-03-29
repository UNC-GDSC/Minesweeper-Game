import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;
import java.util.Stack;

public class MinesweeperFX extends Application {

    // Board configuration
    private static final int ROWS = 16;
    private static final int COLS = 16;
    private static final int MINES = 40;
    private Cell[][] board = new Cell[ROWS][COLS];
    private Button[][] buttons = new Button[ROWS][COLS];
    private boolean gameOver = false;
    private int cellsRevealed = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initBoard();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setHgap(1);
        grid.setVgap(1);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Button btn = new Button();
                btn.setPrefSize(35, 35);
                btn.setFont(Font.font("Arial", 14));
                final int row = r;
                final int col = c;
                btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (gameOver) return;
                        if (event.getButton() == MouseButton.SECONDARY) {
                            toggleFlag(row, col);
                        } else if (event.getButton() == MouseButton.PRIMARY) {
                            revealCell(row, col);
                        }
                    }
                });
                buttons[r][c] = btn;
                grid.add(btn, c, r);
            }
        }

        Scene scene = new Scene(grid);
        primaryStage.setTitle("MinesweeperFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initBoard() {
        // Initialize cells
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = new Cell();
            }
        }
        // Place mines randomly
        int minesPlaced = 0;
        Random rand = new Random();
        while (minesPlaced < MINES) {
            int r = rand.nextInt(ROWS);
            int c = rand.nextInt(COLS);
            if (!board[r][c].isMine) {
                board[r][c].isMine = true;
                minesPlaced++;
            }
        }
        // Set adjacent mine counts
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c].adjacentMines = countAdjacentMines(r, c);
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    if (board[r][c].isMine) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void toggleFlag(int row, int col) {
        if (board[row][col].isRevealed) return;
        board[row][col].isFlagged = !board[row][col].isFlagged;
        buttons[row][col].setText(board[row][col].isFlagged ? "F" : "");
    }

    private void revealCell(int row, int col) {
        if (board[row][col].isFlagged || board[row][col].isRevealed) return;

        board[row][col].isRevealed = true;
        buttons[row][col].setDisable(true);
        cellsRevealed++;

        if (board[row][col].isMine) {
            buttons[row][col].setText("M");
            buttons[row][col].setStyle("-fx-background-color: red;");
            gameOver();
            return;
        }

        if (board[row][col].adjacentMines > 0) {
            buttons[row][col].setText(String.valueOf(board[row][col].adjacentMines));
        } else {
            buttons[row][col].setText("");
            floodFill(row, col);
        }

        // Check win condition
        if (cellsRevealed == ROWS * COLS - MINES) {
            winGame();
        }
    }

    private void floodFill(int row, int col) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{row, col});

        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            int r = pos[0], c = pos[1];

            for (int i = r - 1; i <= r + 1; i++) {
                for (int j = c - 1; j <= c + 1; j++) {
                    if (i >= 0 && i < ROWS && j >= 0 && j < COLS) {
                        if (!board[i][j].isRevealed && !board[i][j].isFlagged) {
                            board[i][j].isRevealed = true;
                            buttons[i][j].setDisable(true);
                            cellsRevealed++;
                            if (board[i][j].isMine) continue;
                            if (board[i][j].adjacentMines > 0) {
                                buttons[i][j].setText(String.valueOf(board[i][j].adjacentMines));
                            } else {
                                buttons[i][j].setText("");
                                stack.push(new int[]{i, j});
                            }
                        }
                    }
                }
            }
        }
    }

    private void gameOver() {
        gameOver = true;
        revealAllMines();
        showAlert("Game Over", "You hit a mine! Game Over.", Alert.AlertType.ERROR);
    }

    private void winGame() {
        gameOver = true;
        showAlert("Congratulations!", "You've cleared the board! You win!", Alert.AlertType.INFORMATION);
    }

    private void revealAllMines() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c].isMine) {
                    buttons[r][c].setText("M");
                    buttons[r][c].setDisable(true);
                }
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Cell model representing each board cell
    private static class Cell {
        boolean isMine = false;
        boolean isRevealed = false;
        boolean isFlagged = false;
        int adjacentMines = 0;
    }
}
