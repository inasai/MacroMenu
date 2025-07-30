package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import com.inasai.macromenu.MacroMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import javax.annotation.Nonnull;

public class MacroMenuScreen extends BaseMacroScreen {

    private static final int BASE_BUTTON_SPACING_Y = 24;

    public MacroMenuScreen() {
        super(Component.translatable("screen.macromenu.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        double currentScale = ModConfig.getButtonSize().getScale();
        int scaledButtonHeight = (int)(MacroButtonWidget.BASE_BUTTON_HEIGHT * currentScale);
        int scaledButtonSpacingY = (int)(BASE_BUTTON_SPACING_Y * currentScale);
        int scaledMinButtonWidth = (int)(MacroButtonWidget.MIN_BUTTON_WIDTH * currentScale);

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.settings"),
                btn -> this.minecraft.setScreen(new ModSettingsScreen(this))
        ).bounds(this.width - 105, 10, 95, scaledButtonHeight).build());

        List<MacroButtonData> buttonDataList = ModConfig.getButtons();

        int currentY = this.height / 4;

        if (buttonDataList.isEmpty()) {
            Component noMacrosText = Component.translatable("macromenu.gui.no_macros_message");
            int textWidth = this.font.width(noMacrosText);
            int calculatedWidth = Math.max(scaledMinButtonWidth, textWidth + MacroButtonWidget.PADDING_X * 2);
            int startX = this.width / 2 - calculatedWidth / 2;

            this.addRenderableWidget(Button.builder(
                    noMacrosText,
                    btn -> {}
            ).bounds(startX, currentY, calculatedWidth, scaledButtonHeight).build());
            currentY += scaledButtonSpacingY;
        } else {
            for (MacroButtonData data : buttonDataList) {
                MacroButtonWidget macroButton = new MacroButtonWidget(
                        0, 0,
                        data,
                        btn -> runCommand(data.getCommand())
                );
                int startX = this.width / 2 - macroButton.getWidth() / 2;
                macroButton.setX(startX);
                macroButton.setY(currentY);

                this.addRenderableWidget(macroButton);
                currentY += scaledButtonSpacingY;
            }
        }

        Component addButtonText = Component.translatable("macromenu.gui.add");
        Component editButtonText = Component.translatable("macromenu.gui.edit");
        Component deleteButtonText = Component.translatable("macromenu.gui.delete");

        int addWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);
        int editWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);
        int deleteWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);

        addWidth = Math.max(addWidth, this.font.width(addButtonText) + MacroButtonWidget.PADDING_X * 2);
        editWidth = Math.max(editWidth, this.font.width(editButtonText) + MacroButtonWidget.PADDING_X * 2);
        deleteWidth = Math.max(deleteWidth, this.font.width(deleteButtonText) + MacroButtonWidget.PADDING_X * 2);

        int totalControlButtonsWidth = addWidth + editWidth + deleteWidth + (MacroButtonWidget.PADDING_X * 2);
        int controlButtonY = this.height - 40;

        int startX = this.width / 2 - totalControlButtonsWidth / 2;

        this.addRenderableWidget(Button.builder(
                addButtonText,
                btn -> this.minecraft.setScreen(new AddMacroScreen(this))
        ).bounds(startX, controlButtonY, addWidth, scaledButtonHeight).build());

        startX += addWidth + MacroButtonWidget.PADDING_X;
        this.addRenderableWidget(Button.builder(
                editButtonText,
                btn -> this.minecraft.setScreen(new SelectMacroScreen(this, SelectMacroScreen.Mode.EDIT))
        ).bounds(startX, controlButtonY, editWidth, scaledButtonHeight).build());

        startX += editWidth + MacroButtonWidget.PADDING_X;
        this.addRenderableWidget(Button.builder(
                deleteButtonText,
                btn -> this.minecraft.setScreen(new SelectMacroScreen(this, SelectMacroScreen.Mode.DELETE))
        ).bounds(startX, controlButtonY, deleteWidth, scaledButtonHeight).build());
    }

    private void runCommand(String command) {
        if (minecraft != null && minecraft.getConnection() != null) {
            double delaySeconds = ModConfig.getCommandDelaySeconds();
            if (delaySeconds > 0) {
                MacroMenu.SCHEDULER.schedule(() -> {
                    if (minecraft.getConnection() != null) {
                        minecraft.getConnection().sendCommand(command.startsWith("/") ? command.substring(1) : command);
                    }
                }, (long) (delaySeconds * 1000));
            } else {
                minecraft.getConnection().sendCommand(command.startsWith("/") ? command.substring(1) : command);
            }
            this.onClose();
        }
    }

    // Видаляємо дублюючий метод render, оскільки він тепер є в BaseMacroScreen
    /*
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int alpha = (int) (ModConfig.getBackgroundTransparency() * 255.0D);
        int backgroundColor = (alpha << 24) | (0x000000);
        guiGraphics.fill(0, 0, this.width, this.height, backgroundColor);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    */

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}