package org.seal.mindustry.timeFactor.settings;

import arc.scene.ui.Slider;

/**
 * Custom slider for Time Factor mod that displays speed values.
 * Extends arc.scene.ui.Slider with additional functionality.
 */
public class TFSlider extends Slider {

    /**
     * Creates a new TFSlider with specified range.
     *
     * @param min Minimum value (negative for slow speeds)
     * @param max Maximum value (positive for fast speeds)
     */
    public TFSlider(int min, int max) {
        // Конвертируем int в float для родительского конструктора
        super((float)min, (float)max, 1f, false);
    }

    /**
     * Creates a new TFSlider with specified range and step.
     *
     * @param min Minimum value
     * @param max Maximum value
     * @param step Step size between values
     */
    public TFSlider(int min, int max, float step) {
        super((float)min, (float)max, step, false);
    }

    /**
     * Creates a new TFSlider with specified range, step and orientation.
     *
     * @param min Minimum value
     * @param max Maximum value
     * @param step Step size between values
     * @param vertical True for vertical orientation, false for horizontal
     */
    public TFSlider(int min, int max, float step, boolean vertical) {
        super((float)min, (float)max, step, vertical);
    }

    /**
     * Gets the current speed as a human-readable string.
     *
     * @return Formatted speed string (e.g., "x1", "x4", "x1/2")
     */
    public String getSpeed() {
        float value = this.getValue();

        if (value >= 0) {
            return "x" + (int) Math.pow(2, value);
        } else {
            return "x1/" + (int) Math.pow(2, Math.abs(value));
        }
    }

    /**
     * Gets the current speed multiplier as a float.
     *
     * @return Speed multiplier (e.g., 1.0, 2.0, 0.5)
     */
    public float getSpeedMultiplier() {
        return (float) Math.pow(2, this.getValue());
    }

    /**
     * Gets the current position as an integer.
     *
     * @return Current position rounded to nearest integer
     */
    public int getPosition() {
        return Math.round(this.getValue());
    }

    /**
     * Sets the slider position using an integer value.
     *
     * @param position New position value
     */
    public void setPosition(int position) {
        this.setValue((float)position);
    }

    /**
     * Checks if current speed is faster than normal.
     *
     * @return true if speed > 1x
     */
    public boolean isFaster() {
        return this.getValue() > 0;
    }

    /**
     * Checks if current speed is slower than normal.
     *
     * @return true if speed < 1x
     */
    public boolean isSlower() {
        return this.getValue() < 0;
    }

    /**
     * Checks if current speed is normal (1x).
     *
     * @return true if speed == 1x
     */
    public boolean isNormal() {
        return this.getValue() == 0;
    }
}