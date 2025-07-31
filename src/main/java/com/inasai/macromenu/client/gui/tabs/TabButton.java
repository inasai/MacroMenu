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

    private final String originalTabName;
    private final boolean isActive;
    private EditBox nameEditBox;
    private boolean isEditing = false;
    private final MacroMenuScreen parentScreen;
    private final Minecraft minecraft;

    public TabButton(int x, int y, int width, int height, Component message, OnPress onPress, boolean isActive, MacroMenuScreen parentScreen) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.originalTabName = message.getString();
        this.isActive = isActive;
        this.parentScreen = parentScreen;
        this.minecraft = Minecraft.getInstance();
    }

    public void enterEditMode() {
        this.isEditing = true;
        this.nameEditBox = new EditBox(this.minecraft.font, this.getX(), this.getY(), this.getWidth(), this.getHeight(), Component.literal(originalTabName));
        this.nameEditBox.setMaxLength(30);
        this.nameEditBox.setValue(this.originalTabName);
        this.nameEditBox.setFocused(true); // ВИПРАВЛЕНО: Змінено на setFocused
    }

    private void saveChanges() {
        if (this.isEditing) {
            String newName = nameEditBox.getValue();
            if (!newName.isEmpty() && !newName.equals(originalTabName)) {
                ModConfig.renameTab(originalTabName, newName);
            }
            this.isEditing = false;
            // Перезавантажуємо екран, щоб оновити вкладки
            this.minecraft.setScreen(this.parentScreen); // ВИПРАВЛЕНО: Використання поля minecraft
        }
    }

    @Override
    public void onPress() {
        if (isEditing) {
            saveChanges();
        } else {
            super.onPress();
        }
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (isEditing) {
            this.nameEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        } else {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
            if (this.isActive) {
                guiGraphics.fill(this.getX(), this.getY() + this.getHeight() - 1, this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isEditing) {
            if (nameEditBox.isMouseOver(mouseX, mouseY) && button == 0) {
                return nameEditBox.mouseClicked(mouseX, mouseY, button);
            }
            if (!isMouseOver(mouseX, mouseY)) {
                saveChanges();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isEditing) {
            if (keyCode == 257 || keyCode == 335) { // Enter or Numpad Enter
                saveChanges();
                return true;
            }
            if (keyCode == 256) { // Escape
                this.isEditing = false;
                this.minecraft.setScreen(this.parentScreen); // ВИПРАВЛЕНО: Використання поля minecraft
                return true;
            }
            return this.nameEditBox.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (isEditing) {
            return this.nameEditBox.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }
}