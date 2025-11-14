package com.unc.gdsc.minesweeper.swing;

import com.unc.gdsc.minesweeper.core.Cell;
import com.unc.gdsc.minesweeper.core.GameBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Swing implementation of Minesweeper game.
 * <p>
 * Provides a graphical user interface using Java Swing components.
 * Left-click to reveal cells, right-click to flag/unflag.
 * </p>
 *
 * @author UNC-CH Google Developer Student Club
 * @version 2.0
 */
public class MinesweeperSwing extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int ROWS = 16;
    private static final int COLS = 16;
    private static final int MINES = 40;
    private static final int CELL_SIZE = 35;

    private final GameBoard gameBoard;
    private final JButton[][] buttons;

    /**
     * Constructs the Minesweeper Swing application.
     */
    public MinesweeperSwing() {
        super("Minesweeper - Swing");
        this.gameBoard = new GameBoard(ROWS, COLS, MINES);
        this.buttons = new JButton[ROWS][COLS];
        initializeUI();
    }

    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> newGame());
        gameMenu.add(newGameItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        // Create game grid
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                buttons[row][col] = createCellButton(row, col);
                gridPanel.add(buttons[row][col]);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates a button for a single cell.
     *
     * @param row the row index
     * @param col the column index
     * @return configured JButton
     */
    private JButton createCellButton(int row, int col) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameBoard.isGameOver()) {
                    return;
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(row, col);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    handleLeftClick(row, col);
                }
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
                if (gameBoard.isWon()) {
                    showGameOverDialog("Victory!", "Congratulations! You've cleared all mines!");
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
        boolean toggled = gameBoard.toggleFlag(row, col);
        if (toggled) {
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
        JButton button = buttons[row][col];

        if (cell.isRevealed()) {
            button.setEnabled(false);
            if (cell.isMine()) {
                button.setText("💣");
                button.setBackground(Color.RED);
                button.setForeground(Color.WHITE);
            } else if (cell.getAdjacentMines() > 0) {
                button.setText(String.valueOf(cell.getAdjacentMines()));
                button.setForeground(getNumberColor(cell.getAdjacentMines()));
            } else {
                button.setText("");
                button.setBackground(Color.LIGHT_GRAY);
            }
        } else if (cell.isFlagged()) {
            button.setText("🚩");
            button.setForeground(Color.RED);
        } else {
            button.setText("");
        }
    }

    /**
     * Gets the color for displaying mine count numbers.
     *
     * @param count the adjacent mine count
     * @return appropriate Color for the count
     */
    private Color getNumberColor(int count) {
        switch (count) {
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128); // Dark blue
            case 5: return new Color(128, 0, 0); // Maroon
            case 6: return Color.CYAN;
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    /**
     * Shows a game over dialog.
     *
     * @param title   the dialog title
     * @param message the dialog message
     */
    private void showGameOverDialog(String title, String message) {
        int result = JOptionPane.showConfirmDialog(
                this,
                message + "\n\nWould you like to play again?",
                title,
                JOptionPane.YES_NO_OPTION,
                gameBoard.isWon() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            newGame();
        }
    }

    /**
     * Starts a new game.
     */
    private void newGame() {
        dispose();
        SwingUtilities.invokeLater(MinesweeperSwing::new);
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel if system L&F fails
        }

        SwingUtilities.invokeLater(MinesweeperSwing::new);
    }
}
