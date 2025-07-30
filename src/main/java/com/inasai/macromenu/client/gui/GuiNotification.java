package com.inasai.macromenu.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class GuiNotification {
    private final Component title;
    private final Component message;
    private final int color;
    private final long createTime;
    private final int duration; // Тривалість повідомлення в мілісекундах

    public GuiNotification(Component title, Component message, int color, int duration) {
        this.title = title;
        this.message = message;
        this.color = color;
        this.createTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTime > duration;
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        long elapsed = System.currentTimeMillis() - createTime;
        float alpha = 1.0f;

        // Згасання на початку та в кінці
        if (elapsed < 500) {
            alpha = elapsed / 500.0f;
        } else if (elapsed > duration - 500) {
            alpha = 1.0f - (elapsed - (duration - 500)) / 500.0f;
        }

        int alphaInt = (int)(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F);
        if (alphaInt < 10) return; // Не малюємо, якщо майже прозорий

        int boxColor = (alphaInt << 24) | 0x000000;
        int textColor = (alphaInt << 24) | (color & 0xFFFFFF);

        int textX = x + 5;
        int textY = y + 5;

        // Малюємо фон
        guiGraphics.fill(x, y, x + width, y + 35, boxColor);

        // Малюємо текст
        guiGraphics.drawString(minecraft.font, title, textX, textY, textColor, false);
        guiGraphics.drawString(minecraft.font, message, textX, textY + 12, textColor, false);
    }
}