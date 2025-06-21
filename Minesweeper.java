import javax.swing.*;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;

/**
 * A simple Minesweeper game implemented with Swing.
 * <p>
 * The board is a ROWS×COLS grid containing MINES mines placed at random.
 * Left-click to reveal a cell, right-click to toggle a flag.
 * Implements Serializable; the board model array is marked transient.
 * </p>
 */
public class Minesweeper extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Number of rows on the board. */
    private static final int ROWS = 16;
    /** Number of columns on the board. */
    private static final int COLS = 16;
    /** Total number of mines to place. */
    private static final int MINES = 40;
    /** Shared Random for mine placement. */
    private static final Random RANDOM = new Random();

    /** Game board model (not serialized). */
    private transient Cell[][] board;
    /** GUI buttons corresponding to each cell. */
    private JButton[][] buttons;
    /** Has the game ended (win or loss)? */
    private boolean gameOver;
    /** How many non-mine cells have been revealed so far. */
    private int cellsRevealed;

    /**
     * Constructs the game: initializes the model and GUI.
     */
    public Minesweeper() {
        super("Minesweeper");
        board   = new Cell[ROWS][COLS];
        buttons = new JButton[ROWS][COLS];
        initBoardModel();
        initGUI();
    }

    /**
     * Initialize the board model: create cells, place mines, compute adjacencies.
     */
    private void initBoardModel() {
        createEmptyCells();
        placeMines();
        computeAllAdjacents();
    }

    /**
     * Create empty cell instances for the entire board.
     */
    private void createEmptyCells() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = new Cell();
            }
        }
    }

    /**
     * Randomly place MINES mines into the board using a shared Random.
     */
    private void placeMines() {
        int placed = 0;
        while (placed < MINES) {
            int r = RANDOM.nextInt(ROWS);
            int c = RANDOM.nextInt(COLS);
            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                placed++;
            }
        }
    }

    /**
     * Compute adjacent-mine counts for every cell.
     */
    private void computeAllAdjacents() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c].setAdjacentMines(countAdjacentMines(r, c));
            }
        }
    }

    /**
     * Count mines adjacent to the specified cell.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return number of surrounding mines
     */
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int rr = row + dr, cc = col + dc;
                if (rr >= 0 && rr < ROWS && cc >= 0 && cc < COLS
                        && board[rr][cc].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Initialize and display the Swing GUI: grid of buttons.
     */
    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS, COLS));
        setSize(600, 600);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                buttons[r][c] = createCellButton(r, c);
                add(buttons[r][c]);
            }
        }

        setVisible(true);
    }

    /**
     * Create and configure a JButton for a single cell.
     *
     * @param row the row index
     * @param col the column index
     * @return configured JButton
     */
    private JButton createCellButton(int row, int col) {
        JButton btn = new JButton();
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.addMouseListener(new MouseAdapter() {
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
        return btn;
    }

    /**
     * Toggle a flag on the specified cell (right-click).
     *
     * @param row the row index
     * @param col the column index
     */
    private void toggleFlag(int row, int col) {
        Cell cell = board[row][col];
        if (cell.isRevealed()) return;
        cell.setFlagged(!cell.isFlagged());
        buttons[row][col].setText(cell.isFlagged() ? "F" : "");
    }

    /**
     * Reveal the specified cell (left-click). If it's a mine, end game; if zero,
     * flood-fill neighboring zeros; otherwise display adjacent count.
     *
     * @param row the row index
     * @param col the column index
     */
    private void revealCell(int row, int col) {
        if (!canReveal(row, col)) return;

        Cell cell = board[row][col];
        cell.setRevealed(true);
        buttons[row][col].setEnabled(false);
        cellsRevealed++;

        if (cell.isMine()) {
            showMine(row, col);
            endGame(false);
        } else if (cell.getAdjacentMines() > 0) {
            buttons[row][col].setText(String.valueOf(cell.getAdjacentMines()));
            checkWin();
        } else {
            buttons[row][col].setText("");
            floodFillZeros(row, col);
            checkWin();
        }
    }

    /**
     * Determine whether the cell can be revealed.
     *
     * @param row the row index
     * @param col the column index
     * @return true if not yet revealed and not flagged
     */
    private boolean canReveal(int row, int col) {
        Cell cell = board[row][col];
        return !cell.isRevealed() && !cell.isFlagged();
    }

    /**
     * Display a mine at the given position with red background.
     *
     * @param row the row index
     * @param col the column index
     */
    private void showMine(int row, int col) {
        buttons[row][col].setText("M");
        buttons[row][col].setBackground(Color.RED);
    }

    /**
     * Flood-fill reveal of all connected zero-adjacent cells.
     * <p>
     * Uses an unsynchronized Deque instead of Stack for better performance.
     * </p>
     *
     * @param startRow starting cell row
     * @param startCol starting cell column
     */
    private void floodFillZeros(int startRow, int startCol) {
        Deque<Point> stack = new ArrayDeque<>();
        stack.push(new Point(startRow, startCol));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            for (Point neighbor : getValidNeighbors(p.x, p.y)) {
                processNeighbor(neighbor.x, neighbor.y, stack);
            }
        }
    }

    /**
     * Return a list of valid neighbor coordinates for a given cell.
     *
     * @param row the current row
     * @param col the current column
     * @return list of neighbor Points within bounds
     */
    private List<Point> getValidNeighbors(int row, int col) {
        List<Point> neighbors = new ArrayList<>(8);
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = row + dr, c = col + dc;
                if ((dr != 0 || dc != 0) && r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    neighbors.add(new Point(r, c));
                }
            }
        }
        return neighbors;
    }

    /**
     * Reveal or queue a neighbor cell during flood-fill.
     *
     * @param r     neighbor row
     * @param c     neighbor column
     * @param stack flood-fill deque for zero cells
     */
    private void processNeighbor(int r, int c, Deque<Point> stack) {
        Cell nbr = board[r][c];
        if (nbr.isRevealed() || nbr.isFlagged()) return;

        nbr.setRevealed(true);
        buttons[r][c].setEnabled(false);
        cellsRevealed++;

        if (nbr.isMine()) {
            return; // do not expand from mines
        }

        int count = nbr.getAdjacentMines();
        if (count > 0) {
            buttons[r][c].setText(String.valueOf(count));
        } else {
            buttons[r][c].setText("");
            stack.push(new Point(r, c));
        }
    }

    /**
     * Check whether the player has won (all non-mine cells revealed).
     * If so, end the game as a win.
     */
    private void checkWin() {
        if (cellsRevealed == ROWS * COLS - MINES) {
            endGame(true);
        }
    }

    /**
     * End the game: reveal all mines and display a dialog.
     *
     * @param won true if the player won, false if hit a mine
     */
    private void endGame(boolean won) {
        gameOver = true;
        revealAllMines();
        String title = won ? "You Win" : "Game Over";
        String msg   = won
                ? "Congratulations! You've cleared the board!"
                : "Game Over! You hit a mine.";
        int type     = won
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(this, msg, title, type);
    }

    /**
     * Reveal all mines on the board (called at game end).
     */
    private void revealAllMines() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c].isMine()) {
                    buttons[r][c].setText("M");
                    buttons[r][c].setEnabled(false);
                }
            }
        }
    }

    /**
     * Model representing a single cell on the board.
     */
    private static class Cell implements Serializable {
        private static final long serialVersionUID = 1L;

        private boolean mine;
        private boolean revealed;
        private boolean flagged;
        private int adjacentMines;

        boolean isMine()              { return mine; }
        void    setMine(boolean m)    { mine = m; }
        boolean isRevealed()          { return revealed; }
        void    setRevealed(boolean r){ revealed = r; }
        boolean isFlagged()           { return flagged; }
        void    setFlagged(boolean f){ flagged = f; }
        int     getAdjacentMines()    { return adjacentMines; }
        void    setAdjacentMines(int a){ adjacentMines = a; }
    }

    /**
     * Entry point: launch the game on the Event Dispatch Thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Minesweeper::new);
    }
}
