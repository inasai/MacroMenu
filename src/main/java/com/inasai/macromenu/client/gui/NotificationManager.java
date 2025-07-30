package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.client.gui.widgets.GuiNotification;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.Queue;

public class NotificationManager {
    private static final Queue<GuiNotification> notifications = new LinkedList<>();

    public static void addNotification(GuiNotification notification) {
        notifications.add(notification);
    }

    public static void renderAll(GuiGraphics guiGraphics) {
        int notificationY = 10;
        int notificationWidth = 250;
        int screenWidth = guiGraphics.guiWidth();

        notifications.removeIf(GuiNotification::isExpired);

        for (GuiNotification notification : notifications) {
            int notificationX = screenWidth - notificationWidth - 10;
            notification.render(guiGraphics, notificationX, notificationY, notificationWidth);
            notificationY += 40;
        }
    }

    public static void showSuccess(Component title, Component message) {
        addNotification(new GuiNotification(title, message, 0x33FF33, 4000));
    }

    public static void showError(Component title, Component message) {
        addNotification(new GuiNotification(title, message, 0xFF3333, 5000));
    }
}