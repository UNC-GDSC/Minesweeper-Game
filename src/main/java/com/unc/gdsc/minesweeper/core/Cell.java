package com.unc.gdsc.minesweeper.core;

import java.io.Serializable;

/**
 * Represents a single cell in the Minesweeper grid.
 * <p>
 * Each cell can be a mine or empty, revealed or hidden, and flagged or unflagged.
 * The cell also tracks the number of adjacent mines.
 * </p>
 *
 * @author UNC-CH Google Developer Student Club
 * @version 2.0
 */
public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean mine;
    private boolean revealed;
    private boolean flagged;
    private int adjacentMines;

    /**
     * Constructs a new empty Cell.
     */
    public Cell() {
        this.mine = false;
        this.revealed = false;
        this.flagged = false;
        this.adjacentMines = 0;
    }

    /**
     * Checks if this cell contains a mine.
     *
     * @return true if this cell is a mine, false otherwise
     */
    public boolean isMine() {
        return mine;
    }

    /**
     * Sets whether this cell contains a mine.
     *
     * @param mine true to make this cell a mine, false otherwise
     */
    public void setMine(boolean mine) {
        this.mine = mine;
    }

    /**
     * Checks if this cell has been revealed.
     *
     * @return true if revealed, false if still hidden
     */
    public boolean isRevealed() {
        return revealed;
    }

    /**
     * Sets the revealed state of this cell.
     *
     * @param revealed true to reveal, false to hide
     */
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    /**
     * Checks if this cell is flagged by the player.
     *
     * @return true if flagged, false otherwise
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Sets the flagged state of this cell.
     *
     * @param flagged true to flag, false to unflag
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * Gets the number of mines adjacent to this cell.
     *
     * @return number of adjacent mines (0-8)
     */
    public int getAdjacentMines() {
        return adjacentMines;
    }

    /**
     * Sets the number of mines adjacent to this cell.
     *
     * @param adjacentMines number of adjacent mines (0-8)
     */
    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    /**
     * Resets this cell to its initial state.
     */
    public void reset() {
        this.mine = false;
        this.revealed = false;
        this.flagged = false;
        this.adjacentMines = 0;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "mine=" + mine +
                ", revealed=" + revealed +
                ", flagged=" + flagged +
                ", adjacentMines=" + adjacentMines +
                '}';
    }
}
