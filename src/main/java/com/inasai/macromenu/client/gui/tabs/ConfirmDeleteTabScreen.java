package com.inasai.macromenu.client.gui.tabs;

import com.inasai.macromenu.client.gui.BaseMacroScreen;
import com.inasai.macromenu.client.gui.MacroMenuScreen;
import com.inasai.macromenu.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfirmDeleteTabScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private final String tabName;

    public ConfirmDeleteTabScreen(Screen parentScreen, String tabName) {
        super(Component.translatable("screen.macromenu.confirm_delete_tab"));
        this.parentScreen = parentScreen;
        this.tabName = tabName;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int buttonWidth = 95;
        int buttonHeight = (int) (20 * ModConfig.getButtonSize().getScale());
        int centerX = this.width / 2;

        Component confirmText = Component.translatable("macromenu.gui.confirm_delete_tab_message", Component.literal(tabName));
        int textWidth = this.font.width(confirmText);
        int textX = this.width / 2 - textWidth / 2;
        int textY = this.height / 2 - 20;

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.yes"),
                btn -> {
                    ModConfig.removeTab(tabName);
                    // Переходимо на першу вкладку після видалення поточної
                    ModConfig.setActiveTab(ModConfig.getTabs().get(0).name);
                    this.minecraft.setScreen(new MacroMenuScreen());
                }
        ).bounds(centerX - buttonWidth - 10, this.height / 2 + 20, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.no"),
                btn -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX + 10, this.height / 2 + 20, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        Component confirmText = Component.translatable("macromenu.gui.confirm_delete_tab_message", Component.literal(tabName));
        guiGraphics.drawCenteredString(this.font, confirmText, this.width / 2, this.height / 2 - 10, 0xFFFFFF);
    }
}