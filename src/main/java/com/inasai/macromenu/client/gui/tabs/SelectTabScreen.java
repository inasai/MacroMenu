package com.inasai.macromenu.client.gui.tabs;

import com.inasai.macromenu.client.gui.BaseMacroScreen;
import com.inasai.macromenu.client.gui.NotificationManager;
import com.inasai.macromenu.config.ModConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SelectTabScreen extends BaseMacroScreen {
    private final Screen parentScreen;
    private final int macroIndex;

    public SelectTabScreen(Screen parentScreen, int macroIndex) {
        super(Component.translatable("screen.macromenu.select_tab"));
        this.parentScreen = parentScreen;
        this.macroIndex = macroIndex;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int buttonHeight = (int) (20 * ModConfig.getButtonSize().getScale());
        int buttonWidth = 200;
        int centerX = this.width / 2;
        int currentY = this.height / 4;
        int spacing = 24;

        // Показуємо всі вкладки, крім поточної
        String currentTabName = ModConfig.getActiveTab().name;
        // ВИПРАВЛЕНО: Змінено ModConfig.Config.getTabs() на ModConfig.getTabs()
        for (ModConfig.TabConfig tab : ModConfig.getTabs()) {
            if (!tab.name.equals(currentTabName)) {
                this.addRenderableWidget(Button.builder(
                        Component.literal(tab.name),
                        btn -> {
                            ModConfig.moveMacro(currentTabName, macroIndex, tab.name);
                            // ВИПРАВЛЕНО: Змінено addSuccessNotification на showSuccess
                            NotificationManager.showSuccess(
                                    Component.translatable("macromenu.notification.success.title"),
                                    Component.translatable("macromenu.notification.macro_moved", tab.name)
                            );
                            this.minecraft.setScreen(parentScreen);
                        }
                ).bounds(centerX - buttonWidth / 2, currentY, buttonWidth, buttonHeight).build());
                currentY += spacing;
            }
        }

        // Кнопка "Відмінити"
        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.cancel"),
                btn -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX - buttonWidth / 2, this.height - 40, buttonWidth, buttonHeight).build());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}