package org.seal.mindustry.timeFactor.api;

public interface PositionController {
    int getPosition();
    void setPosition(int position);

    void addListener(PositionListener listener);
    void removeListener(PositionListener listener);
    void clearListeners();

    String pos2str();
}
