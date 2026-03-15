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
import org.seal.mindustry.timeFactor.api.PositionController;
import org.seal.mindustry.timeFactor.api.SettingsController;
import org.seal.mindustry.timeFactor.model.PositionManager;
import org.seal.mindustry.timeFactor.model.Settings;
import org.seal.mindustry.timeFactor.model.TimeSliderModel;
import org.seal.mindustry.timeFactor.ui.SettingsDialog;
import org.seal.mindustry.timeFactor.ui.TimeSliderView;
import org.seal.mindustry.timeFactor.util.TimeSpeedFormat;

/**
 * Main mod class for Time Factor - a Mindustry mod that allows real-time speed control.
 * Adds a UI slider to adjust game speed from 1/16x to 16x.
 *
 * @author Seal
 * @version 2.0
 */
public class TimeFactor extends Mod {

    private PositionController positionController;
    private SettingsController settingsController;
    private TimeSliderModel sliderModel;
    private TimeSliderView sliderView;
    private SettingsDialog settingsDialog;

    /**
     * Updates the game's delta time provider based on the current speed position.
     * The delta is capped to prevent physics issues at extreme speeds.
     */
    private void updateGameSpeed(int position) {
        float speed = TimeSpeedFormat.getMultiplier(position);
        Time.setDeltaProvider(() -> {
            float delta = Core.graphics.getDeltaTime();
            return Math.min(delta * 60 * speed, 3 * speed);
        });
    }

    /**
     * Resets the game speed to normal (1x) and sets position to 0.
     */
    private void reset() {
        Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60, 3));
        positionController.setPosition(0);
    }

    /**
     * Initializes all controllers and models.
     */
    private void initControllers() {
        // Create controllers
        positionController = new PositionManager();
        settingsController = new Settings(this);

        // Create slider model with initial values
        sliderModel = new TimeSliderModel(
                positionController.getPosition(),
                settingsController.getMinPos(),
                settingsController.getMaxPos()
        );

        // Connect slider model to position controller
        sliderModel.addListener(value -> {
            positionController.setPosition((int) value);
        });

        // Update slider model when position changes externally
        positionController.addListener(newPosition -> {
            sliderModel.setValue(newPosition);
        });

        // ✅ ВАЖНО: Update slider model when settings change
        settingsController.addListener(settings -> {
            sliderModel.setRange(settings.minPos, settings.maxPos);
        });

        // Create UI components
        sliderView = new TimeSliderView(sliderModel);
        settingsDialog = new SettingsDialog(settingsController);
    }

    /**
     * Creates and initializes the user interface.
     */
    private void createUI() {
        Table main = new Table();

        main.table(Tex.buttonEdge3, panel -> {
                    panel.name = "time-control-ui";

                    // Settings button
                    ImageButton settingsBtn = new ImageButton(Icon.settings);
                    settingsBtn.clicked(() -> {
                        settingsDialog.showDialog();
                        Log.info("Opening settings dialog");
                    });
                    styleButton(settingsBtn);

                    // Speed display label
                    Label label = new Label(positionController.pos2str());
                    label.setAlignment(Align.center);

                    // Update label when position changes
                    positionController.addListener(position -> {
                        label.setText(positionController.pos2str());
                    });

                    // Reset button
                    ImageButton resetBtn = new ImageButton(Icon.refresh);
                    resetBtn.clicked(this::reset);
                    styleButton(resetBtn);

                    // Add components to panel
                    panel.add(settingsBtn).size(40, 40).padRight(5);
                    panel.add(label).size(60, 40).pad(5).padRight(5);
                    panel.add(resetBtn).size(40, 40).padRight(5);

                    // Add slider view
                    panel.add(sliderView.render()).pad(5);
                })
                .pad(10)
                .size(440, 60); // Slightly larger for settings button

        main.left().bottom();
        Vars.ui.hudGroup.addChild(main);

        Log.info("TimeFactor UI created successfully");
    }

    /**
     * Applies consistent styling to buttons.
     */
    private void styleButton(ImageButton button) {
        button.getStyle().up = Tex.pane;
        button.getStyle().over = Tex.flatDownBase;
        button.getStyle().down = Tex.whitePane;
    }

    /**
     * Initializes the mod after game load.
     */
    @Override
    public void init() {
        // Initialize all components
        initControllers();

        // Connect position controller to game speed
        positionController.addListener(this::updateGameSpeed);

        // Schedule UI creation on main thread
        Core.app.post(() -> {
            if (Vars.ui != null && Vars.ui.hudGroup != null) {
                createUI();
            }
        });

        Log.info("TimeFactor mod initialized");
    }
}