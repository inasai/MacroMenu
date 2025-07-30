package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;

public class AddMacroScreen extends BaseMacroScreen {
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

        this.labelEditBox = new EditBox(this.font, centerX - 150, 75, 300, 20, Component.translatable("macromenu.gui.label"));
        this.labelEditBox.setMaxLength(32);
        this.addRenderableWidget(this.labelEditBox);

        this.commandEditBox = new EditBox(this.font, centerX - 150, 125, 300, 20, Component.translatable("macromenu.gui.command"));
        this.commandEditBox.setMaxLength(256);
        this.addRenderableWidget(this.commandEditBox);

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.save"),
                button -> {
                    if (!labelEditBox.getValue().isEmpty() && !commandEditBox.getValue().isEmpty()) {
                        ModConfig.addMacro(new MacroButtonData(labelEditBox.getValue(), commandEditBox.getValue()));
                        NotificationManager.showSuccess(
                                Component.translatable("macromenu.notification.success.title"),
                                Component.translatable("macromenu.notification.add_macro.success", labelEditBox.getValue())
                        );
                        this.minecraft.setScreen(parentScreen);
                    } else {
                        // Додаємо виклик повідомлення про помилку
                        NotificationManager.showError(
                                Component.translatable("macromenu.notification.error.title"),
                                Component.translatable("macromenu.notification.add_macro.error")
                        );
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
        // Викликаємо метод батьківського класу для рендерингу фону та заголовка
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.label"), this.width / 2 - 150, 60, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.command"), this.width / 2 - 150, 110, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}