package org.seal.mindustry.timeFactor.ui;

import arc.scene.ui.Slider;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import mindustry.gen.Tex;
import org.seal.mindustry.timeFactor.api.TimeSliderController;

public class TimeSliderView implements UIComponent<Table> {
    private final TimeSliderController controller;
    private final Slider slider;
    private final TextButton decBtn;
    private final TextButton incBtn;

    public TimeSliderView(TimeSliderController controller) {
        this.controller = controller;
        this.slider = createSlider();
        this.decBtn = createButton("<", false);
        this.incBtn = createButton(">", true);

        controller.addListener(this::onControllerChanged);
    }

    private Slider createSlider() {
        Slider s = new Slider(controller.getMinValue(), controller.getMaxValue(), 1, false);
        s.setValue(controller.getValue());
        s.moved(value -> controller.setValue((int) value));

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
        slider.setValue(newValue);
    }

    @Override
    public Table render() {
        Table table = new Table();
        table.add(decBtn).size(40, 40);
        table.add(slider).growX().height(40);
        table.add(incBtn).size(40, 40);

        return table;
    }
}
