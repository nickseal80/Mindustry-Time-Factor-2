package org.seal.mindustry.timeFactor.api;

import org.seal.mindustry.timeFactor.model.Settings;

public interface SettingsController {
    void loadSettings();
    void saveSettings();

    Settings.BaseSettings getSettings();
    int getMinPos();
    void setMinPos(int minPos);
    int getMaxPos();
    void setMaxPos(int maxPos);
    boolean isShowTooltips();
    void setShowTooltips(boolean showTooltips);
    void addListener(SettingsListener listener);
    void removeListener(SettingsListener listener);
}
