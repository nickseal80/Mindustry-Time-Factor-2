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
import org.seal.mindustry.timeFactor.settings.Settings;

/**
 * Main mod class for Time Factor - a Mindustry mod that allows real-time speed control.
 * Adds a UI slider to adjust game speed from 1/16x to 16x.
 *
 * @author Seal
 * @version 1.0
 */
public class TimeFactor extends Mod {

    private PositionManager positionManager;

    /** The speed slider UI component. */
    private SpeedSlider slider;

    /**
     * Updates the game's delta time provider based on the current speed position.
     * The delta is capped to prevent physics issues at extreme speeds.
     */
    private void updateGameSpeed(int position) {
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
        positionManager.setPosition(0);
    }

    /**
     * Creates and initializes the user interface for speed control.
     * Sets up the label, reset button, and speed slider.
     * Positions the UI in the bottom-left corner of the screen.
     */
    private void createUI() {
        Settings settings = new Settings(TimeFactor.this);

        slider = new SpeedSlider(positionManager.getPosition(), settings.getMinPos(), settings.getMaxPos());
        slider.setListener(value -> {
            positionManager.setPosition((int)value);
        });
        settings.setSlider(slider);

        positionManager.addListener(newPosition -> {
            slider.getSlider().setValue(newPosition);
        });

        Table main = new Table();

        main.table(Tex.buttonEdge3, panel -> {
                    panel.name = "time-control-ui";

                    ImageButton settingsBtn = new ImageButton(Icon.settings);
                    settingsBtn.clicked(() -> {
                        settings.showDialog();
                        Log.info("Setting up time-control ui");
                    });
                    settingsBtn.getStyle().up = Tex.pane;
                    settingsBtn.getStyle().over = Tex.flatDownBase;
                    settingsBtn.getStyle().down = Tex.whitePane;

                    // Speed display label
                    Label label = new Label(positionManager.pos2str());
                    label.setAlignment(Align.center);

                    positionManager.addListener(position -> {
                        label.setText(positionManager.pos2str());
                    });

                    // Reset button with refresh icon
                    ImageButton resetBtn = new ImageButton(Icon.refresh);
                    resetBtn.clicked(this::reset);
                    resetBtn.getStyle().up = Tex.pane;
                    resetBtn.getStyle().over = Tex.flatDownBase;
                    resetBtn.getStyle().down = Tex.whitePane;

                    // Add components to panel
                    panel.add(settingsBtn).size(40, 40).padRight(5);
                    panel.add(label).size(60, 40).pad(5).padRight(5);
                    panel.add(resetBtn).size(40, 40).padRight(5);

                    // Add slider table
                    panel.add(slider.render()).pad(5);
                })
                .pad(10)
                .size(420, 60);

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
        positionManager = new PositionManager();

        positionManager.addListener(this::updateGameSpeed);

        Core.app.post(() -> {
            if (Vars.ui != null && Vars.ui.hudGroup != null) {
                createUI();
            }
        });
    }
}