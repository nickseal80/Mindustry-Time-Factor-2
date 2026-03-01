package org.seal.mindustry.timeFactor.settings;

import arc.files.Fi;
import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.util.Align;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;
import arc.util.Log;
import org.seal.mindustry.timeFactor.SpeedSlider;
import org.seal.mindustry.timeFactor.TimeFactor;

/**
 * Manages mod settings including min/max speed range.
 * Handles persistent storage of settings using JSON.
 * Provides a configuration dialog for user interaction.
 *
 * @author Seal
 * @version 1.0
 */
public class Settings {

    /**
     * Internal class representing the settings data structure.
     */
    public static class BaseSettings {
        /** Minimum speed position (negative = slower speeds) */
        public int minPos = -4;

        /** Maximum speed position (positive = faster speeds) */
        public int maxPos = 4;
    }

    private BaseSettings settings;
    private final Json json; // Один экземпляр на весь класс
    private final BaseDialog dialog;
    private final TimeFactor mod;
    private SpeedSlider slider;

    /**
     * Creates a new Settings instance for the specified mod.
     * Loads existing settings and creates the configuration dialog.
     *
     * @param mod The main mod instance
     */
    public Settings(TimeFactor mod) {
        this.json = new Json();
        this.mod = mod;
        this.settings = new BaseSettings();

        // Configure JSON serializer for BaseSettings
        json.setSerializer(BaseSettings.class, new Json.Serializer<BaseSettings>() {
            @Override
            public void write(Json json, BaseSettings object, Class knownType) {
                json.writeObjectStart();
                json.writeValue("minPos", object.minPos);
                json.writeValue("maxPos", object.maxPos);
                json.writeObjectEnd();
            }

            @Override
            public BaseSettings read(Json json, JsonValue jsonValue, Class type) {
                BaseSettings settings = new BaseSettings();
                settings.minPos = json.readValue("minPos", Integer.class, jsonValue);
                settings.maxPos = json.readValue("maxPos", Integer.class, jsonValue);
                return settings;
            }
        });

        loadSettings();

        /*
         * Creates the settings dialog with min/max speed controls.
         * TODO: set createUI() method
         */
        TFSlider minValSlider = new TFSlider(-6, 0);
        minValSlider.setValue(settings.minPos);
        Label minValLabel = new Label(minValSlider.getSpeed());
        minValLabel.setAlignment(Align.center);
        minValSlider.moved(value -> {
            minValLabel.setText(minValSlider.getSpeed());
            settings.minPos = (int) value;
        });

        TFSlider maxValSlider = new TFSlider(0, 6);
        maxValSlider.setValue(settings.maxPos);
        Label maxValLabel = new Label(maxValSlider.getSpeed());
        maxValLabel.setAlignment(Align.center);
        maxValSlider.moved(value -> {
            maxValLabel.setText(maxValSlider.getSpeed());
            settings.maxPos = (int) value;
        });

        dialog = new BaseDialog("Settings");
        dialog.cont.table(grid -> {
            grid.defaults().pad(5);

            // min value
            grid.add(minValSlider).left();
            grid.add(minValLabel).width(60).right();
            grid.row();

            // max value
            grid.add(maxValSlider).left();
            grid.add(maxValLabel).width(60).right();
            grid.row();

            TextButton okBtn = new TextButton("Ok");
            okBtn.clicked(() -> {
                saveSettings();
                slider.setRange(settings.minPos, settings.maxPos);
                dialog.hide();
            });

            TextButton cancelBtn = new TextButton("Cancel");
            cancelBtn.clicked(dialog::hide);

            dialog.buttons.add(okBtn).width(120);
            dialog.buttons.add(cancelBtn).width(120);
        });
    }

    /**
     * Displays the settings dialog.
     */
    public void showDialog() {
        dialog.show();
    }

    /**
     * Closes the settings dialog.
     */
    public void closeDialog() {
        dialog.hide();
    }

    /**
     * Loads settings from persistent storage.
     * If no settings file exists, creates default settings.
     */
    public void loadSettings() {
        Fi file = Vars.mods.getConfig(mod);
        if (file.exists()) {
            try {
                String jsonStr = file.readString();
                Log.info("Loading settings: " + jsonStr);

                settings = json.fromJson(BaseSettings.class, jsonStr);
            } catch (Exception e) {
                Log.err("Failed to load settings", e);
                settings = new BaseSettings();
            }
        } else {
            Log.info("No settings file, using defaults");
            settings = new BaseSettings();
            saveSettings();
        }
    }

    /**
     * Saves current settings to persistent storage.
     */
    public void saveSettings() {
        try {
            Fi file = Vars.mods.getConfig(mod);

            String jsonStr = json.toJson(settings, BaseSettings.class);

            Log.info("Saving settings: " + jsonStr);
            file.writeString(jsonStr);

        } catch (Exception e) {
            Log.err("Error saving settings.", e);
        }
    }

    /**
     * Sets the main speed slider to update when settings change.
     *
     * @param slider The main speed slider instance
     */
    public void setSlider(SpeedSlider slider) {
        this.slider = slider;
    }

    /** @return The current minimum position value */
    public int getMinPos() {return settings.minPos;}

    /** @return The current maximum position value */
    public int getMaxPos() {return settings.maxPos;}

    /**
     * Sets the minimum position and saves settings.
     *
     * @param minPos New minimum position value
     */
    public void setMinPos(int minPos) {
        if (settings.minPos != minPos) {
            settings.minPos = minPos;
            saveSettings();
        }
    }

    /**
     * Sets the maximum position and saves settings.
     *
     * @param maxPos New maximum position value
     */
    public void setMaxPos(int maxPos) {
        if (settings.maxPos != maxPos) {
            settings.maxPos = maxPos;
            saveSettings();
        }
    }
}