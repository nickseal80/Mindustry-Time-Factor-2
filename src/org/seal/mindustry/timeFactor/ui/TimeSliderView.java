package org.seal.mindustry.timeFactor.ui;

import arc.scene.ui.Slider;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.gen.Tex;
import org.seal.mindustry.timeFactor.api.TimeSliderController;

public class TimeSliderView implements UIComponent<Table> {
    private final TimeSliderController controller;
    private Slider slider;
    private final TextButton decBtn;
    private final TextButton incBtn;
    private Table container;

    // Для отслеживания изменений диапазона
    private int lastMinValue;
    private int lastMaxValue;

    public TimeSliderView(TimeSliderController controller) {
        this.controller = controller;
        this.lastMinValue = controller.getMinValue();
        this.lastMaxValue = controller.getMaxValue();
        this.slider = createSlider();
        this.decBtn = createButton("<", false);
        this.incBtn = createButton(">", true);

        // Подписываемся на изменения контроллера
        controller.addListener(this::onControllerChanged);
    }

    private Slider createSlider() {
        Log.info("Creating new slider with range: " +
                controller.getMinValue() + "-" + controller.getMaxValue() +
                ", value: " + controller.getValue());

        Slider s = new Slider(controller.getMinValue(), controller.getMaxValue(), 1, false);
        s.setValue(controller.getValue());

        // При движении слайдера обновляем контроллер
        s.moved(value -> {
            controller.setValue((int) value);
        });

        return s;
    }

    private TextButton createButton(String text, boolean increment) {
        TextButton btn = new TextButton(text);
        btn.clicked(() -> {
            int newValue = increment ?
                    controller.getValue() + 1 :
                    controller.getValue() - 1;
            controller.setValue(newValue);
        });
        btn.getStyle().up = Tex.pane;
        btn.getStyle().over = Tex.flatDownBase;
        btn.getStyle().down = Tex.whitePane;

        return btn;
    }

    private void onControllerChanged(float newValue) {

        // Проверяем, изменился ли диапазон
        boolean rangeChanged =
                lastMinValue != controller.getMinValue() ||
                        lastMaxValue != controller.getMaxValue();

        if (rangeChanged) {
            Log.info("Range changed from " + lastMinValue + "-" + lastMaxValue +
                    " to " + controller.getMinValue() + "-" + controller.getMaxValue());

            // Обновляем сохраненные значения
            lastMinValue = controller.getMinValue();
            lastMaxValue = controller.getMaxValue();

            // Создаём новый слайдер с обновлённым диапазоном
            Slider newSlider = new Slider(
                    controller.getMinValue(),
                    controller.getMaxValue(),
                    1, false
            );
            newSlider.setValue(newValue);
            newSlider.moved(val -> controller.setValue((int) val));

            // Заменяем старый слайдер новым
            this.slider = newSlider;

            // Перестраиваем UI
            rebuildUI();
        } else {
            // Просто обновляем позицию слайдера
            slider.setValue(newValue);
        }
    }

    private void rebuildUI() {
        if (container != null) {
            Log.info("Rebuilding UI with new slider");

            // Очищаем контейнер
            container.clear();

            // Добавляем компоненты заново
            container.add(decBtn).size(40, 40);
            container.add(slider).growX().height(40);
            container.add(incBtn).size(40, 40);

            // Принудительно обновляем layout
            container.invalidate();
            container.layout();
            container.pack();
        }
    }

    @Override
    public Table render() {
        container = new Table();
        container.add(decBtn).size(40, 40);
        container.add(slider).growX().height(40);
        container.add(incBtn).size(40, 40);

        return container;
    }
}