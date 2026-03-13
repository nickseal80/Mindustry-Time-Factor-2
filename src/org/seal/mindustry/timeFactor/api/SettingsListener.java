package org.seal.mindustry.timeFactor.api;

import org.seal.mindustry.timeFactor.model.Settings;

public interface SettingsListener {
    void onSettingsChanged(Settings.BaseSettings settings);
}
