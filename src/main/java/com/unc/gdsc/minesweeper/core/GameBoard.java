package com.unc.gdsc.minesweeper.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game board for Minesweeper.
 * <p>
 * This class manages the grid of cells, mine placement, and game logic
 * such as revealing cells and checking win/lose conditions.
 * </p>
 *
 * @author UNC-CH Google Developer Student Club
 * @version 2.0
 */
public class GameBoard implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int rows;
    private final int cols;
    private final int totalMines;
    private final Cell[][] board;
    private final Random random;

    private int cellsRevealed;
    private boolean gameOver;
    private boolean won;

    /**
     * Constructs a new GameBoard with the specified dimensions and mine count.
     *
     * @param rows       number of rows in the board
     * @param cols       number of columns in the board
     * @param totalMines total number of mines to place
     */
    public GameBoard(int rows, int cols, int totalMines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        this.board = new Cell[rows][cols];
        this.random = new Random();
        this.cellsRevealed = 0;
        this.gameOver = false;
        this.won = false;
        initialize();
    }

    /**
     * Initializes the game board by creating cells, placing mines, and computing adjacencies.
     */
    private void initialize() {
        createEmptyCells();
        placeMines();
        computeAllAdjacencies();
    }

    /**
     * Creates empty cell instances for the entire board.
     */
    private void createEmptyCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new Cell();
            }
        }
    }

    /**
     * Randomly places mines into the board.
     */
    private void placeMines() {
        int placed = 0;
        while (placed < totalMines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                placed++;
            }
        }
    }

    /**
     * Computes adjacent mine counts for all cells.
     */
    private void computeAllAdjacencies() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c].setAdjacentMines(countAdjacentMines(r, c));
            }
        }
    }

    /**
     * Counts the number of mines adjacent to the specified cell.
     *
     * @param row the row index
     * @param col the column index
     * @return number of adjacent mines (0-8)
     */
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = row + dr;
                int c = col + dc;
                if (isValidCell(r, c) && board[r][c].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Checks if the specified coordinates are within the board bounds.
     *
     * @param row the row index
     * @param col the column index
     * @return true if valid, false otherwise
     */
    public boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Gets the cell at the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @return the Cell at the specified position
     * @throws IndexOutOfBoundsException if coordinates are invalid
     */
    public Cell getCell(int row, int col) {
        if (!isValidCell(row, col)) {
            throw new IndexOutOfBoundsException("Invalid cell coordinates: (" + row + ", " + col + ")");
        }
        return board[row][col];
    }

    /**
     * Toggles the flag state of the specified cell.
     *
     * @param row the row index
     * @param col the column index
     * @return true if flag was toggled, false if cell is already revealed or game is over
     */
    public boolean toggleFlag(int row, int col) {
        if (gameOver || !isValidCell(row, col)) {
            return false;
        }

        Cell cell = board[row][col];
        if (cell.isRevealed()) {
            return false;
        }

        cell.setFlagged(!cell.isFlagged());
        return true;
    }

    /**
     * Reveals the cell at the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @return true if cell was revealed, false if already revealed, flagged, or game is over
     */
    public boolean revealCell(int row, int col) {
        if (gameOver || !isValidCell(row, col)) {
            return false;
        }

        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return false;
        }

        cell.setRevealed(true);
        cellsRevealed++;

        if (cell.isMine()) {
            gameOver = true;
            won = false;
            return true;
        }

        if (cell.getAdjacentMines() == 0) {
            revealAdjacentCells(row, col);
        }

        checkWin();
        return true;
    }

    /**
     * Recursively reveals adjacent cells when a zero-mine cell is revealed.
     *
     * @param row the starting row index
     * @param col the starting column index
     */
    private void revealAdjacentCells(int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int r = row + dr;
                int c = col + dc;

                if (isValidCell(r, c)) {
                    Cell neighbor = board[r][c];
                    if (!neighbor.isRevealed() && !neighbor.isFlagged() && !neighbor.isMine()) {
                        neighbor.setRevealed(true);
                        cellsRevealed++;

                        if (neighbor.getAdjacentMines() == 0) {
                            revealAdjacentCells(r, c);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the player has won the game.
     */
    private void checkWin() {
        if (cellsRevealed == rows * cols - totalMines) {
            gameOver = true;
            won = true;
        }
    }

    /**
     * Reveals all mines on the board (typically called when game ends).
     */
    public void revealAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c].isMine()) {
                    board[r][c].setRevealed(true);
                }
            }
        }
    }

    /**
     * Gets all neighbor cells around the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @return list of neighbor cell coordinates as int[]{row, col}
     */
    public List<int[]> getNeighbors(int row, int col) {
        List<int[]> neighbors = new ArrayList<>(8);
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr;
                int c = col + dc;
                if (isValidCell(r, c)) {
                    neighbors.add(new int[]{r, c});
                }
            }
        }
        return neighbors;
    }

    // Getters

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getTotalMines() {
        return totalMines;
    }

    public int getCellsRevealed() {
        return cellsRevealed;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public int getRemainingCells() {
        return rows * cols - totalMines - cellsRevealed;
    }
}
