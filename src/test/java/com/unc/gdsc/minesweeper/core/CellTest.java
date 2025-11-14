package com.unc.gdsc.minesweeper.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Cell class.
 *
 * @author UNC-CH Google Developer Student Club
 * @version 2.0
 */
class CellTest {

    private Cell cell;

    @BeforeEach
    void setUp() {
        cell = new Cell();
    }

    @Test
    void testInitialState() {
        assertFalse(cell.isMine(), "New cell should not be a mine");
        assertFalse(cell.isRevealed(), "New cell should not be revealed");
        assertFalse(cell.isFlagged(), "New cell should not be flagged");
        assertEquals(0, cell.getAdjacentMines(), "New cell should have 0 adjacent mines");
    }

    @Test
    void testSetMine() {
        cell.setMine(true);
        assertTrue(cell.isMine(), "Cell should be a mine after setMine(true)");

        cell.setMine(false);
        assertFalse(cell.isMine(), "Cell should not be a mine after setMine(false)");
    }

    @Test
    void testSetRevealed() {
        cell.setRevealed(true);
        assertTrue(cell.isRevealed(), "Cell should be revealed after setRevealed(true)");

        cell.setRevealed(false);
        assertFalse(cell.isRevealed(), "Cell should not be revealed after setRevealed(false)");
    }

    @Test
    void testSetFlagged() {
        cell.setFlagged(true);
        assertTrue(cell.isFlagged(), "Cell should be flagged after setFlagged(true)");

        cell.setFlagged(false);
        assertFalse(cell.isFlagged(), "Cell should not be flagged after setFlagged(false)");
    }

    @Test
    void testSetAdjacentMines() {
        cell.setAdjacentMines(3);
        assertEquals(3, cell.getAdjacentMines(), "Adjacent mines should be 3");

        cell.setAdjacentMines(8);
        assertEquals(8, cell.getAdjacentMines(), "Adjacent mines should be 8");

        cell.setAdjacentMines(0);
        assertEquals(0, cell.getAdjacentMines(), "Adjacent mines should be 0");
    }

    @Test
    void testReset() {
        cell.setMine(true);
        cell.setRevealed(true);
        cell.setFlagged(true);
        cell.setAdjacentMines(5);

        cell.reset();

        assertFalse(cell.isMine(), "Reset cell should not be a mine");
        assertFalse(cell.isRevealed(), "Reset cell should not be revealed");
        assertFalse(cell.isFlagged(), "Reset cell should not be flagged");
        assertEquals(0, cell.getAdjacentMines(), "Reset cell should have 0 adjacent mines");
    }

    @Test
    void testToString() {
        String str = cell.toString();
        assertNotNull(str, "toString should not return null");
        assertTrue(str.contains("Cell{"), "toString should contain class name");
    }
}
