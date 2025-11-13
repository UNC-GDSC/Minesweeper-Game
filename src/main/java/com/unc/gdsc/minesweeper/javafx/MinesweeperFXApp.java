package com.unc.gdsc.minesweeper.javafx;

import com.unc.gdsc.minesweeper.core.Cell;
import com.unc.gdsc.minesweeper.core.GameBoard;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * JavaFX implementation of Minesweeper game.
 * <p>
 * Provides a graphical user interface using JavaFX components with integrated status bar.
 * Left-click to reveal cells, right-click to flag/unflag.
 * </p>
 *
 * @author UNC-CH Google Developer Student Club
 * @version 2.0
 */
public class MinesweeperFXApp extends Application {

    private static final int ROWS = 16;
    private static final int COLS = 16;
    private static final int MINES = 40;
    private static final double CELL_SIZE = 35.0;

    private GameBoard gameBoard;
    private Button[][] buttons;
    private StatusBarFX statusBar;
    private boolean firstClick = true;

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        gameBoard = new GameBoard(ROWS, COLS, MINES);
        buttons = new Button[ROWS][COLS];
        firstClick = true;

        BorderPane root = new BorderPane();

        // Create menu bar
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        // Create status bar
        statusBar = new StatusBarFX(MINES);
        BorderPane.setMargin(statusBar, new Insets(5));

        // Create game grid
        GridPane gridPane = createGridPane();

        // Combine status bar and grid
        BorderPane gameArea = new BorderPane();
        gameArea.setTop(statusBar);
        gameArea.setCenter(gridPane);

        root.setCenter(gameArea);

        Scene scene = new Scene(root);

        // Load CSS if available
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            // CSS file not found, continue without styling
        }

        primaryStage.setTitle("Minesweeper - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Creates the menu bar with game controls.
     *
     * @param stage the primary stage
     * @return configured MenuBar
     */
    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Game");

        MenuItem newGameItem = new MenuItem("New Game");
        newGameItem.setOnAction(e -> newGame(stage));

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());

        gameMenu.getItems().addAll(newGameItem, exitItem);
        menuBar.getMenus().add(gameMenu);

        return menuBar;
    }

    /**
     * Creates the grid pane containing all cell buttons.
     *
     * @return configured GridPane
     */
    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(2);
        gridPane.setVgap(2);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Button button = createCellButton(row, col);
                buttons[row][col] = button;
                gridPane.add(button, col, row);
            }
        }

        return gridPane;
    }

    /**
     * Creates a button for a single cell.
     *
     * @param row the row index
     * @param col the column index
     * @return configured Button
     */
    private Button createCellButton(int row, int col) {
        Button button = new Button();
        button.setPrefSize(CELL_SIZE, CELL_SIZE);
        button.setMinSize(CELL_SIZE, CELL_SIZE);
        button.setMaxSize(CELL_SIZE, CELL_SIZE);
        button.getStyleClass().add("grid-button");

        button.setOnMouseClicked(e -> {
            if (gameBoard.isGameOver()) {
                return;
            }

            if (firstClick) {
                statusBar.startTimer();
                firstClick = false;
            }

            if (e.getButton() == MouseButton.SECONDARY) {
                handleRightClick(row, col);
            } else if (e.getButton() == MouseButton.PRIMARY) {
                handleLeftClick(row, col);
            }
        });

        return button;
    }

    /**
     * Handles left-click events (reveal cell).
     *
     * @param row the row index
     * @param col the column index
     */
    private void handleLeftClick(int row, int col) {
        boolean revealed = gameBoard.revealCell(row, col);
        if (revealed) {
            updateBoard();

            if (gameBoard.isGameOver()) {
                statusBar.stopTimer();
                if (gameBoard.isWon()) {
                    gameBoard.revealAllMines();
                    updateBoard();
                    showGameOverDialog("Victory!", "Congratulations! You've cleared all mines!\nTime: " + statusBar.getElapsedSeconds() + " seconds");
                } else {
                    gameBoard.revealAllMines();
                    updateBoard();
                    showGameOverDialog("Game Over", "You hit a mine! Better luck next time.");
                }
            }
        }
    }

    /**
     * Handles right-click events (toggle flag).
     *
     * @param row the row index
     * @param col the column index
     */
    private void handleRightClick(int row, int col) {
        Cell cell = gameBoard.getCell(row, col);
        boolean wasFlagged = cell.isFlagged();

        boolean toggled = gameBoard.toggleFlag(row, col);
        if (toggled) {
            if (cell.isFlagged() && !wasFlagged) {
                statusBar.decrementMines();
            } else if (!cell.isFlagged() && wasFlagged) {
                statusBar.incrementMines();
            }
            updateCell(row, col);
        }
    }

    /**
     * Updates the entire board display.
     */
    private void updateBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                updateCell(row, col);
            }
        }
    }

    /**
     * Updates a single cell's display.
     *
     * @param row the row index
     * @param col the column index
     */
    private void updateCell(int row, int col) {
        Cell cell = gameBoard.getCell(row, col);
        Button button = buttons[row][col];

        if (cell.isRevealed()) {
            button.setDisable(true);
            if (cell.isMine()) {
                button.setText("💣");
                button.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            } else if (cell.getAdjacentMines() > 0) {
                button.setText(String.valueOf(cell.getAdjacentMines()));
                button.setStyle("-fx-text-fill: " + getNumberColor(cell.getAdjacentMines()) + ";");
            } else {
                button.setText("");
                button.setStyle("-fx-background-color: #e0e0e0;");
            }
        } else if (cell.isFlagged()) {
            button.setText("🚩");
            button.setStyle("-fx-text-fill: red;");
        } else {
            button.setText("");
            button.setStyle("");
        }
    }

    /**
     * Gets the color string for displaying mine count numbers.
     *
     * @param count the adjacent mine count
     * @return CSS color string
     */
    private String getNumberColor(int count) {
        switch (count) {
            case 1: return "#0000ff";
            case 2: return "#008000";
            case 3: return "#ff0000";
            case 4: return "#000080";
            case 5: return "#800000";
            case 6: return "#008080";
            case 7: return "#000000";
            case 8: return "#808080";
            default: return "#000000";
        }
    }

    /**
     * Shows a game over dialog with option to play again.
     *
     * @param title   the dialog title
     * @param message the dialog message
     */
    private void showGameOverDialog(String title, String message) {
        Alert alert = new Alert(
                gameBoard.isWon() ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING,
                message + "\n\nWould you like to play again?",
                ButtonType.YES,
                ButtonType.NO
        );
        alert.setTitle(title);
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            newGame((Stage) buttons[0][0].getScene().getWindow());
        }
    }

    /**
     * Starts a new game.
     *
     * @param stage the primary stage
     */
    private void newGame(Stage stage) {
        stage.close();
        start(new Stage());
    }
}
