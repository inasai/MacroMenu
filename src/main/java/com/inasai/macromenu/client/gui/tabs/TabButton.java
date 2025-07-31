package com.inasai.macromenu.client.gui.tabs;

import com.inasai.macromenu.client.gui.MacroMenuScreen;
import com.inasai.macromenu.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import javax.annotation.Nonnull;

public class TabButton extends Button {
    private final boolean isActive;
    private final MacroMenuScreen parentScreen;
    private EditBox editBox;

    public TabButton(int x, int y, int width, int height, Component message, OnPress onPress, boolean isActive, MacroMenuScreen parentScreen) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.isActive = isActive;
        this.parentScreen = parentScreen;
        this.editBox = new EditBox(Minecraft.getInstance().font, this.getX(), this.getY(), this.getWidth(), this.getHeight(), Component.literal(""));
        this.editBox.setMaxLength(32);
        this.editBox.setValue(message.getString());
        this.editBox.visible = false;
    }

    @Override
    public void onPress() {
        super.onPress();
    }

    public void enterEditMode() {
        if (isActive) {
            this.editBox.visible = true;
            this.editBox.setFocused(true); // Виправлено: викликаємо setFocused на EditBox
            // Не потрібно викликати setFocused на Minecraft.getInstance()
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (editBox.visible) {
            if (editBox.mouseClicked(mouseX, mouseY, button)) {
                return true;
            } else {
                saveTabName();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editBox.visible && editBox.keyPressed(keyCode, scanCode, modifiers)) {
            if (keyCode == 257) { // 257 - клавіша Enter
                saveTabName();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void saveTabName() {
        String newName = editBox.getValue();
        if (!newName.isEmpty() && !newName.equals(getMessage().getString())) {
            ModConfig.renameTab(getMessage().getString(), newName);
            Minecraft.getInstance().setScreen(new MacroMenuScreen());
        }
        editBox.visible = false;
        editBox.setFocused(false); // Виправлено: викликаємо setFocused на EditBox
        // Не потрібно викликати getFocused або setFocused на Minecraft.getInstance()
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

        if (editBox.visible) {
            editBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        } else {
            int textColor = 0xFFFFFF;
            if (isActive) {
                textColor = 0x00CCFF; // Жовтий колір для активної вкладки
            }
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, textColor);
        }
    }
}