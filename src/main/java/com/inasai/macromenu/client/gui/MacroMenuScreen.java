package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.client.gui.macros.AddMacroScreen;
import com.inasai.macromenu.client.gui.macros.MacroButtonWidget;
import com.inasai.macromenu.client.gui.macros.SelectMacroScreen;
import com.inasai.macromenu.client.gui.tabs.AddTabScreen;
import com.inasai.macromenu.client.gui.tabs.ConfirmDeleteTabScreen;
import com.inasai.macromenu.client.gui.tabs.EditTabScreen;
import com.inasai.macromenu.client.gui.tabs.TabButton;
import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import com.inasai.macromenu.MacroMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        // --- Кнопки керування вкладками та налаштування ---
        int smallButtonWidth = scaledButtonHeight; // Робимо кнопки квадратними
        int buttonY = 10;
        int currentX = this.width - 10 - smallButtonWidth;

        // Кнопка налаштувань (іконка шестерні)
        this.addRenderableWidget(Button.builder(
                Component.literal("⚙"), // Простіший символ шестерні
                btn -> this.minecraft.setScreen(new ModSettingsScreen(this))
        ).bounds(currentX, buttonY, smallButtonWidth, scaledButtonHeight).build());

        currentX -= smallButtonWidth + 5;

        // Кнопка "Видалити вкладку" (іконка кошика)
        if (ModConfig.getTabs().size() > 1) { // Можна видалити, лише якщо є більше однієї вкладки
            this.addRenderableWidget(Button.builder(
                    Component.literal("×"), // Простий символ "x"
                    btn -> this.minecraft.setScreen(new ConfirmDeleteTabScreen(this, ModConfig.getActiveTab().name))
            ).bounds(currentX, buttonY, smallButtonWidth, scaledButtonHeight).build());
            currentX -= smallButtonWidth + 5;
        }

        // Кнопка "Редагувати вкладку"
        this.addRenderableWidget(Button.builder(
                Component.literal("!"), // Простий символ "!"
                btn -> this.minecraft.setScreen(new EditTabScreen(this))
        ).bounds(currentX, buttonY, smallButtonWidth, scaledButtonHeight).build());

        currentX -= smallButtonWidth + 5;

        // Кнопка "Додати вкладку" (іконка плюсика)
        this.addRenderableWidget(Button.builder(
                Component.literal("+"), // Простий символ "+"
                btn -> this.minecraft.setScreen(new AddTabScreen(this))
        ).bounds(currentX, buttonY, smallButtonWidth, scaledButtonHeight).build());

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

        List<MacroButtonData> buttonDataList = ModConfig.getButtons();
        int availableWidth = this.width - scaledButtonSpacing * 2;

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

        // --- Кнопки керування макросами ---
        Component addButtonText = Component.translatable("macromenu.gui.add");
        Component editButtonText = Component.translatable("macromenu.gui.edit");
        Component deleteButtonText = Component.translatable("macromenu.gui.delete");

        int addWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);
        int editWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);
        int deleteWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);

        addWidth = Math.max(addWidth, this.font.width(addButtonText) + MacroButtonWidget.PADDING_X * 2);
        editWidth = Math.max(editWidth, this.font.width(editButtonText) + MacroButtonWidget.PADDING_X * 2);
        deleteWidth = Math.max(deleteWidth, this.font.width(deleteButtonText) + MacroButtonWidget.PADDING_X * 2);

        int totalControlButtonsWidth = addWidth + (buttonDataList.isEmpty() ? 0 : editWidth + deleteWidth) + (buttonDataList.isEmpty() ? 0 : MacroButtonWidget.PADDING_X * 2);
        int controlButtonY = this.height - 40;
        int startX_control = this.width / 2 - totalControlButtonsWidth / 2;

        this.addRenderableWidget(Button.builder(
                addButtonText,
                btn -> this.minecraft.setScreen(new AddMacroScreen(this))
        ).bounds(startX_control, controlButtonY, addWidth, scaledButtonHeight).build());

        if (!buttonDataList.isEmpty()) {
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
    }

    private void runCommand(String command) {
        if (minecraft != null && minecraft.getConnection() != null) {
            double delaySeconds = ModConfig.getCommandDelaySeconds();
            if (delaySeconds > 0) {
                MacroMenu.SCHEDULER.schedule(() -> {
                    if (minecraft.getConnection() != null) {
                        minecraft.getConnection().sendCommand(command.startsWith("/") ? command.substring(1) : command);
                    }
                }, (long) (delaySeconds * 1000), TimeUnit.MILLISECONDS);
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