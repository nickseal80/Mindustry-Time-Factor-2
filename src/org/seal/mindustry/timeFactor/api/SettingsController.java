package org.seal.mindustry.timeFactor.api;

public interface SettingsController {
    void loadSettings();
    void saveSettings();

    int getMinPos();
    void setMinPos(int minPos);
    int getMaxPos();
    void setMaxPos(int maxPos);
    void addListener(SettingsListener listener);
    void removeListener(SettingsListener listener);
}
