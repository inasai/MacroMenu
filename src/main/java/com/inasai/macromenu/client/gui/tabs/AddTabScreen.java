package com.inasai.macromenu.client.gui.tabs;

import com.inasai.macromenu.client.gui.BaseMacroScreen;
import com.inasai.macromenu.config.ModConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;

public class AddTabScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private EditBox nameEditBox;

    public AddTabScreen(Screen parentScreen) {
        super(Component.translatable("screen.macromenu.add_tab"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int buttonWidth = 95;
        int buttonHeight = (int) (20 * ModConfig.getButtonSize().getScale());
        int centerX = this.width / 2;
        int startY = this.height / 2 - 20;

        this.nameEditBox = new EditBox(this.font, centerX - 100, startY, 200, 20, Component.empty());
        this.nameEditBox.setMaxLength(30);
        this.addWidget(this.nameEditBox);

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.save"),
                btn -> {
                    if (!nameEditBox.getValue().isEmpty()) {
                        ModConfig.addTab(nameEditBox.getValue());
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