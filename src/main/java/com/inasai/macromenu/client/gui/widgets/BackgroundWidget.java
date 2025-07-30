package com.inasai.macromenu.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BackgroundWidget extends AbstractWidget {

    private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft", "textures/block/dirt.png");
    private final boolean isDirt;
    private final int color;

    public BackgroundWidget(int x, int y, int width, int height, boolean isDirt, int color) {
        super(x, y, width, height, Component.empty());
        this.isDirt = isDirt;
        this.color = color;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (isDirt) {
            guiGraphics.blit(DIRT_TEXTURE, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), 32, 32);
        } else {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // Не потребує озвучення
    }
}