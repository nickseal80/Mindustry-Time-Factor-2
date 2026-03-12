package org.seal.mindustry.timeFactor.api;

/**
 * Listener interface for receiving slider value change events.
 *
 * @author Seal
 * @version 1.0
 */
public interface SliderListener {

    /**
     * Called when the slider value changes.
     *
     * @param value The new slider value (range: -4 to 4, step size 1)
     */
    void onValueChanged(float value);
}