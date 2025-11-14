package com.unc.gdsc.minesweeper.javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * A status bar component for the Minesweeper JavaFX application.
 * <p>
 * Displays the number of remaining mines and elapsed time.
 * Provides methods to start/stop the timer and update mine count.
 * </p>
 *
 * @author UNC-CH Google Developer Student Club
 * @version 2.0
 */
public class StatusBarFX extends HBox {

    private static final String MINES_PREFIX = "Mines: ";
    private static final String TIME_PREFIX = "Time: ";
    private static final String TIME_SUFFIX = "s";

    private final Label minesLabel;
    private final Label timeLabel;
    private int minesRemaining;
    private int secondsElapsed;
    private Timeline timer;

    /**
     * Constructs a new StatusBarFX with the specified initial mine count.
     *
     * @param totalMines the total number of mines in the game
     */
    public StatusBarFX(int totalMines) {
        this.minesRemaining = totalMines;
        this.secondsElapsed = 0;

        setSpacing(20);
        setPadding(new Insets(5, 10, 5, 10));
        setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        minesLabel = new Label(MINES_PREFIX + minesRemaining);
        minesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #c00;");

        timeLabel = new Label(TIME_PREFIX + secondsElapsed + TIME_SUFFIX);
        timeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #005;");

        getChildren().addAll(minesLabel, timeLabel);
        initializeTimer();
    }

    /**
     * Initializes the timer that updates every second.
     */
    private void initializeTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            updateTimeLabel();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Starts the timer.
     */
    public void startTimer() {
        if (timer != null && timer.getStatus() != Timeline.Status.RUNNING) {
            timer.play();
        }
    }

    /**
     * Stops the timer.
     */
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Resets the status bar to initial state.
     *
     * @param totalMines the total number of mines for the new game
     */
    public void reset(int totalMines) {
        stopTimer();
        this.minesRemaining = totalMines;
        this.secondsElapsed = 0;
        updateMinesLabel();
        updateTimeLabel();
    }

    /**
     * Decrements the mines remaining count by one.
     */
    public void decrementMines() {
        if (minesRemaining > 0) {
            minesRemaining--;
            updateMinesLabel();
        }
    }

    /**
     * Increments the mines remaining count by one.
     */
    public void incrementMines() {
        minesRemaining++;
        updateMinesLabel();
    }

    /**
     * Updates the mines label display.
     */
    private void updateMinesLabel() {
        minesLabel.setText(MINES_PREFIX + minesRemaining);
    }

    /**
     * Updates the time label display.
     */
    private void updateTimeLabel() {
        timeLabel.setText(TIME_PREFIX + secondsElapsed + TIME_SUFFIX);
    }

    /**
     * Gets the elapsed time in seconds.
     *
     * @return seconds elapsed since timer started
     */
    public int getElapsedSeconds() {
        return secondsElapsed;
    }

    /**
     * Gets the number of mines remaining (according to flag count).
     *
     * @return mines remaining
     */
    public int getMinesRemaining() {
        return minesRemaining;
    }
}
