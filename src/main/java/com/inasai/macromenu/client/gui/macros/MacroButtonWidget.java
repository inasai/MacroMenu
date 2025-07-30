package com.inasai.macromenu.client.gui.macros;

import com.inasai.macromenu.config.ModConfig; // Додаємо імпорт ModConfig
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;

public class MacroButtonWidget extends Button {

    public static final int BASE_BUTTON_WIDTH = 150; // Базова ширина кнопки
    public static final int BASE_BUTTON_HEIGHT = 20; // Базова висота кнопки
    public static final int PADDING_X = 10;
    public static final int MIN_BUTTON_WIDTH = 70; // Мінімальна ширина кнопки

    private final MacroButtonData data;

    public MacroButtonWidget(int x, int y, MacroButtonData data, OnPress onPress) {
        super(x, y, calculateWidth(data.getLabel()), calculateHeight(), Component.literal(data.getLabel()), onPress, DEFAULT_NARRATION);
        this.data = data;
    }

    public static int calculateWidth(String label) {
        // Отримуємо коефіцієнт масштабування з конфігурації
        double scale = ModConfig.getButtonSize().getScale();
        Minecraft minecraft = Minecraft.getInstance();
        // Обчислюємо ширину на основі тексту та масштабу
        int textWidth = minecraft.font.width(label);
        int scaledWidth = (int) (BASE_BUTTON_WIDTH * scale);
        return Math.max(scaledWidth, textWidth + PADDING_X * 2);
    }

    private static int calculateHeight() {
        // Висота також може масштабуватися, якщо потрібно
        double scale = ModConfig.getButtonSize().getScale();
        return (int) (BASE_BUTTON_HEIGHT * scale);
    }

    @Override
    public int getWidth() {
        // Перевизначаємо getWidth, щоб він завжди повертав актуальну ширину
        return calculateWidth(this.data.getLabel());
    }

    @Override
    public int getHeight() {
        // Перевизначаємо getHeight, щоб він завжди повертав актуальну висоту
        return calculateHeight();
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

        if (ModConfig.showTooltips() && this.isHoveredOrFocused()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(data.getCommand()), mouseX, mouseY);
        }
    }
}