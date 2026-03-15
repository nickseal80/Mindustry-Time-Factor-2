package org.seal.mindustry.timeFactor.ui;

import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.util.Align;
import mindustry.ui.dialogs.BaseDialog;
import org.seal.mindustry.timeFactor.api.SettingsController;
import org.seal.mindustry.timeFactor.util.TimeSpeedFormat;

public class SettingsDialog implements UIComponent<BaseDialog> {
    private final SettingsController controller;
    private final BaseDialog dialog;

    // Сохраняем ссылки на UI компоненты
    private RangeSlider minRangeSlider;
    private RangeSlider maxRangeSlider;
    private Label minRangeLabel;
    private Label maxRangeLabel;

    public SettingsDialog(SettingsController controller) {
        this.controller = controller;
        this.dialog = createDialog();

        // Подписываемся на изменения настроек
        controller.addListener(settings -> {
            updateUI();
        });
    }

    private BaseDialog createDialog() {
        BaseDialog dlg = new BaseDialog("Settings");

        // Создаём компоненты с начальными значениями из контроллера
        minRangeSlider = createRangeSlider("minPos", -6, 0);
        maxRangeSlider = createRangeSlider("maxPos", 0, 6);
        minRangeLabel = createRangeLabel("minPos");
        maxRangeLabel = createRangeLabel("maxPos");

        dlg.cont.table(grid -> {
            grid.defaults().pad(5);

            grid.add(minRangeSlider);
            grid.add(minRangeLabel).width(60).right();
            grid.row();

            grid.add(maxRangeSlider);
            grid.add(maxRangeLabel).width(60).right();
            grid.row();
        }).growX().pad(10);

        // Кнопки
        TextButton okBtn = new TextButton("Ok");
        okBtn.clicked(dlg::hide);

        TextButton cancelBtn = new TextButton("Cancel");
        cancelBtn.clicked(() -> {
            // Отменяем изменения - перезагружаем из контроллера
            updateUI();
            dlg.hide();
        });

        TextButton resetBtn = new TextButton("Reset to Defaults");
        resetBtn.clicked(() -> {
            controller.setMinPos(-4);
            controller.setMaxPos(4);
        });

        dlg.buttons.add(resetBtn).size(140, 50).pad(5);
        dlg.buttons.add(cancelBtn).size(100, 50).pad(5);
        dlg.buttons.add(okBtn).size(100, 50).pad(5);

        return dlg;
    }

    private void updateUI() {
        // Обновляем все UI компоненты из текущих настроек
        minRangeSlider.setIntValue(controller.getMinPos());
        maxRangeSlider.setIntValue(controller.getMaxPos());
        minRangeLabel.setText(TimeSpeedFormat.pos2str(controller.getMinPos()));
        maxRangeLabel.setText(TimeSpeedFormat.pos2str(controller.getMaxPos()));
    }

    private RangeSlider createRangeSlider(String key, int min, int max) {
        // Получаем начальное значение
        int initialValue = key.equals("minPos") ?
                controller.getMinPos() : controller.getMaxPos();

        RangeSlider slider = new RangeSlider(min, max);
        slider.setIntValue(initialValue);

        // Добавляем слушатель на изменения слайдера
        slider.addListener(value -> {
            if (key.equals("minPos")) {
                controller.setMinPos(value);
            } else {
                controller.setMaxPos(value);
            }
        });

        return slider;
    }

    private Label createRangeLabel(String key) {
        int value = key.equals("minPos") ?
                controller.getMinPos() : controller.getMaxPos();

        Label label = new Label(TimeSpeedFormat.pos2str(value));
        label.setAlignment(Align.center);
        return label;
    }

    public void showDialog() {
        updateUI(); // Обновляем перед показом
        dialog.show();
    }

    public void closeDialog() {
        dialog.hide();
    }

    @Override
    public BaseDialog render() {
        return dialog;
    }
}