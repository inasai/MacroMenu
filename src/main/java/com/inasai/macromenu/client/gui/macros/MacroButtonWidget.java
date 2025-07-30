package com.inasai.macromenu.client.gui.macros;

import com.inasai.macromenu.client.gui.MacroMenuScreen;
import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;

public class MacroButtonWidget extends Button {

    public static final int BASE_BUTTON_WIDTH = 150;
    public static final int BASE_BUTTON_HEIGHT = 20;
    public static final int PADDING_X = 10;
    public static final int MIN_BUTTON_WIDTH = 70;
    public static final int BASE_BUTTON_SPACING = 5;

    private final MacroButtonData data; // Зберігаємо повні дані для підказок
    private final int textColor; // Колір тексту

    // Додаємо MacroMenuScreen як аргумент
    public MacroButtonWidget(int x, int y, MacroButtonData data, int textColor, MacroMenuScreen parentScreen) {
        super(x, y, calculateWidth(data.getLabel()), calculateHeight(), Component.literal(data.getLabel()), btn -> {
            if (parentScreen != null) {
                parentScreen.runCommand(data.getCommand());
            }
        }, DEFAULT_NARRATION);
        this.data = data;
        this.textColor = textColor;
    }

    public static int calculateWidth(String label) {
        double scale = ModConfig.getButtonSize().getScale();
        Minecraft minecraft = Minecraft.getInstance();
        int textWidth = minecraft.font.width(label);
        int scaledWidth = (int) (BASE_BUTTON_WIDTH * scale);
        return Math.max(scaledWidth, textWidth + PADDING_X * 2);
    }

    private static int calculateHeight() {
        double scale = ModConfig.getButtonSize().getScale();
        return (int) (BASE_BUTTON_HEIGHT * scale);
    }

    @Override
    public int getWidth() {
        return calculateWidth(this.data.getLabel());
    }

    @Override
    public int getHeight() {
        return calculateHeight();
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

        int textX = this.getX() + this.getWidth() / 2;
        int textY = this.getY() + (this.getHeight() - 8) / 2;
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), textX, textY, this.textColor);

        if (this.isHovered && !this.data.getDescription().isEmpty()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(this.data.getDescription()), mouseX, mouseY);
        } else if (ModConfig.showTooltips() && this.isHovered) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(this.data.getCommand()), mouseX, mouseY);
        }
    }
}