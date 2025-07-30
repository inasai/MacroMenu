package com.inasai.macromenu.client.gui.macros;

import com.inasai.macromenu.client.gui.BaseMacroScreen;
import com.inasai.macromenu.client.gui.NotificationManager;
import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;

public class ConfirmDeleteScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private final int macroIndex;
    private final MacroButtonData macroData;
    private final Component message;

    public ConfirmDeleteScreen(Screen parentScreen, int macroIndex, MacroButtonData macroData) {
        super(Component.translatable("macromenu.gui.confirm_delete_title"));
        this.parentScreen = parentScreen;
        this.macroIndex = macroIndex;
        this.macroData = macroData;
        this.message = Component.translatable("macromenu.gui.confirm_delete_message", macroData.getLabel());
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int buttonWidth = 100;
        int buttonHeight = (int)(MacroButtonWidget.BASE_BUTTON_HEIGHT * ModConfig.getButtonSize().getScale());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.yes"),
                btn -> {
                    ModConfig.removeMacro(macroIndex);
                    NotificationManager.showSuccess(
                            Component.translatable("macromenu.notification.success.title"),
                            Component.translatable("macromenu.notification.delete_macro.success", macroData.getLabel())
                    );
                    this.minecraft.setScreen(new SelectMacroScreen(parentScreen, SelectMacroScreen.Mode.DELETE));
                }
        ).bounds(centerX - buttonWidth - 10, this.height / 2 + 20, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.no"),
                btn -> this.minecraft.setScreen(new SelectMacroScreen(parentScreen, SelectMacroScreen.Mode.DELETE))
        ).bounds(centerX + 10, this.height / 2 + 20, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Викликаємо метод батьківського класу для рендерингу фону та заголовка
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawCenteredString(this.font, this.message, this.width / 2, this.height / 2 - 20, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}