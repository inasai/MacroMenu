package com.inasai.macromenu.client.gui.tabs;

import net.minecraft.client.Minecraft; // Додаємо цей імпорт
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TabButton extends Button {
    private final boolean isActive;

    public TabButton(int x, int y, int width, int height, Component message, OnPress onPress, boolean isActive) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.isActive = isActive;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int color = 0xFFFFFFFF; // Білий колір тексту
        int backgroundColor = isActive ? 0xAA6699CC : 0xAA000000; // Підсвічуємо активну вкладку

        // Малюємо фон
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), backgroundColor);

        // Малюємо текст
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, color);
    }
}