package com.inasai.macromenu.client.gui.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

// Новий клас для слайдера затримки виконання команди
public class CommandDelaySliderWidget extends AbstractSliderButton {
    private final Consumer<Double> applyValue; // Consumer для застосування нового значення
    private final double minValue;
    private final double maxValue;

    public CommandDelaySliderWidget(int x, int y, int width, int height, double initialValueNormalized, Consumer<Double> applyValue) {
        // initialValueNormalized має бути значенням від 0.0 до 1.0
        // Для затримки, ми працюємо з секундами. Припустимо, діапазон від 0 до 60 секунд.
        // Тоді initialValueNormalized = ModConfig.getCommandDelaySeconds() / 60.0D
        super(x, y, width, height, Component.empty(), initialValueNormalized); // Початкове повідомлення буде оновлено в updateMessage()
        this.minValue = 0.0D; // Мінімальне значення слайдера (0 секунд)
        this.maxValue = 60.0D; // Максимальне значення слайдера (60 секунд)
        this.applyValue = applyValue;
        this.updateMessage(); // Оновити повідомлення після ініціалізації
    }

    @Override
    protected void updateMessage() {
        // Перетворюємо значення слайдера (0.0 - 1.0) в секунди (0 - 60)
        int seconds = (int) (this.value * (maxValue - minValue) + minValue);
        this.setMessage(Component.translatable("macromenu.gui.command_delay", seconds));
    }

    @Override
    protected void applyValue() {
        // Перетворюємо значення слайдера (0.0 - 1.0) в секунди (0 - 60)
        double actualValue = this.value * (maxValue - minValue) + minValue;
        this.applyValue.accept(actualValue); // Застосовуємо отримане значення
    }
}