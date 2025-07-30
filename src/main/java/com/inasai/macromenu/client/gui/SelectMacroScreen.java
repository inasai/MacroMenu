package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import javax.annotation.Nonnull;

public class SelectMacroScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private final Mode mode;

    public enum Mode {
        SELECT,
        EDIT,
        DELETE
    }

    public SelectMacroScreen(Screen parentScreen, Mode mode) {
        super(
                mode == Mode.EDIT ? Component.translatable("screen.macromenu.select_title_edit") :
                        mode == Mode.DELETE ? Component.translatable("screen.macromenu.select_title_delete") :
                                mode == Mode.SELECT ? Component.translatable("screen.macromenu.select_title_select") :
                                        Component.translatable("screen.macromenu.select_title")
        );
        this.parentScreen = parentScreen;
        this.mode = mode;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        // Отримуємо кнопки з активної вкладки
        List<MacroButtonData> buttonDataList = ModConfig.getButtons();
        int currentY = this.height / 4;
        int buttonHeight = (int)(MacroButtonWidget.BASE_BUTTON_HEIGHT * ModConfig.getButtonSize().getScale());
        int spacing = 24;

        if (buttonDataList.isEmpty()) {
            int textWidth = this.font.width(Component.translatable("macromenu.gui.no_macros_message"));
            int calculatedWidth = Math.max(MacroButtonWidget.MIN_BUTTON_WIDTH, textWidth + MacroButtonWidget.PADDING_X * 2);
            int startX = this.width / 2 - calculatedWidth / 2;

            this.addRenderableWidget(Button.builder(
                    Component.translatable("macromenu.gui.no_macros_message"),
                    btn -> {}
            ).bounds(startX, currentY, calculatedWidth, buttonHeight).build());
        } else {
            for (int i = 0; i < buttonDataList.size(); i++) {
                final int index = i;
                MacroButtonData data = buttonDataList.get(i);

                MacroButtonWidget macroButton = new MacroButtonWidget(
                        0, 0,
                        data,
                        btn -> {
                            if (mode == Mode.EDIT) {
                                this.minecraft.setScreen(new EditMacroScreen(parentScreen, index, data));
                            } else { // Mode.DELETE
                                this.minecraft.setScreen(new ConfirmDeleteScreen(parentScreen, index, data));
                            }
                        }
                );
                int startX = this.width / 2 - macroButton.getWidth() / 2;
                macroButton.setX(startX);
                macroButton.setY(currentY);

                this.addRenderableWidget(macroButton);
                currentY += spacing;
            }
        }

        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.back"),
                btn -> this.minecraft.setScreen(parentScreen)
        ).bounds(this.width / 2 - 100, this.height - 40, 200, buttonHeight).build());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}