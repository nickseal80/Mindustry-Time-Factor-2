package org.seal.mindustry.timeFactor.model;

import org.seal.mindustry.timeFactor.api.SliderListener;
import org.seal.mindustry.timeFactor.api.TimeSliderController;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TimeSliderModel implements TimeSliderController {
    private int value;
    private int minValue;
    private int maxValue;
    private final List<SliderListener> listeners = new CopyOnWriteArrayList<>();

    public TimeSliderModel(int value, int minValue, int maxValue) {
        validateRange(minValue, maxValue);
        validateValue(value, minValue, maxValue);

        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        if (this.value != value && value >= minValue && value <= maxValue) {
            this.value = value;
            notifyListeners();
        }
    }

    @Override
    public int getMinValue() {
        return minValue;
    }

    @Override
    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public void setRange(int min, int max) {
        validateRange(min, max);

        boolean rangeChanged = this.minValue != min || this.maxValue != max;

        this.minValue = min;
        this.maxValue = max;

        // Adjust current value if it's out of new range
        int oldValue = this.value;
        if (value < min) {
            this.value = min;
        } else if (value > max) {
            this.value = max;
        }

        // Уведомляем слушателей, если изменилось значение или диапазон
        if (rangeChanged || oldValue != this.value) {
            notifyListeners();
        }
    }

    @Override
    public void addListener(SliderListener listener) {
        listeners.add(listener);
        // Immediately notify new listener of current value
        listener.onValueChanged(value);
    }

    @Override
    public void removeListener(SliderListener listener) {
        listeners.remove(listener);
    }

    private void validateRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(String.format("min: %d > max: %d", min, max));
        }
    }

    private void validateValue(int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("value out of range");
        }
    }

    private void notifyListeners() {
        for (SliderListener listener : listeners) {
            try {
                listener.onValueChanged(value);
            } catch (Exception e) {
                arc.util.Log.err(e);
            }
        }
    }
}