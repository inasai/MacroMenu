package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class EditMacroScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private final int macroIndex;
    private EditBox labelField;
    private EditBox commandField;
    private MacroButtonData originalData;

    public EditMacroScreen(Screen parentScreen, int macroIndex, MacroButtonData originalData) {
        super(Component.translatable("screen.macromenu.edit_title"));
        this.parentScreen = parentScreen;
        this.macroIndex = macroIndex;
        this.originalData = originalData;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int startY = this.height / 4;
        int fieldWidth = 200;
        int buttonHeight = (int)(MacroButtonWidget.BASE_BUTTON_HEIGHT * ModConfig.getButtonSize().getScale());
        int spacing = 24;

        this.labelField = new EditBox(this.font, centerX - fieldWidth / 2, startY, fieldWidth, buttonHeight, Component.translatable("macromenu.gui.label"));
        this.labelField.setValue(originalData.getLabel());
        this.addRenderableWidget(this.labelField);

        this.commandField = new EditBox(this.font, centerX - fieldWidth / 2, startY + spacing, fieldWidth, buttonHeight, Component.translatable("macromenu.gui.command"));
        this.commandField.setValue(originalData.getCommand());
        this.addRenderableWidget(this.commandField);

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.save"),
                btn -> {
                    String newLabel = labelField.getValue();
                    String newCommand = commandField.getValue();
                    if (!newLabel.isEmpty() && !newCommand.isEmpty()) {
                        ModConfig.updateMacro(macroIndex, new MacroButtonData(newLabel, newCommand));
                        this.minecraft.setScreen(parentScreen);
                    }
                }
        ).bounds(centerX - 100, startY + spacing * 2, 95, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.cancel"),
                btn -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX + 5, startY + spacing * 2, 95, buttonHeight).build());
    }

    // Видаляємо дублюючий метод render, оскільки він тепер є в BaseMacroScreen
    /*
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int alpha = (int) (ModConfig.getBackgroundTransparency() * 255.0D);
        int backgroundColor = (alpha << 24) | (0x000000);
        guiGraphics.fill(0, 0, this.width, this.height, backgroundColor);
        guiGraphics.drawCenteredString(font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    */

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}