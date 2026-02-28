package org.seal.mindustry.timeFactor;

import arc.scene.ui.Slider;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import mindustry.gen.Tex;

/**
 * A custom UI component that combines a slider with increment/decrement buttons
 * for controlling speed values. Ranges from -4 to 4 with step size 1.
 *
 * @author Seal
 * @version 1.0
 */
public class SpeedSlider {
    /** Minimum slider value (slowest speed: 1/16x) */
    private int minVal;

    /** Maximum slider value (fastest speed: 16x) */
    private int maxVal;

    /** The main slider component */
    private final Slider slider;

    /** Listener for value change events */
    private SliderListener sliderListener;

    /** Button to decrease the value */
    private final TextButton decBtn;

    /** Button to increase the value */
    private final TextButton incBtn;

    /**
     * Constructs a new SpeedSlider with the specified default position.
     * Creates and configures the slider and both control buttons.
     *
     * @param defaultPosition Initial position value (should be between -4 and 4)
     */
    public SpeedSlider(int defaultPosition, int minVal, int maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;

        slider = new Slider(minVal, maxVal, 1, false);
        slider.setValue(defaultPosition);

        // Handle slider movement
        slider.moved(value -> {
            if (sliderListener != null) {
                sliderListener.onValueChanged(value);
            }
        });

        // Decrement button
        decBtn = new TextButton("<");
        decBtn.clicked(() -> {
            float currentValue = slider.getValue();
            if (slider.getValue() > minVal) {
                slider.setValue(currentValue - 1);

                if (sliderListener != null) {
                    sliderListener.onValueChanged(slider.getValue());
                }
            }
        });
        decBtn.getStyle().up = Tex.pane;
        decBtn.getStyle().over = Tex.flatDownBase;
        decBtn.getStyle().down = Tex.whitePane;

        // Increment button
        incBtn = new TextButton(">");
        incBtn.clicked(() -> {
            float currentValue = slider.getValue();
            if (slider.getValue() < maxVal) {
                slider.setValue(currentValue + 1);

                if (sliderListener != null) {
                    sliderListener.onValueChanged(slider.getValue());
                }
            }
        });
        incBtn.getStyle().up = Tex.pane;
        incBtn.getStyle().over = Tex.flatDownBase;
        incBtn.getStyle().down = Tex.whitePane;
    }

    public void setRange(int minVal, int maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;

        // Сохраняем текущее значение
        float currentValue = slider.getValue();

        // Обновляем диапазон слайдера
        slider.setRange(minVal, maxVal);

        // Если текущее значение выходит за новые границы, корректируем его
        if (currentValue < minVal) {
            slider.setValue(minVal);
            if (sliderListener != null) {
                sliderListener.onValueChanged(minVal);
            }
        } else if (currentValue > maxVal) {
            slider.setValue(maxVal);
            if (sliderListener != null) {
                sliderListener.onValueChanged(maxVal);
            }
        } else {
            // Возвращаем сохраненное значение
            slider.setValue(currentValue);
        }
    }

    /**
     * Renders the complete slider component with buttons in a table layout.
     *
     * @return A Table containing the decrement button, slider, and increment button
     */
    public Table render() {
        Table table = new Table();
        table.add(decBtn).size(40, 40);
        table.add(slider).growX().height(40);
        table.add(incBtn).size(40, 40);

        return table;
    }

    /**
     * Sets the listener for slider value changes.
     *
     * @param sliderListener The listener that will receive value change events
     */
    public void setListener(SliderListener sliderListener) {
        this.sliderListener = sliderListener;
    }

    /**
     * Gets the underlying slider component.
     *
     * @return The Slider instance
     */
    public Slider getSlider() {
        return slider;
    }

    /**
     * Gets the decrement button component.
     *
     * @return The decrement TextButton
     */
    public TextButton getDecBtn() {
        return decBtn;
    }

    /**
     * Gets the increment button component.
     *
     * @return The increment TextButton
     */
    public TextButton getIncBtn() {
        return incBtn;
    }
}