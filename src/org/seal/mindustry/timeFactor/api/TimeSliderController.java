package org.seal.mindustry.timeFactor.api;

public interface TimeSliderController {
    int getValue();
    void setValue(int value);

    int getMinValue();
    int getMaxValue();
    void setRange(int min, int max);

    void addListener(SliderListener listener);
    void removeListener(SliderListener listener);
}
