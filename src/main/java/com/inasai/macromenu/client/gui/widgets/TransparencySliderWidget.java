package com.inasai.macromenu.client.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.function.Consumer;

public class TransparencySliderWidget extends AbstractWidget {

    private double value;
    private final Consumer<Double> onValueChange;
    private boolean isDragging;

    public TransparencySliderWidget(int x, int y, int width, int height, double initialValue, Consumer<Double> onValueChange) {
        super(x, y, width, height, Component.empty());
        this.value = Mth.clamp(initialValue, 0.0D, 1.0D);
        this.onValueChange = onValueChange;
        updateMessage();
    }

    private void updateMessage() {
        int percentage = (int) (this.value * 100.0D);
        this.setMessage(Component.translatable("macromenu.gui.background_transparency", percentage));
    }

    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 20, 200, 20, 0, 46);

        int textureY = 46;
        if (this.isHoveredOrFocused() || this.isDragging) {
            textureY = 66;
        }

        int sliderX = this.getX() + (int)(this.value * (double)(this.getWidth() - 8));

        guiGraphics.blitNineSliced(WIDGETS_LOCATION,
                sliderX,
                this.getY(),
                8,
                this.getHeight(),
                20, 20, 200, 20,
                0, textureY
        );

        guiGraphics.drawCenteredString(minecraft.font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 8) / 2, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isMouseOver(mouseX, mouseY)) {
            this.setValueFromMouse(mouseX);
            this.isDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isDragging) {
            this.setValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void setValueFromMouse(double mouseX) {
        double newValue = (mouseX - (double)(this.getX() + 4)) / (double)(this.getWidth() - 8);
        this.value = Mth.clamp(newValue, 0.0D, 1.0D);
        updateMessage();
        onValueChange.accept(this.value);
    }

    public double getValue() {
        return this.value;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
        // ВИПРАВЛЕНО: Змінено NarratedElementType.VALUE на NarratedElementType.POSITION
        narrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("gui.slider.value", (int)(this.value * 100)));
        // Якщо `gui.slider.value` не існує у ваших локалізаціях, можна використати:
        // narrationElementOutput.add(NarratedElementType.POSITION, Component.literal(String.valueOf((int)(this.value * 100)) + "%"));
    }
}