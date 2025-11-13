# Minesweeper Game

A classic Minesweeper game with both **Swing** and **JavaFX** implementations, featuring clean architecture, comprehensive testing, and modern Java development practices.

![Java](https://img.shields.io/badge/Java-11+-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

---

## Features

- 🎮 **Dual UI Implementations:** Choose between Swing or JavaFX
- 🏗️ **Clean Architecture:** Separation of concerns with core game logic independent of UI
- 🎯 **Classic Gameplay:** 16×16 grid with 40 mines
- 🖱️ **Interactive Controls:** Left-click to reveal, right-click to flag
- 🔄 **Flood-Fill Algorithm:** Automatically reveals adjacent empty cells
- ⏱️ **Timer & Status Bar:** Track elapsed time and remaining mines (JavaFX)
- 🎨 **Styled UI:** Custom CSS styling for JavaFX version
- ✅ **Comprehensive Testing:** JUnit 5 test suite with high coverage
- 📦 **Maven Build System:** Easy compilation and dependency management
- 📚 **Complete JavaDoc:** Full API documentation

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Building the Project](#building-the-project)
- [Running the Game](#running-the-game)
- [Testing](#testing)
- [Development](#development)
- [Contributing](#contributing)
- [License](#license)

---

## Prerequisites

- **Java Development Kit (JDK):** Version 11 or later
- **Maven:** Version 3.6 or later
- **JavaFX:** Automatically managed by Maven (for JavaFX version)

---

## Quick Start

```bash
# Clone the repository
git clone https://github.com/UNC-GDSC/Minesweeper-Game.git
cd Minesweeper-Game

# Build the project
mvn clean package

# Run the JavaFX version
mvn javafx:run

# Or run the Swing version
java -jar target/minesweeper-swing.jar
```

---

## Project Structure

```
Minesweeper-Game/
├── src/
│   ├── main/
│   │   ├── java/com/unc/gdsc/minesweeper/
│   │   │   ├── core/              # Core game logic (UI-independent)
│   │   │   │   ├── Cell.java      # Cell model
│   │   │   │   └── GameBoard.java # Game board logic
│   │   │   ├── swing/             # Swing implementation
│   │   │   │   └── MinesweeperSwing.java
│   │   │   └── javafx/            # JavaFX implementation
│   │   │       ├── MinesweeperFXApp.java
│   │   │       └── StatusBarFX.java
│   │   └── resources/
│   │       └── styles.css         # JavaFX CSS styling
│   └── test/
│       └── java/com/unc/gdsc/minesweeper/core/
│           ├── CellTest.java      # Cell unit tests
│           └── GameBoardTest.java # GameBoard unit tests
├── pom.xml                        # Maven configuration
├── .gitignore                     # Git ignore rules
├── LICENSE                        # MIT License
├── README.md                      # This file
└── CONTRIBUTING.md                # Contribution guidelines

```

---

## Building the Project

### Using Maven

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as JAR files
mvn package

# Generate JavaDoc
mvn javadoc:javadoc
```

### Build Artifacts

After running `mvn package`, you'll find:

- `target/minesweeper-swing.jar` - Standalone Swing version
- `target/minesweeper-javafx.jar` - Standalone JavaFX version
- `target/minesweeper-2.0.0.jar` - Main artifact

---

## Running the Game

### JavaFX Version (Recommended)

```bash
# Using Maven plugin
mvn javafx:run

# Or using the JAR file
java -jar target/minesweeper-javafx.jar
```

**Features:**
- Modern UI with gradient styling
- Integrated status bar with timer
- Mine counter updates with flags
- Smooth animations and alerts

### Swing Version

```bash
# Using Maven exec plugin
mvn exec:java -P swing

# Or using the JAR file
java -jar target/minesweeper-swing.jar
```

**Features:**
- Classic desktop look and feel
- Lightweight and fast
- Menu bar with New Game option
- Color-coded numbers

---

## Testing

Run the complete test suite:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=CellTest
mvn test -Dtest=GameBoardTest
```

Generate test coverage report (requires Jacoco plugin):

```bash
mvn jacoco:report
```

---

## How to Play

1. **Start the Game:** Launch either the Swing or JavaFX version
2. **Reveal Cells:** Left-click on a cell to reveal it
   - Numbers indicate how many mines are adjacent
   - Empty cells (no adjacent mines) auto-reveal neighbors
   - Mines end the game!
3. **Flag Mines:** Right-click to mark suspected mine locations
4. **Win Condition:** Reveal all non-mine cells
5. **New Game:** Use the Game menu or dialog prompt to start over

---

## Game Configuration

Both implementations use these default settings:

- **Rows:** 16
- **Columns:** 16
- **Mines:** 40
- **Difficulty:** Intermediate

To customize, modify the constants in the respective main classes:

```java
private static final int ROWS = 16;
private static final int COLS = 16;
private static final int MINES = 40;
```

---

## Development

### Code Organization

- **Core Package (`com.unc.gdsc.minesweeper.core`):** Contains game logic independent of UI framework
  - `Cell`: Represents a single grid cell
  - `GameBoard`: Manages game state, mine placement, and game rules

- **Swing Package (`com.unc.gdsc.minesweeper.swing`):** Swing-based UI implementation

- **JavaFX Package (`com.unc.gdsc.minesweeper.javafx`):** JavaFX-based UI implementation with enhanced features

### Design Patterns

- **Model-View Separation:** Core logic is completely independent of UI
- **Observer Pattern:** UI updates based on model state changes
- **Factory Pattern:** Cell creation and initialization
- **Strategy Pattern:** Different UI implementations share the same core

### Adding New Features

1. For game logic changes, modify the `core` package
2. For UI changes, modify the respective `swing` or `javafx` package
3. Always add corresponding unit tests
4. Update JavaDoc documentation

---

## Maven Profiles

The project includes two Maven profiles:

```bash
# Run JavaFX version (default)
mvn exec:java -P javafx

# Run Swing version
mvn exec:java -P swing
```

---

## Generating Documentation

Generate JavaDoc documentation:

```bash
mvn javadoc:javadoc
```

Documentation will be available in `target/site/apidocs/index.html`

---

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Quick Contribution Steps

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes and add tests
4. Commit with descriptive messages (`git commit -m 'Add amazing feature'`)
5. Push to your fork (`git push origin feature/amazing-feature`)
6. Open a Pull Request

---

## Architecture Highlights

### Separation of Concerns

```
┌─────────────────────────────────────┐
│     UI Layer (Swing/JavaFX)         │
│  - Event handling                   │
│  - Rendering                        │
│  - User interaction                 │
└──────────────┬──────────────────────┘
               │
               │ calls methods on
               ▼
┌─────────────────────────────────────┐
│     Core Layer (Game Logic)         │
│  - Cell management                  │
│  - Mine placement                   │
│  - Win/loss detection               │
│  - Flood-fill algorithm             │
└─────────────────────────────────────┘
```

### Benefits

- ✅ Easy to add new UI frameworks
- ✅ Core logic can be tested without UI
- ✅ Changes to one layer don't affect the other
- ✅ Reusable game engine

---

## Troubleshooting

### JavaFX Not Found

If you encounter JavaFX module errors:

```bash
# Ensure you're using Java 11+
java -version

# Use Maven to run (handles modules automatically)
mvn javafx:run
```

### Build Failures

```bash
# Clean Maven cache
mvn clean

# Delete target directory
rm -rf target/

# Rebuild
mvn install
```

---

## Roadmap

- [ ] Difficulty levels (Beginner, Intermediate, Expert)
- [ ] High score tracking with persistence
- [ ] Customizable themes
- [ ] Sound effects and animations
- [ ] Mobile version (Android/iOS)
- [ ] Online multiplayer mode
- [ ] Statistics and analytics

---

## Credits

**Developed by:** UNC-CH Google Developer Student Club
**License:** MIT License
**Year:** 2025

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 UNC-CH Google Developer Student Club

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

---

## Contact & Support

- **Issues:** [GitHub Issues](https://github.com/UNC-GDSC/Minesweeper-Game/issues)
- **Discussions:** [GitHub Discussions](https://github.com/UNC-GDSC/Minesweeper-Game/discussions)
- **GDSC Website:** [gdsc.community.dev/university-of-north-carolina-chapel-hill/](https://gdsc.community.dev/university-of-north-carolina-chapel-hill/)

---

**Happy Mining! 💣**
