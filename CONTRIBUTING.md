# Contributing to Minesweeper Game

Thank you for your interest in contributing to the Minesweeper Game project! We welcome contributions from everyone. This document provides guidelines for contributing to this project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Enhancements](#suggesting-enhancements)

---

## Code of Conduct

This project follows the principles of respect, inclusivity, and collaboration. By participating, you are expected to uphold these values. Please be respectful in all communications and interactions.

---

## Getting Started

### Prerequisites

Before contributing, ensure you have:

1. **Java Development Kit (JDK) 11 or later**
2. **Maven 3.6 or later**
3. **Git** for version control
4. A **GitHub account**
5. Your favorite **Java IDE** (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Setting Up Your Development Environment

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Minesweeper-Game.git
   cd Minesweeper-Game
   ```
3. **Add the upstream repository**:
   ```bash
   git remote add upstream https://github.com/UNC-GDSC/Minesweeper-Game.git
   ```
4. **Build the project**:
   ```bash
   mvn clean install
   ```
5. **Run the tests**:
   ```bash
   mvn test
   ```

---

## How to Contribute

There are many ways to contribute:

- 🐛 **Report bugs** - Help us identify issues
- 💡 **Suggest features** - Share your ideas for improvements
- 📝 **Improve documentation** - Fix typos, clarify explanations
- 🧪 **Write tests** - Increase code coverage
- 🎨 **Improve UI/UX** - Enhance the visual design
- 💻 **Write code** - Implement new features or fix bugs
- 🔍 **Review code** - Help review pull requests

---

## Development Workflow

### 1. Create a Branch

Always create a new branch for your work:

```bash
# Update your main branch
git checkout main
git pull upstream main

# Create a feature branch
git checkout -b feature/your-feature-name

# Or for bug fixes
git checkout -b fix/bug-description
```

### 2. Make Your Changes

- Write clean, readable code
- Follow the existing code style
- Add tests for new functionality
- Update documentation as needed

### 3. Test Your Changes

```bash
# Run all tests
mvn test

# Run specific tests
mvn test -Dtest=ClassName

# Test the Swing version
mvn exec:java -P swing

# Test the JavaFX version
mvn javafx:run
```

### 4. Commit Your Changes

See [Commit Messages](#commit-messages) section for guidelines.

```bash
git add .
git commit -m "feat: add new feature description"
```

### 5. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

### 6. Create a Pull Request

Go to GitHub and create a pull request from your branch to the main repository.

---

## Coding Standards

### Java Code Style

We follow standard Java coding conventions:

#### Naming Conventions

- **Classes**: PascalCase (e.g., `GameBoard`, `MinesweeperSwing`)
- **Methods**: camelCase (e.g., `revealCell`, `toggleFlag`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `ROWS`, `TOTAL_MINES`)
- **Variables**: camelCase (e.g., `cellsRevealed`, `gameOver`)
- **Packages**: lowercase (e.g., `com.unc.gdsc.minesweeper.core`)

#### Code Formatting

- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Maximum 120 characters
- **Braces**: Opening brace on same line (K&R style)
  ```java
  if (condition) {
      // code
  }
  ```
- **Blank Lines**: Use blank lines to separate logical sections

#### JavaDoc

All public classes and methods must have JavaDoc comments:

```java
/**
 * Reveals the cell at the specified position.
 * <p>
 * If the cell contains a mine, the game ends. If the cell has no adjacent
 * mines, this method recursively reveals neighboring cells.
 * </p>
 *
 * @param row the row index of the cell
 * @param col the column index of the cell
 * @return true if the cell was revealed, false otherwise
 */
public boolean revealCell(int row, int col) {
    // implementation
}
```

### Package Organization

```
com.unc.gdsc.minesweeper/
├── core/          # Game logic (UI-independent)
├── swing/         # Swing UI implementation
├── javafx/        # JavaFX UI implementation
└── utils/         # Utility classes (if needed)
```

### Design Principles

- **Separation of Concerns**: Keep UI separate from game logic
- **Single Responsibility**: Each class should have one clear purpose
- **DRY (Don't Repeat Yourself)**: Avoid code duplication
- **KISS (Keep It Simple)**: Prefer simple, readable solutions

---

## Testing Guidelines

### Writing Tests

- Use **JUnit 5** for all tests
- Aim for **high code coverage** (target: 80%+)
- Test **edge cases** and **error conditions**
- Keep tests **independent** and **repeatable**

### Test Structure

```java
@Test
void testMethodName() {
    // Arrange: Set up test data
    GameBoard board = new GameBoard(8, 8, 10);

    // Act: Perform the action
    boolean result = board.revealCell(0, 0);

    // Assert: Verify the outcome
    assertTrue(result, "Cell should be revealed");
}
```

### Test Categories

- **Unit Tests**: Test individual methods in isolation
- **Integration Tests**: Test interactions between components
- **Parameterized Tests**: Test multiple inputs with same logic

Example:
```java
@ParameterizedTest
@CsvSource({
    "0, 0, true",
    "-1, 0, false",
    "8, 8, false"
})
void testIsValidCell(int row, int col, boolean expected) {
    assertEquals(expected, board.isValidCell(row, col));
}
```

---

## Commit Messages

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, no logic change)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks (dependencies, build, etc.)
- **perf**: Performance improvements

### Examples

```bash
feat(core): add difficulty level selection

Add support for Beginner, Intermediate, and Expert difficulty levels
with different board sizes and mine counts.

Closes #42

---

fix(javafx): resolve timer not stopping on game over

The timer continued running after the game ended. Fixed by calling
stopTimer() in the endGame() method.

Fixes #56

---

docs(readme): update installation instructions

Added troubleshooting section for JavaFX module errors.

---

test(core): increase GameBoard test coverage

Added tests for edge cases in revealCell and toggleFlag methods.
```

### Guidelines

- Use **imperative mood** ("add" not "added" or "adds")
- First line max **72 characters**
- Capitalize first letter
- No period at the end of subject
- Separate subject from body with blank line
- Explain **what** and **why**, not **how**

---

## Pull Request Process

### Before Submitting

- ✅ All tests pass (`mvn test`)
- ✅ Code follows style guidelines
- ✅ JavaDoc is complete and accurate
- ✅ No merge conflicts with main branch
- ✅ Commits are well-formed and descriptive

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe how you tested your changes

## Checklist
- [ ] My code follows the style guidelines
- [ ] I have performed a self-review
- [ ] I have commented my code where necessary
- [ ] I have updated the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix/feature works
- [ ] New and existing tests pass locally

## Screenshots (if applicable)
Add screenshots for UI changes

## Related Issues
Closes #(issue number)
```

### Review Process

1. At least one maintainer will review your PR
2. Address any requested changes
3. Once approved, a maintainer will merge your PR
4. Your contribution will be included in the next release!

---

## Reporting Bugs

### Before Reporting

1. **Search existing issues** to avoid duplicates
2. **Update to latest version** to see if issue persists
3. **Collect relevant information** (OS, Java version, error logs)

### Bug Report Template

```markdown
**Describe the bug**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What you expected to happen.

**Screenshots**
If applicable, add screenshots.

**Environment:**
 - OS: [e.g., Windows 10, macOS 12, Ubuntu 22.04]
 - Java Version: [e.g., 11.0.12]
 - Maven Version: [e.g., 3.8.4]
 - UI Framework: [Swing or JavaFX]

**Additional context**
Any other relevant information.
```

---

## Suggesting Enhancements

We welcome feature suggestions! Please:

1. **Check existing issues** for similar suggestions
2. **Provide clear use case** - Why is this useful?
3. **Describe the solution** - How should it work?
4. **Consider alternatives** - Are there other approaches?

### Enhancement Template

```markdown
**Is your feature request related to a problem?**
A clear description of the problem.

**Describe the solution you'd like**
A clear description of what you want to happen.

**Describe alternatives you've considered**
Other solutions or features you've considered.

**Additional context**
Any other context, mockups, or examples.
```

---

## Questions?

If you have questions about contributing:

1. Check the [README.md](README.md)
2. Look at [existing issues](https://github.com/UNC-GDSC/Minesweeper-Game/issues)
3. Open a [discussion](https://github.com/UNC-GDSC/Minesweeper-Game/discussions)
4. Contact the maintainers

---

## Recognition

Contributors will be recognized in:
- Release notes
- Contributors list
- Project documentation

Thank you for contributing to Minesweeper Game! 🎮

---

**Happy Coding!** 💻
