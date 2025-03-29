import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Stack;

public class Minesweeper extends JFrame {

    // Board configuration
    private static final int ROWS = 16;
    private static final int COLS = 16;
    private static final int MINES = 40;
    private Cell[][] board = new Cell[ROWS][COLS];
    private JButton[][] buttons = new JButton[ROWS][COLS];
    private boolean gameOver = false;
    private int cellsRevealed = 0;

    public Minesweeper() {
        initBoard();
        initGUI();
    }

    // Initialize the board model (cells and mines)
    private void initBoard() {
        // Initialize cells
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = new Cell();
            }
        }
        // Randomly place mines
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
        // Calculate adjacent mines for each cell
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c].adjacentMines = countAdjacentMines(r, c);
            }
        }
    }

    // Count the number of mines adjacent to a given cell
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

    // Initialize the GUI
    private void initGUI() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new GridLayout(ROWS, COLS));

        // Create buttons and add them to the frame
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                JButton button = new JButton();
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setFont(new Font("Arial", Font.BOLD, 12));
                final int row = r;
                final int col = c;
                // Handle left-click and right-click events
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver) return;
                        if (SwingUtilities.isRightMouseButton(e)) {
                            toggleFlag(row, col);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            revealCell(row, col);
                        }
                    }
                });
                buttons[r][c] = button;
                add(button);
            }
        }

        setVisible(true);
    }

    // Toggle flag on a cell
    private void toggleFlag(int row, int col) {
        if (board[row][col].isRevealed) return;
        board[row][col].isFlagged = !board[row][col].isFlagged;
        buttons[row][col].setText(board[row][col].isFlagged ? "F" : "");
    }

    // Reveal a cell and perform flood-fill if no adjacent mines
    private void revealCell(int row, int col) {
        if (board[row][col].isFlagged || board[row][col].isRevealed) return;

        board[row][col].isRevealed = true;
        buttons[row][col].setEnabled(false);
        cellsRevealed++;

        if (board[row][col].isMine) {
            buttons[row][col].setText("M");
            buttons[row][col].setBackground(Color.RED);
            gameOver();
            return;
        }

        if (board[row][col].adjacentMines > 0) {
            buttons[row][col].setText(String.valueOf(board[row][col].adjacentMines));
        } else {
            // No adjacent mines - flood fill (reveal neighboring cells)
            buttons[row][col].setText("");
            floodFill(row, col);
        }

        // Check win condition
        if (cellsRevealed == ROWS * COLS - MINES) {
            winGame();
        }
    }

    // Flood-fill algorithm to reveal all connected cells with zero adjacent mines
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
                            buttons[i][j].setEnabled(false);
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

    // Handle game over scenario
    private void gameOver() {
        gameOver = true;
        revealAllMines();
        JOptionPane.showMessageDialog(this, "Game Over! You hit a mine.", "Game Over", JOptionPane.ERROR_MESSAGE);
    }

    // Handle win scenario
    private void winGame() {
        gameOver = true;
        JOptionPane.showMessageDialog(this, "Congratulations! You've cleared the board!", "You Win", JOptionPane.INFORMATION_MESSAGE);
    }

    // Reveal all mines (called on game over)
    private void revealAllMines() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c].isMine) {
                    buttons[r][c].setText("M");
                    buttons[r][c].setEnabled(false);
                }
            }
        }
    }

    // Cell model representing each board cell
    private static class Cell {
        boolean isMine = false;
        boolean isRevealed = false;
        boolean isFlagged = false;
        int adjacentMines = 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Minesweeper::new);
    }
}
