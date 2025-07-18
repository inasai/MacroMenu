package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.config.ModConfig; // Додаємо імпорт
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;

public class AddMacroScreen extends Screen {
    private final Screen parentScreen;
    private EditBox labelEditBox;
    private EditBox commandEditBox;

    public AddMacroScreen(Screen parentScreen) {
        super(Component.translatable("screen.macromenu.add_title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int buttonWidth = 150;
        int buttonHeight = (int)(MacroButtonWidget.BASE_BUTTON_HEIGHT * ModConfig.getButtonSize().getScale());

        this.labelEditBox = new EditBox(this.font, centerX - 150, 75, 300, 20, Component.translatable("macromenu.gui.label")); // ВИПРАВЛЕНО: З guiGraphics.font на this.font
        this.labelEditBox.setMaxLength(32);
        this.addRenderableWidget(this.labelEditBox);

        this.commandEditBox = new EditBox(this.font, centerX - 150, 125, 300, 20, Component.translatable("macromenu.gui.command")); // ВИПРАВЛЕНО: З guiGraphics.font на this.font
        this.commandEditBox.setMaxLength(256);
        this.addRenderableWidget(this.commandEditBox);

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.save"),
                button -> {
                    if (!labelEditBox.getValue().isEmpty() && !commandEditBox.getValue().isEmpty()) {
                        ModConfig.addMacro(new MacroButtonData(labelEditBox.getValue(), commandEditBox.getValue()));
                        this.minecraft.setScreen(parentScreen);
                    }
                }
        ).bounds(centerX - buttonWidth - 5, this.height - 40, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.cancel"),
                button -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX + 5, this.height - 40, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int alpha = (int) (ModConfig.getBackgroundTransparency() * 255.0D);
        int backgroundColor = (alpha << 24) | (0x000000);
        guiGraphics.fill(0, 0, this.width, this.height, backgroundColor);

        // ВИПРАВЛЕНО: З guiGraphics.font на this.font
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        this.labelEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.commandEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        // ВИПРАВЛЕНО: З guiGraphics.font на this.font
        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.label"), this.width / 2 - 150, 60, 0xFFFFFF);
        // ВИПРАВЛЕНО: З guiGraphics.font на this.font
        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.command"), this.width / 2 - 150, 110, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}