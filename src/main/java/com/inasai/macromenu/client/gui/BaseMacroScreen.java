package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.client.gui.themes.Theme;
import com.inasai.macromenu.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;

public abstract class BaseMacroScreen extends Screen {

    protected BaseMacroScreen(Component title) {
        super(title);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        if (this.minecraft == null) return;

        // Отримуємо значення прозорості
        double transparency = ClientConfig.backgroundTransparency;
        int alpha = (int) (transparency * 255.0F);

        if (ClientConfig.currentTheme == Theme.ThemeType.CLASSIC) {
            // Класична тема: рендеримо стандартний фон Minecraft
            // Важливо: для цього потрібно правильно викликати метод fillGradient, який накладає текстуру з прозорістю
            guiGraphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

            // Якщо потрібна текстура, то робимо так:
            // guiGraphics.setColor(1.0F, 1.0F, 1.0F, (float) transparency);
            // guiGraphics.blit(Theme.MENU_BACKGROUND, 0, 0, 0, 0, this.width, this.height);
            // guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        } else if (ClientConfig.currentTheme == Theme.ThemeType.DARK) {
            // Темна тема: рендеримо напівпрозорий чорний фон
            int backgroundColor = alpha << 24;
            guiGraphics.fill(0, 0, this.width, this.height, backgroundColor);
        } else {
            // Кастомна тема, поки що як темна
            int backgroundColor = alpha << 24;
            guiGraphics.fill(0, 0, this.width, this.height, backgroundColor);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}