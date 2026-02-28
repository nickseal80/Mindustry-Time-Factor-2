package org.seal.mindustry.timeFactor;

import arc.files.Fi;
import arc.scene.ui.TextButton;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;
import arc.util.Log;

public class Settings {
    public static class BaseSettings {
        public int minPos = -4;
        public int maxPos = 4;
    }

    private BaseSettings settings;
    private final Json json; // Один экземпляр на весь класс
    private final BaseDialog dialog;
    private final TimeFactor mod;

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

        dialog = new BaseDialog("Settings");
        TextButton closeBtn = new TextButton("Close");
        closeBtn.clicked(dialog::hide);
        dialog.cont.add("Tutut").row();
        dialog.buttons.add(closeBtn).size(100, 50);
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

                // ✅ Используем this.json (с зарегистрированным сериализатором)
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

            // ✅ Используем this.json (с зарегистрированным сериализатором)
            String jsonStr = json.toJson(settings, BaseSettings.class);

            Log.info("Saving settings: " + jsonStr);
            file.writeString(jsonStr);

        } catch (Exception e) {
            Log.err("Error saving settings.", e);
        }
    }

    // Геттеры для доступа к настройкам
    public int getMinPos() { return settings.minPos; }
    public int getMaxPos() { return settings.maxPos; }

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