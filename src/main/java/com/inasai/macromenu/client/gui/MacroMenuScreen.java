package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import com.inasai.macromenu.MacroMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class MacroMenuScreen extends BaseMacroScreen {

    private static final int BASE_BUTTON_SPACING = 5;
    private static final int BASE_BUTTON_SPACING_Y = 24;

    public MacroMenuScreen() {
        super(Component.translatable("screen.macromenu.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        double currentScale = ModConfig.getButtonSize().getScale();
        int scaledButtonHeight = (int) (MacroButtonWidget.BASE_BUTTON_HEIGHT * currentScale);
        int scaledButtonSpacing = (int) (BASE_BUTTON_SPACING * currentScale);
        int scaledButtonSpacingY = (int) (BASE_BUTTON_SPACING_Y * currentScale);
        int scaledMinButtonWidth = (int) (MacroButtonWidget.MIN_BUTTON_WIDTH * currentScale);

        // Кнопка налаштувань
        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.settings"),
                btn -> this.minecraft.setScreen(new ModSettingsScreen(this))
        ).bounds(this.width - 105, 10, 95, scaledButtonHeight).build());

        List<MacroButtonData> buttonDataList = ModConfig.getButtons();
        int availableWidth = this.width - scaledButtonSpacing * 2;

        // --- Логіка відображення вкладок ---
        int tabButtonWidth = 80;
        int tabButtonY = 10;
        int tabButtonX = 10;

        for (ModConfig.TabConfig tab : ModConfig.getTabs()) {
            this.addRenderableWidget(new TabButton(
                    tabButtonX, tabButtonY, tabButtonWidth, scaledButtonHeight,
                    Component.literal(tab.name),
                    btn -> {
                        ModConfig.setActiveTab(tab.name);
                        this.minecraft.setScreen(new MacroMenuScreen());
                    },
                    tab.name.equals(ModConfig.getActiveTab().name)
            ));
            tabButtonX += tabButtonWidth + 5;
        }

        // --- Логіка для головних кнопок ---
        if (buttonDataList.isEmpty()) {
            Component noMacrosText = Component.translatable("macromenu.gui.no_macros_message");
            int textWidth = this.font.width(noMacrosText);
            int calculatedWidth = Math.max(scaledMinButtonWidth, textWidth + MacroButtonWidget.PADDING_X * 2);
            int startX = this.width / 2 - calculatedWidth / 2;

            this.addRenderableWidget(Button.builder(
                    noMacrosText,
                    btn -> {}
            ).bounds(startX, this.height / 2, calculatedWidth, scaledButtonHeight).build());
        } else {
            int currentY = this.height / 2 - scaledButtonHeight / 2;
            List<List<MacroButtonData>> rows = new ArrayList<>();
            List<MacroButtonData> currentRow = new ArrayList<>();
            int currentRowWidth = 0;

            for (MacroButtonData data : buttonDataList) {
                int buttonWidth = MacroButtonWidget.calculateWidth(data.getLabel());
                if (currentRowWidth + buttonWidth + (currentRow.isEmpty() ? 0 : scaledButtonSpacing) > availableWidth) {
                    rows.add(currentRow);
                    currentRow = new ArrayList<>();
                    currentRowWidth = 0;
                }
                currentRow.add(data);
                currentRowWidth += buttonWidth + (currentRow.size() == 1 ? 0 : scaledButtonSpacing);
            }
            if (!currentRow.isEmpty()) {
                rows.add(currentRow);
            }

            int totalHeight = (rows.size() * scaledButtonHeight) + ((rows.size() - 1) * scaledButtonSpacingY);
            currentY = (this.height / 2) - (totalHeight / 2);

            for (List<MacroButtonData> row : rows) {
                int rowWidth = (row.stream().mapToInt(d -> MacroButtonWidget.calculateWidth(d.getLabel())).sum()) + (row.size() - 1) * scaledButtonSpacing;
                int startX = (this.width / 2) - (rowWidth / 2);

                int buttonX = startX;
                for (MacroButtonData data : row) {
                    MacroButtonWidget macroButton = new MacroButtonWidget(
                            buttonX, currentY,
                            data,
                            btn -> runCommand(data.getCommand())
                    );
                    this.addRenderableWidget(macroButton);
                    buttonX += macroButton.getWidth() + scaledButtonSpacing;
                }
                currentY += scaledButtonHeight + scaledButtonSpacingY;
            }
        }

        // Кнопки керування
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

        int startX_control = this.width / 2 - totalControlButtonsWidth / 2;

        this.addRenderableWidget(Button.builder(
                addButtonText,
                btn -> this.minecraft.setScreen(new AddMacroScreen(this))
        ).bounds(startX_control, controlButtonY, addWidth, scaledButtonHeight).build());

        startX_control += addWidth + MacroButtonWidget.PADDING_X;
        this.addRenderableWidget(Button.builder(
                editButtonText,
                btn -> this.minecraft.setScreen(new SelectMacroScreen(this, SelectMacroScreen.Mode.EDIT))
        ).bounds(startX_control, controlButtonY, editWidth, scaledButtonHeight).build());

        startX_control += editWidth + MacroButtonWidget.PADDING_X;
        this.addRenderableWidget(Button.builder(
                deleteButtonText,
                btn -> this.minecraft.setScreen(new SelectMacroScreen(this, SelectMacroScreen.Mode.DELETE))
        ).bounds(startX_control, controlButtonY, deleteWidth, scaledButtonHeight).build());
    }

    private void runCommand(String command) {
        if (minecraft != null && minecraft.getConnection() != null) {
            double delaySeconds = ModConfig.getCommandDelaySeconds();
            if (delaySeconds > 0) {
                // Виправлений рядок: додаємо TimeUnit.MILLISECONDS
                MacroMenu.SCHEDULER.schedule(() -> {
                    if (minecraft.getConnection() != null) {
                        minecraft.getConnection().sendCommand(command.startsWith("/") ? command.substring(1) : command);
                    }
                }, (long) (delaySeconds * 1000), java.util.concurrent.TimeUnit.MILLISECONDS);
            } else {
                minecraft.getConnection().sendCommand(command.startsWith("/") ? command.substring(1) : command);
            }
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}