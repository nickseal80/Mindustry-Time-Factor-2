package org.seal.mindustry.timeFactor;

import arc.Core;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.mod.Mod;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Main mod class for Time Factor - a Mindustry mod that allows real-time speed control.
 * Adds a UI slider to adjust game speed from 1/16x to 16x.
 *
 * @author Seal
 * @version 1.0
 */
public class TimeFactor extends Mod {

    /** Current speed position/index. Range: -4 (slowest) to 4 (fastest), 0 = normal speed. */
    private int position = 0;

    /** Thread-safe list of listeners that observe position changes. */
    private final List<PositionListener> listeners = new CopyOnWriteArrayList<>();

    /** The speed slider UI component. */
    private SpeedSlider slider;

    /**
     * Registers a listener to be notified when the speed position changes.
     *
     * @param listener The callback that will receive the new position value
     * @see PositionListener
     */
    public void onPositionChange(PositionListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters a previously registered position listener.
     *
     * @param listener The listener to remove
     */
    public void removeListener(PositionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners about the current position change.
     * Catches and logs individual listener errors to prevent one faulty listener
     * from breaking the notification chain.
     */
    private void notifyListeners() {
        for (PositionListener listener : listeners) {
            try {
                listener.onPositionChanged(position);
            } catch (Exception e) {
                Log.err("Error in position listener:", e);
            }
        }
    }

    /**
     * Converts the current position value to a human-readable string representation.
     *
     * @return Formatted speed string (e.g., "x1", "x4", "x1/2")
     */
    private String pos2str() {
        if (position >= 0) {
            return "x" + (int) Math.pow(2, position);
        } else {
            return "x1/" + (int) Math.pow(2, Math.abs(position));
        }
    }

    /**
     * Sets a new speed position and triggers all listeners and game speed update.
     *
     * @param pos The new position value (must be between -4 and 4)
     */
    private void setPosition(int pos) {
        this.position = pos;
        notifyListeners();
        updateGameSpeed();
    }

    /**
     * Updates the game's delta time provider based on the current speed position.
     * The delta is capped to prevent physics issues at extreme speeds.
     */
    private void updateGameSpeed() {
        float speed = (float) Math.pow(2, position);
        Time.setDeltaProvider(() -> {
            float delta = Core.graphics.getDeltaTime();
            return Math.min(delta * 60 * speed, 3 * speed);
        });
    }

    /**
     * Resets the game speed to normal (1x) and sets position to 0.
     * Restores the default delta provider behavior.
     */
    private void reset() {
        Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60, 3));
        position = 0;
        notifyListeners();
    }

    /**
     * Creates and initializes the user interface for speed control.
     * Sets up the label, reset button, and speed slider.
     * Positions the UI in the bottom-left corner of the screen.
     */
    private void createUI() {
        slider = new SpeedSlider(position);
        slider.setListener(value -> {
            position = (int) value;
            updateGameSpeed();
            notifyListeners();
        });

        onPositionChange(newPosition -> {
            slider.getSlider().setValue(newPosition);
        });

        Table main = new Table();

        main.table(Tex.buttonEdge3, panel -> {
                    panel.name = "time-control-ui";

                    // Speed display label
                    Label label = new Label(pos2str());
                    label.setAlignment(Align.center);

                    onPositionChange((newPosition) -> {
                        label.setText(pos2str());
                    });

                    // Reset button with refresh icon
                    ImageButton resetBtn = new ImageButton(Icon.refresh);
                    resetBtn.clicked(this::reset);
                    resetBtn.getStyle().up = Tex.pane;
                    resetBtn.getStyle().over = Tex.flatDownBase;
                    resetBtn.getStyle().down = Tex.whitePane;

                    // Add components to panel
                    panel.add(label).size(60, 40).pad(5).padRight(15);
                    panel.add(resetBtn).size(40, 40).padRight(5);

                    // Add slider table
                    panel.add(slider.render()).pad(5);
                })
                .pad(10)
                .size(380, 60);

        main.left().bottom();
        Vars.ui.hudGroup.addChild(main);

        Log.info("Creating TimeFactor");
    }

    /**
     * Initializes the mod after game load.
     * Posts the UI creation task to the main thread to ensure UI components are ready.
     */
    @Override
    public void init() {
        Core.app.post(() -> {
            if (Vars.ui != null && Vars.ui.hudGroup != null) {
                createUI();
            }
        });
    }
}