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
import net.minecraft.client.gui.GuiGraphics;
import javax.annotation.Nonnull;

public class EditMacroScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private final int macroIndex;
    private EditBox labelEditBox;
    private EditBox commandEditBox;
    private EditBox colorEditBox;
    private EditBox descriptionEditBox;
    private final MacroButtonData oldMacroData;

    public EditMacroScreen(Screen parentScreen, int macroIndex, MacroButtonData macro) {
        super(Component.translatable("screen.macromenu.edit_title"));
        this.parentScreen = parentScreen;
        this.macroIndex = macroIndex;
        this.oldMacroData = macro;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int fieldWidth = 300;
        int fieldHeight = 20;
        int startY = 60;
        int spacing = 30;
        int buttonWidth = 100;
        int buttonHeight = (int) (20 * ModConfig.getButtonSize().getScale());

        this.labelEditBox = new EditBox(this.font, centerX - 150, startY, fieldWidth, fieldHeight, Component.translatable("macromenu.gui.label"));
        this.labelEditBox.setMaxLength(32);
        this.labelEditBox.setValue(oldMacroData.getLabel());
        this.addRenderableWidget(this.labelEditBox);

        this.commandEditBox = new EditBox(this.font, centerX - 150, startY + spacing, fieldWidth, fieldHeight, Component.translatable("macromenu.gui.command"));
        this.commandEditBox.setMaxLength(256);
        this.commandEditBox.setValue(oldMacroData.getCommand());
        this.addRenderableWidget(this.commandEditBox);

        this.colorEditBox = new EditBox(this.font, centerX - 150, startY + spacing * 2, fieldWidth, fieldHeight, Component.translatable("macromenu.gui.color_hex"));
        this.colorEditBox.setMaxLength(8);
        this.colorEditBox.setValue(Integer.toHexString(oldMacroData.getColor()).toUpperCase());
        this.addRenderableWidget(this.colorEditBox);

        this.descriptionEditBox = new EditBox(this.font, centerX - 150, startY + spacing * 3, fieldWidth, fieldHeight, Component.translatable("macromenu.gui.description"));
        this.descriptionEditBox.setMaxLength(256);
        this.descriptionEditBox.setValue(oldMacroData.getDescription());
        this.addRenderableWidget(this.descriptionEditBox);

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.save"),
                btn -> {
                    String newLabel = labelEditBox.getValue();
                    String newCommand = commandEditBox.getValue();
                    String newColorHex = colorEditBox.getValue();
                    String newDescription = descriptionEditBox.getValue();

                    if (!newLabel.isEmpty() && !newCommand.isEmpty()) {
                        int newColor = parseColor(newColorHex);
                        ModConfig.updateMacro(macroIndex, new MacroButtonData(newLabel, newCommand, newColor, newDescription));
                        NotificationManager.showSuccess(
                                Component.translatable("macromenu.notification.success.title"),
                                Component.translatable("macromenu.notification.edit_macro.success", newLabel)
                        );
                        this.minecraft.setScreen(parentScreen);
                    } else {
                        NotificationManager.showError(
                                Component.translatable("macromenu.notification.error.title"),
                                Component.translatable("macromenu.notification.edit_macro.error")
                        );
                    }
                }
        ).bounds(centerX - buttonWidth - 5, this.height - 40, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.cancel"),
                btn -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX + 5, this.height - 40, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.move"),
                btn -> this.minecraft.setScreen(new SelectTabScreen(this, macroIndex))
        ).bounds(centerX - buttonWidth / 2, this.height - 70, buttonWidth, buttonHeight).build());
    }

    private int parseColor(String hex) {
        try {
            if (hex.length() == 8 && hex.matches("[0-9a-fA-F]+")) {
                return (int) Long.parseLong(hex, 16);
            }
        } catch (NumberFormatException e) {
        }
        return 0xFFFFFFFF;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.label"), this.width / 2 - 150, 60 - 15, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.command"), this.width / 2 - 150, 60 + 15, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.color_hex"), this.width / 2 - 150, 60 + 45, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("macromenu.gui.description"), this.width / 2 - 150, 60 + 75, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}