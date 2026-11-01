package org.seal.mindustry.timeFactor;

import arc.Core;
import arc.input.KeyCode;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.event.ClickListener;
import arc.scene.event.EventListener;
import arc.scene.event.InputEvent;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.mod.Mod;
import org.seal.mindustry.timeFactor.api.PositionController;
import org.seal.mindustry.timeFactor.api.SettingsController;
import org.seal.mindustry.timeFactor.model.PositionManager;
import org.seal.mindustry.timeFactor.model.Settings;
import org.seal.mindustry.timeFactor.model.TimeSliderModel;
import org.seal.mindustry.timeFactor.ui.SettingsDialog;
import org.seal.mindustry.timeFactor.ui.TimeSliderView;
import org.seal.mindustry.timeFactor.util.Localization;
import org.seal.mindustry.timeFactor.util.TimeSpeedFormat;

/**
 * Main mod class for Time Factor - a Mindustry mod that allows real-time speed control.
 * Adds a UI slider to adjust game speed from 1/16x to 16x.
 *
 * @author Seal
 * @version 2.1
 */
public class TimeFactor extends Mod {

    private PositionController positionController;
    private SettingsController settingsController;
    private TimeSliderModel sliderModel;
    private TimeSliderView sliderView;
    private SettingsDialog settingsDialog;

    private boolean showTooltips;

    private boolean expanded = true;
    private Table main;
    private Table collapsedMain;

    // Текущий активный UI элемент в HUD
    private Table activeUI;

    // Лейбл для свёрнутого режима (нужен для обновления)
    private Label collapsedLabel;
    private TextButton collapsedDecButton;
    private TextButton collapsedIncButton;

    // Сохраняем ссылки на компоненты для обновления тултипов
    private ImageButton settingsBtn;
    private ImageButton resetBtn;
    private ImageButton collapseBtn;

    /**
     * Updates the game's delta time provider based on the current speed position.
     * The delta is capped to prevent physics issues at extreme speeds.
     */
    private void updateGameSpeed(int position) {
        float speed = TimeSpeedFormat.getMultiplier(position);
        Time.setDeltaProvider(() -> {
//            float delta = Core.graphics.getDeltaTime();
//            return Math.min(delta * 60 * speed, 3 * speed);

            float delta = Core.graphics.getDeltaTime() * 60f * speed;
            return Math.min(delta, 10f * speed);
        });
    }

    /**
     * Resets the game speed to normal (1x) and sets position to 0.
     */
    private void reset() {
        Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60, 3));
        positionController.setPosition(0);
    }

    /**
     * Initializes all controllers and models.
     */
    private void initControllers() {
        // Create controllers
        positionController = new PositionManager();
        settingsController = new Settings(this);

        // Create slider model with initial values
        sliderModel = new TimeSliderModel(
                positionController.getPosition(),
                settingsController.getMinPos(),
                settingsController.getMaxPos()
        );

        // Connect slider model to position controller
        sliderModel.addListener(value -> {
            positionController.setPosition((int) value);
        });

        // Update slider model when position changes externally
        positionController.addListener(newPosition -> {
            sliderModel.setValue(newPosition);
        });

        // Update slider model and tooltips when settings change
        settingsController.addListener(settings -> {
            sliderModel.setRange(settings.minPos, settings.maxPos);
            showTooltips = settings.showTooltips;

            // Обновляем тултипы при изменении настроек
            Core.app.post(() -> updateTooltips());
        });

        // Create UI components
        sliderView = new TimeSliderView(sliderModel);
        settingsDialog = new SettingsDialog(settingsController);
    }

    /**
     * Initializes both UI variants.
     */
    private void initUI() {
        this.main = createUIFull();
        this.collapsedMain = createUICollapsed();
    }

    /**
     * Удаляет все тултипы у элемента
     */
    private void removeTooltips(Element element) {
        if (element == null) return;

        Seq<EventListener> listeners = element.getListeners();
        // Идём с конца, чтобы не сбивать индексы
        for (int i = listeners.size - 1; i >= 0; i--) {
            if (listeners.get(i) instanceof Tooltip) {
                listeners.remove(i);
            }
        }
    }

    /**
     * Добавляет тултип к элементу
     */
    private void addTooltip(Element element, String text) {
        if (element == null) return;

        element.addListener(new Tooltip(t -> {
            t.setBackground(Tex.pane);
            t.add(text);
        }));
    }

    /**
     * Обновляет видимость тултипов на всех компонентах
     */
    private void updateTooltips() {
        // Сначала удаляем все старые тултипы
        removeTooltips(settingsBtn);
        removeTooltips(resetBtn);
        removeTooltips(collapseBtn);
        removeTooltips(collapsedMain);

        // Затем добавляем новые, если нужно
        if (showTooltips) {
            addTooltip(settingsBtn, Localization.get("tf.settings.tooltip"));
            addTooltip(resetBtn, Localization.get("tf.reset.tooltip"));
            addTooltip(collapseBtn, Localization.get("tf.collapse.tooltip"));
            addTooltip(collapsedMain, Localization.get("tf.expand.tooltip"));
        }
    }

    /**
     * Creates the full expanded UI with all controls.
     */
    private Table createUIFull() {
        Table container = new Table();

        container.table(Tex.pane, panel -> {
            panel.name = "time-control-ui-full";

            // Settings button
            settingsBtn = new ImageButton(Icon.settings);
            settingsBtn.clicked(() -> settingsDialog.showDialog());
            styleButton(settingsBtn);

            // Reset button
            resetBtn = new ImageButton(Icon.refresh);
            resetBtn.clicked(this::reset);
            styleButton(resetBtn);

            // Collapse button
            collapseBtn = new ImageButton(Icon.upOpenSmall);
            styleButton(collapseBtn);
            collapseBtn.clicked(() -> {
                expanded = false;
                switchUI();
            });

            // Speed display label
            Label label = new Label(positionController.pos2str());
            label.setAlignment(Align.center);
            positionController.addListener(position ->
                    label.setText(positionController.pos2str())
            );

            // Add components
            panel.add(settingsBtn).size(40, 40).padRight(5);
            panel.add(label).size(60, 40).pad(5).padRight(5);
            panel.add(resetBtn).size(40, 40).padRight(5);
            panel.add(sliderView.render()).padRight(5);
            panel.add(collapseBtn).size(20, 20).padLeft(7).right().padTop(5).top();

        }).pad(10, 10, 10, 0).size(440, 60);

        container.left().bottom();
        return container;
    }

    /**
     * Creates the collapsed UI with only speed display.
     */
//    private Table createUICollapsed() {
//        Table container = new Table();
//
//        container.table(Tex.pane, panel -> {
//            panel.name = "time-control-ui-collapsed";
//
//            // Decrement button
//            collapsedDecButton = new TextButton("<");
//            collapsedDecButton.clicked(() -> {
//                int newValue = positionController.getPosition() - 1;
//                sliderModel.setValue(newValue);
//            });
//
//            // IncrementButton
//            collapsedIncButton = new TextButton(">");
//            collapsedIncButton.clicked(() -> {
//                int newValue = positionController.getPosition() + 1;
//                sliderModel.setValue(newValue);
//            });
//
//            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(collapsedDecButton.getStyle());
//            style.up = null;
//            style.over = null;
//            style.down = null;
//
//            collapsedDecButton.setStyle(style);
//            collapsedIncButton.setStyle(style);
//
//            // Speed label
//            collapsedLabel = new Label(positionController.pos2str());
//            collapsedLabel.setAlignment(Align.center);
//
//            positionController.addListener(position ->
//                    collapsedLabel.setText(positionController.pos2str())
//            );
//
//            // Делаем панель кликабельной с курсором-рукой
////            panel.clicked(() -> {
////                expanded = true;
////                switchUI();
////            });
//
//            panel.add(collapsedDecButton).size(30, 40);
//            panel.add(collapsedLabel).size(60, 40).pad(5);
//            panel.add(collapsedIncButton).size(30, 40);
//
//        }).pad(10).size(170, 60);
//
//        container.left().bottom();
//        return container;
//    }

    private Table createUICollapsed() {
        Table container = new Table();

        container.table(Tex.pane, panel -> {
            panel.name = "time-control-ui-collapsed";

            // Decrement button
            collapsedDecButton = new TextButton("<");
            collapsedDecButton.clicked(() -> {
                int newValue = positionController.getPosition() - 1;
                sliderModel.setValue(newValue);
            });

            // IncrementButton
            collapsedIncButton = new TextButton(">");
            collapsedIncButton.clicked(() -> {
                int newValue = positionController.getPosition() + 1;
                sliderModel.setValue(newValue);
            });

            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(collapsedDecButton.getStyle());
            style.up = null;
            style.over = null;
            style.down = null;

            collapsedDecButton.setStyle(style);
            collapsedIncButton.setStyle(style);

            // Speed label
            collapsedLabel = new Label(positionController.pos2str());
            collapsedLabel.setAlignment(Align.center);

            positionController.addListener(position ->
                    collapsedLabel.setText(positionController.pos2str())
            );

            // Добавляем элементы на панель
            panel.add(collapsedDecButton).size(30, 40);
            panel.add(collapsedLabel).size(60, 40).pad(5);
            panel.add(collapsedIncButton).size(30, 40);

            // --- ДОБАВЛЯЕМ КЛИК ПО ПАНЕЛИ ---
            panel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Проверяем локальные координаты x и y.
                    // Если клик НЕ попал в область кнопок — разворачиваем панель.
                    // (Клик по лейблу или пустому месту будет работать)

                    boolean clickedDec = x >= collapsedDecButton.x && x <= collapsedDecButton.x + collapsedDecButton.getWidth() &&
                            y >= collapsedDecButton.y && y <= collapsedDecButton.y + collapsedDecButton.getHeight();

                    boolean clickedInc = x >= collapsedIncButton.x && x <= collapsedIncButton.x + collapsedIncButton.getWidth() &&
                            y >= collapsedIncButton.y && y <= collapsedIncButton.y + collapsedIncButton.getHeight();

                    if (!clickedDec && !clickedInc) {
                        expanded = true;
                        switchUI();
                    }
                }
            });

        }).pad(10).size(170, 60);

        container.left().bottom();
        return container;
    }

    /**
     * Switches between expanded and collapsed UI.
     */
    private void switchUI() {
        if (activeUI == null || Vars.ui == null || Vars.ui.hudGroup == null) {
            return;
        }

        Log.info("Switching UI to " + (expanded ? "expanded" : "collapsed") + " mode");

        // Анимация затухания текущей панели
        activeUI.actions(
                Actions.fadeOut(0.15f),
                Actions.run(() -> {
                    // Удаляем старую панель после затухания
                    Vars.ui.hudGroup.removeChild(activeUI);

                    // Выбираем новую панель
                    activeUI = expanded ? main : collapsedMain;
                    activeUI.setColor(1, 1, 1, 0); // Прозрачная в начале

                    // Добавляем новую панель
                    Vars.ui.hudGroup.addChild(activeUI);

                    // Анимация появления
                    activeUI.actions(Actions.fadeIn(0.15f));
                })
        );
    }

    /**
     * Renders the initial UI based on expanded state.
     */
    private void render() {
        initUI();

        // Выбираем начальную панель
        activeUI = expanded ? main : collapsedMain;

        // Добавляем в HUD
        Vars.ui.hudGroup.addChild(activeUI);

        // Инициализируем тултипы согласно настройкам
        Core.app.post(() -> updateTooltips());

        Log.info("TimeFactor UI rendered in " + (expanded ? "expanded" : "collapsed") + " mode");
    }

    /**
     * Applies consistent styling to buttons.
     */
    private void styleButton(ImageButton button) {
        button.getStyle().up = Tex.pane;
        button.getStyle().over = Tex.flatDownBase;
        button.getStyle().down = Tex.whitePane;
    }

    /**
     * Initializes the mod after game load.
     */
    @Override
    public void init() {
        // Initialize all components
        initControllers();

        // Connect position controller to game speed
        positionController.addListener(this::updateGameSpeed);

        // Schedule UI creation on main thread
        Core.app.post(() -> {
            if (Vars.ui != null && Vars.ui.hudGroup != null) {
                render();
            }
        });

        Log.info("TimeFactor mod initialized");
    }
}