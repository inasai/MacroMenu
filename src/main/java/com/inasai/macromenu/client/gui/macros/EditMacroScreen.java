package com.inasai.macromenu.client.gui.macros;

import com.inasai.macromenu.client.gui.BaseMacroScreen;
import com.inasai.macromenu.client.gui.NotificationManager;
import com.inasai.macromenu.client.gui.tabs.SelectTabScreen;
import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditMacroScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private final int macroIndex;
    private EditBox labelEditBox;
    private EditBox commandEditBox;

    public EditMacroScreen(Screen parentScreen, int macroIndex, MacroButtonData macro) {
        super(Component.translatable("screen.macromenu.edit_title")); // ВИПРАВЛЕНО: Заголовок екрану
        this.parentScreen = parentScreen;
        this.macroIndex = macroIndex;

        this.labelEditBox = new EditBox(this.font, 0, 0, 190, 20, Component.literal(macro.getLabel()));
        this.commandEditBox = new EditBox(this.font, 0, 0, 190, 20, Component.literal(macro.getCommand()));
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 40;
        int buttonWidth = 95;
        int buttonHeight = (int) (20 * ModConfig.getButtonSize().getScale());

        labelEditBox.setX(centerX - 100);
        labelEditBox.setY(startY);
        this.addWidget(labelEditBox);

        commandEditBox.setX(centerX - 100);
        commandEditBox.setY(startY + 30);
        this.addWidget(commandEditBox);

        // Кнопка "Зберегти"
        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.save"),
                btn -> {
                    String newLabel = labelEditBox.getValue();
                    String newCommand = commandEditBox.getValue();
                    if (!newLabel.isEmpty() && !newCommand.isEmpty()) {
                        ModConfig.updateMacro(macroIndex, new MacroButtonData(newLabel, newCommand));
                        // ВИПРАВЛЕНО: Змінено addSuccessNotification на showSuccess
                        NotificationManager.showSuccess(
                                Component.translatable("macromenu.notification.success.title"),
                                Component.translatable("macromenu.notification.edit_macro.success", newLabel)
                        );
                    } else {
                        // ВИПРАВЛЕНО: Змінено addErrorNotification на showError
                        NotificationManager.showError(
                                Component.translatable("macromenu.notification.error.title"),
                                Component.translatable("macromenu.notification.edit_macro.error")
                        );
                    }
                    this.minecraft.setScreen(parentScreen);
                }
        ).bounds(centerX - buttonWidth - 5, this.height - 40, buttonWidth, buttonHeight).build());

        // Кнопка "Відмінити"
        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.cancel"),
                btn -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX + 5, this.height - 40, buttonWidth, buttonHeight).build());

        // Нова кнопка "Перемістити"
        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.move"),
                btn -> this.minecraft.setScreen(new SelectTabScreen(this, macroIndex))
        ).bounds(centerX - buttonWidth / 2, this.height - 70, 100, buttonHeight).build());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}