import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * A horizontal status bar showing remaining mines and elapsed time.
 * <p>
 * Call {@link #startTimer()} when the first cell is revealed,
 * {@link #stopTimer()} and {@link #revealAllMines()} on game end,
 * and {@link #reset(int)} when restarting.
 * </p>
 */
public class StatusBar extends HBox {
    private final Label minesLabel = new Label();
    private final Label timeLabel  = new Label();
    private int minesRemaining;
    private int secondsElapsed;
    private Timeline timer;

    /**
     * @param totalMines initial number of mines to display
     */
    public StatusBar(int totalMines) {
        this.minesRemaining = totalMines;
        this.secondsElapsed = 0;
        setSpacing(20);
        setPadding(new Insets(5));
        minesLabel.setText("Mines: " + minesRemaining);
        timeLabel.setText("Time: 0s");
        getChildren().addAll(minesLabel, timeLabel);
        initTimer();
    }

    /** Configure the JavaFX Timeline to tick every second. */
    private void initTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timeLabel.setText("Time: " + secondsElapsed + "s");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    /** Begin counting elapsed seconds. */
    public void startTimer() {
        if (timer != null && timer.getStatus() != Timeline.Status.RUNNING) {
            timer.play();
        }
    }

    /** Pause the timer. */
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Reset both the mine counter and the timer.
     * @param totalMines the new total mines to track
     */
    public void reset(int totalMines) {
        stopTimer();
        this.minesRemaining = totalMines;
        this.secondsElapsed = 0;
        minesLabel.setText("Mines: " + minesRemaining);
        timeLabel.setText("Time: 0s");
    }

    /** Decrease the “mines remaining” count by one. */
    public void decrementMines() {
        if (minesRemaining > 0) {
            minesRemaining--;
            minesLabel.setText("Mines: " + minesRemaining);
        }
    }

    /** Increase the “mines remaining” count by one. */
    public void incrementMines() {
        minesRemaining++;
        minesLabel.setText("Mines: " + minesRemaining);
    }

    /** @return how many seconds have elapsed since the timer started */
    public int getElapsedSeconds() {
        return secondsElapsed;
    }

    /** @return how many mines are still “unflagged” according to the bar */
    public int getMinesRemaining() {
        return minesRemaining;
    }
}
