package org.seal.mindustry.timeFactor.ui;

import arc.scene.ui.Slider;
import arc.util.Log;
import org.seal.mindustry.timeFactor.util.TimeSpeedFormat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RangeSlider extends Slider {
    private final List<RangeListener> listeners = new CopyOnWriteArrayList<>();

    public RangeSlider(int min, int max) {
        super((float) min, (float) max, 1f, false);
        setupListener();
    }

    public RangeSlider(int min, int max, float step, boolean vertical) {
        super((float) min, (float) max, step, vertical);
        setupListener();
    }

    private void setupListener() {
        this.moved(value -> {
            notifyListeners((int) value);
        });
    }

    public void addListener(RangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(int value) {
        for (RangeListener listener : listeners) {
            try {
                listener.onValueChanged(value);
            } catch (Exception e) {
                Log.err("Error in RangeListener", e);
            }
        }
    }

    public String getRangeValue() {
        return TimeSpeedFormat.pos2str((int) this.getValue());
    }

    public int getIntValue() {
        return Math.round(this.getValue());
    }

    public void setIntValue(int value) {
        this.setValue((float) value);
    }

    public interface RangeListener {
        void onValueChanged(int newValue);
    }
}