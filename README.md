# JavaFX Minesweeper

A Minesweeper game implemented using JavaFX. This project demonstrates a classic Minesweeper game with a graphical user interface, complete with cell-reveal, flagging, flood-fill for empty cells, and win/lose detection.

---

## Features

- **Grid-Based Game Board:** A 16x16 grid with 40 mines.
- **Interactive Gameplay:** Left-click to reveal cells; right-click to toggle flags.
- **Flood-Fill:** Automatically reveal adjacent empty cells.
- **Win/Lose Alerts:** Informative dialogs when you win or hit a mine.
- **Customizable:** Easily modify board size, mine count, or extend the game logic.

---

## Prerequisites

- **Java Development Kit (JDK):** Version 11 or later is recommended.
- **JavaFX SDK:** Download and install the JavaFX SDK from [openjfx.io](https://openjfx.io).  
- **Environment Setup:** Ensure your `PATH` or module path is set up to reference the JavaFX SDK libraries.

---

## File Structure

This project is contained in a single Java file:

```
MinesweeperFX.java
```

---

## Getting Started

### Compiling the Application

1. Open a terminal in the directory where `MinesweeperFX.java` is located.

2. Compile the application using the following command (adjust the path to your JavaFX SDK's `lib` folder):

   ```bash
   javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls MinesweeperFX.java
   ```

   Replace `/path/to/javafx-sdk/lib` with the actual path to your JavaFX SDK library directory.

### Running the Application

After compiling, run the application with:

```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls MinesweeperFX
```

Again, replace `/path/to/javafx-sdk/lib` with the correct path for your environment.

---

## How It Works

- **Game Initialization:**  
  The board is initialized with a 16x16 grid of cells. Mines are randomly placed, and adjacent mine counts are computed for each cell.

- **User Interaction:**  
  - **Left-Click:** Reveals a cell. If the cell contains a mine, the game ends. If not, the cell shows the number of adjacent mines. If no adjacent mines are found, a flood-fill algorithm reveals neighboring cells.
  - **Right-Click:** Toggles a flag on a cell to mark it as suspected to contain a mine.

- **Game End Conditions:**  
  - **Game Over:** When a mine is revealed, all mines are displayed and a "Game Over" alert is shown.
  - **Win:** When all non-mine cells are revealed, a congratulatory message is displayed.

---

## Customization

- **Board Configuration:**  
  You can adjust the number of rows, columns, and mines by modifying the constants at the top of the file:
  ```java
  private static final int ROWS = 16;
  private static final int COLS = 16;
  private static final int MINES = 40;
  ```
- **Appearance:**  
  Modify the button sizes, fonts, and colors in the code to change the UI appearance.
- **Game Logic:**  
  The flood-fill algorithm and event handling are implemented in methods that can be extended to add features like timers, difficulty levels, or scoring.

---

## Contributing

Contributions are welcome! Feel free to fork this repository, make improvements, and submit pull requests. Whether it’s adding new features or improving the UI, your input is valuable.

---

## License

This project is licensed under the MIT License.

---

*Enjoy playing Minesweeper with JavaFX and happy coding!*
