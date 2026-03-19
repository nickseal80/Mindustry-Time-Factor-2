package org.seal.mindustry.timeFactor.model;

import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import org.seal.mindustry.timeFactor.TimeFactor;
import org.seal.mindustry.timeFactor.api.SettingsController;
import org.seal.mindustry.timeFactor.api.SettingsListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Settings implements SettingsController {
    public static class BaseSettings {
        /** Minimum speed position (negative = slower speeds) */
        public int minPos = -4;

        /** Maximum speed position (positive = faster speeds) */
        public int maxPos = 4;

        public boolean showTooltips = true;
    }

    private BaseSettings settings;
    private final Json json;
    private final TimeFactor mod;
    private final List<SettingsListener> listeners = new CopyOnWriteArrayList<>();

    public Settings(TimeFactor mod) {
        this.json = new Json();
        this.mod = mod;
        this.settings = new BaseSettings();

        // Configure JSON serializer for BaseSettings - ИСПРАВЛЕНО: убраны лишние package paths
        json.setSerializer(BaseSettings.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, BaseSettings object, Class knownType) {
                json.writeObjectStart();
                json.writeValue("minPos", object.minPos);
                json.writeValue("maxPos", object.maxPos);
                json.writeValue("showTooltips", object.showTooltips);
                json.writeObjectEnd();
            }

            @Override
            public BaseSettings read(Json json, JsonValue jsonValue, Class type) {
                BaseSettings settings = new BaseSettings();
                settings.minPos = json.readValue("minPos", Integer.class, jsonValue);
                settings.maxPos = json.readValue("maxPos", Integer.class, jsonValue);
                settings.showTooltips = json.readValue("showTooltips", Boolean.class, jsonValue);
                return settings;
            }
        });

        loadSettings();
    }

    private void notifyListeners() {
        for (SettingsListener listener : listeners) {
            try {
                listener.onSettingsChanged(settings);
            } catch (Exception e) {
                Log.err(e);
            }
        }
    }

    /**
     * Get current settings object for listeners
     */
    @Override
    public BaseSettings getSettings() {
        return settings;
    }

    @Override
    public void loadSettings() {
        Fi file = Vars.mods.getConfig(mod);
        if (file.exists()) {
            try {
                String jsonStr = file.readString();
                Log.info("Loading settings: " + jsonStr);
                settings = json.fromJson(BaseSettings.class, jsonStr);
                notifyListeners(); // Уведомляем после загрузки
            } catch (Exception e) {
                Log.err("Failed to load settings", e);
                settings = new BaseSettings();
            }
        } else {
            Log.info("No settings file, using defaults");
            settings = new BaseSettings();
            saveSettings();
            notifyListeners(); // Уведомляем после создания дефолтных
        }
    }

    @Override
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

    @Override
    public int getMinPos() {
        return settings.minPos;
    }

    @Override
    public void setMinPos(int minPos) {
        if (settings.minPos != minPos) {
            settings.minPos = minPos;
            saveSettings();
            notifyListeners();
        }
    }

    @Override
    public int getMaxPos() {
        return settings.maxPos;
    }

    @Override
    public void setMaxPos(int maxPos) {
        if (settings.maxPos != maxPos) {
            settings.maxPos = maxPos;
            saveSettings();
            notifyListeners();
        }
    }

    @Override
    public boolean isShowTooltips() {
        return settings.showTooltips;
    }

    @Override
    public void setShowTooltips(boolean showTooltips) {
        if (settings.showTooltips != showTooltips) {
            settings.showTooltips = showTooltips;
            saveSettings();
            notifyListeners();
        }
    }

    @Override
    public void addListener(SettingsListener listener) {
        listeners.add(listener);
        // Сразу уведомляем нового слушателя о текущем состоянии
        listener.onSettingsChanged(settings);
    }

    @Override
    public void removeListener(SettingsListener listener) {
        listeners.remove(listener);
    }
}