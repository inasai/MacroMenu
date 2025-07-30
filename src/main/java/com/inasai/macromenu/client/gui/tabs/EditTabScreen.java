package com.inasai.macromenu.client.gui.tabs;

import com.inasai.macromenu.client.gui.BaseMacroScreen;
import com.inasai.macromenu.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditTabScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private EditBox nameEditBox;
    private final String oldTabName;

    public EditTabScreen(Screen parentScreen) {
        super(Component.translatable("screen.macromenu.edit_tab"));
        this.parentScreen = parentScreen;
        this.oldTabName = ModConfig.getActiveTab().name;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int buttonWidth = 95;
        int buttonHeight = (int) (20 * ModConfig.getButtonSize().getScale());
        int centerX = this.width / 2;
        int startY = this.height / 2 - 20;

        this.nameEditBox = new EditBox(this.font, centerX - 100, startY, 200, 20, Component.literal(oldTabName));
        this.nameEditBox.setMaxLength(30);
        this.addWidget(this.nameEditBox);
        this.nameEditBox.setValue(oldTabName);

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.save"),
                btn -> {
                    String newName = nameEditBox.getValue();
                    if (!newName.isEmpty() && !newName.equals(oldTabName)) {
                        ModConfig.renameTab(oldTabName, newName);
                        this.minecraft.setScreen(parentScreen);
                    } else {
                        this.minecraft.setScreen(parentScreen);
                    }
                }
        ).bounds(centerX - buttonWidth - 5, this.height - 40, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.cancel"),
                btn -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX + 5, this.height - 40, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.nameEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}