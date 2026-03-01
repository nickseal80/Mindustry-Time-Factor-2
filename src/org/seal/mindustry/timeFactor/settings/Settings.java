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
import org.seal.mindustry.timeFactor.PositionManager;
import org.seal.mindustry.timeFactor.SpeedSlider;
import org.seal.mindustry.timeFactor.TimeFactor;

public class Settings {
    public static class BaseSettings {
        public int minPos = -4;
        public int maxPos = 4;
    }

    private BaseSettings settings;
    private final Json json; // Один экземпляр на весь класс
    private final BaseDialog dialog;
    private final TimeFactor mod;
    private SpeedSlider slider;

    public Settings(TimeFactor mod) {
        this.json = new Json(); // Инициализируем один раз
        this.mod = mod;
        this.settings = new BaseSettings(); // Инициализируем

        // Регистрируем сериализатор в НАШЕМ экземпляре json
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

        loadSettings(); // Загружаем после регистрации сериализатора

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

    public void showDialog() {
        dialog.show();
    }

    public void closeDialog() {
        dialog.hide();
    }

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
            saveSettings(); // Сохраняем defaults
        }
    }

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

    public void setSlider(SpeedSlider slider) {
        this.slider = slider;
    }

    // Геттеры для доступа к настройкам
    public int getMinPos() {return settings.minPos;}

    public int getMaxPos() {return settings.maxPos;}

    public void setMinPos(int minPos) {
        if (settings.minPos != minPos) {
            settings.minPos = minPos;
            saveSettings();
        }
    }

    public void setMaxPos(int maxPos) {
        if (settings.maxPos != maxPos) {
            settings.maxPos = maxPos;
            saveSettings();
        }
    }
}