package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.client.gui.macros.AddMacroScreen;
import com.inasai.macromenu.client.gui.macros.ConfirmDeleteScreen;
import com.inasai.macromenu.client.gui.macros.EditMacroScreen;
import com.inasai.macromenu.client.gui.macros.MacroButtonWidget;
import com.inasai.macromenu.client.gui.tabs.AddTabScreen;
import com.inasai.macromenu.client.gui.tabs.ConfirmDeleteTabScreen;
import com.inasai.macromenu.client.gui.tabs.TabButton;
import com.inasai.macromenu.config.ModConfig;
import com.inasai.macromenu.data.MacroButtonData;
import com.inasai.macromenu.MacroMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
// net.minecraft.resources.ResourceLocation більше не потрібен для фону цього екрану

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class MacroMenuScreen extends BaseMacroScreen {

    private static final int BASE_BUTTON_SPACING_Y = 24;
    private static final int TOP_AREA_HEIGHT = 45;
    private static final int BOTTOM_AREA_HEIGHT = 45;
    private TabButton activeTabButton;

    public MacroMenuScreen() {
        super(Component.translatable("screen.macromenu.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        double currentScale = ModConfig.getButtonSize().getScale();
        int scaledButtonHeight = (int) (MacroButtonWidget.BASE_BUTTON_HEIGHT * currentScale);
        int scaledButtonSpacingY = (int) (BASE_BUTTON_SPACING_Y * currentScale);

        int smallButtonWidth = scaledButtonHeight;
        int buttonY = (TOP_AREA_HEIGHT - scaledButtonHeight) / 2;
        int currentX = this.width - 10 - smallButtonWidth;

        if (ModConfig.getTabs().size() > 1) {
            this.addRenderableWidget(Button.builder(
                    Component.literal("×"),
                    btn -> this.minecraft.setScreen(new ConfirmDeleteTabScreen(this, ModConfig.getActiveTab().name))
            ).bounds(currentX, buttonY, smallButtonWidth, scaledButtonHeight).build());
            currentX -= smallButtonWidth + 5;
        }

        Button renameTabButton = Button.builder(
                Component.literal("✎"), // Символ олівця для редагування
                btn -> {
                    if (activeTabButton != null) {
                        activeTabButton.enterEditMode();
                    }
                }
        ).bounds(currentX, buttonY, smallButtonWidth, scaledButtonHeight).build();
        this.addRenderableWidget(renameTabButton);
        currentX -= smallButtonWidth + 5;

        this.addRenderableWidget(Button.builder(
                Component.literal("+"),
                btn -> this.minecraft.setScreen(new AddTabScreen(this))
        ).bounds(currentX, buttonY, smallButtonWidth, scaledButtonHeight).build());

        int tabButtonWidth = 80;
        int tabButtonY = buttonY;
        int tabButtonX = 10;

        for (ModConfig.TabConfig tab : ModConfig.getTabs()) {
            TabButton newTabButton = new TabButton(
                    tabButtonX, tabButtonY, tabButtonWidth, scaledButtonHeight,
                    Component.literal(tab.name),
                    btn -> {
                        ModConfig.setActiveTab(tab.name);
                        this.minecraft.setScreen(new MacroMenuScreen());
                    },
                    tab.name.equals(ModConfig.getActiveTab().name),
                    this
            );
            this.addRenderableWidget(newTabButton);
            if (tab.name.equals(ModConfig.getActiveTab().name)) {
                activeTabButton = newTabButton;
            }
            tabButtonX += tabButtonWidth + 5;
        }

        List<MacroButtonData> buttonDataList = ModConfig.getButtons();
        int middleAreaHeight = this.height - TOP_AREA_HEIGHT - BOTTOM_AREA_HEIGHT;
        int scaledButtonWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH * currentScale);
        int scaledIconSize = scaledButtonHeight;
        int totalMacroButtonWidth = scaledButtonWidth + 2 * scaledIconSize + 2 * MacroButtonWidget.BASE_BUTTON_SPACING;

        if (buttonDataList.isEmpty()) {
            Component noMacrosText = Component.translatable("macromenu.gui.no_macros_message");
            int textWidth = this.font.width(noMacrosText);
            int calculatedWidth = Math.max((int)(MacroButtonWidget.MIN_BUTTON_WIDTH * currentScale), textWidth + MacroButtonWidget.PADDING_X * 2);
            int startX = this.width / 2 - calculatedWidth / 2;
            int startY = TOP_AREA_HEIGHT + (middleAreaHeight - scaledButtonHeight) / 2;

            this.addRenderableWidget(Button.builder(
                    noMacrosText,
                    btn -> {}
            ).bounds(startX, startY, calculatedWidth, scaledButtonHeight).build());
        } else {
            int buttonY_middle = TOP_AREA_HEIGHT + MacroButtonWidget.PADDING_X;
            for (int i = 0; i < buttonDataList.size(); i++) {
                MacroButtonData data = buttonDataList.get(i);
                final int finalI = i;

                int macroButtonX = this.width / 2 - totalMacroButtonWidth / 2;
                int iconButtonX = macroButtonX + scaledButtonWidth + MacroButtonWidget.BASE_BUTTON_SPACING;

                this.addRenderableWidget(new MacroButtonWidget(
                        macroButtonX,
                        buttonY_middle,
                        data,
                        data.getColor(),
                        this // Передаємо посилання на MacroMenuScreen
                ));

                this.addRenderableWidget(Button.builder(
                        Component.literal("!"),
                        btn -> this.minecraft.setScreen(new EditMacroScreen(this, finalI, data))
                ).bounds(iconButtonX, buttonY_middle, scaledIconSize, scaledIconSize).build());

                this.addRenderableWidget(Button.builder(
                        Component.literal("×"),
                        btn -> this.minecraft.setScreen(new ConfirmDeleteScreen(this, finalI, data))
                ).bounds(iconButtonX + scaledIconSize + MacroButtonWidget.BASE_BUTTON_SPACING, buttonY_middle, scaledIconSize, scaledIconSize).build());

                buttonY_middle += scaledButtonHeight + scaledButtonSpacingY;
            }
        }

        Component addButtonText = Component.translatable("macromenu.gui.add");
        Component settingsButtonText = Component.translatable("macromenu.gui.settings");

        int addWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);
        int settingsWidth = (int) (MacroButtonWidget.BASE_BUTTON_WIDTH / 2 * currentScale);
        addWidth = Math.max(addWidth, this.font.width(addButtonText) + MacroButtonWidget.PADDING_X * 2);
        settingsWidth = Math.max(settingsWidth, this.font.width(settingsButtonText) + MacroButtonWidget.PADDING_X * 2);

        int controlButtonY = this.height - BOTTOM_AREA_HEIGHT + (BOTTOM_AREA_HEIGHT - scaledButtonHeight) / 2;

        this.addRenderableWidget(Button.builder(
                addButtonText,
                btn -> this.minecraft.setScreen(new AddMacroScreen(this))
        ).bounds(this.width / 2 - addWidth / 2, controlButtonY, addWidth, scaledButtonHeight).build());

        this.addRenderableWidget(Button.builder(
                settingsButtonText,
                btn -> this.minecraft.setScreen(new ModSettingsScreen(this))
        ).bounds(this.width - settingsWidth - 10, controlButtonY, settingsWidth, scaledButtonHeight).build());
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Рендеримо весь фон екрану з прозорістю з ModConfig
        int alpha = (int) (ModConfig.getBackgroundTransparency() * 255.0D);
        int backgroundColor = (alpha << 24) | (0x000000); // Чорний колір з заданою прозорістю
        guiGraphics.fill(0, 0, this.width, this.height, backgroundColor);

        // Малюємо заголовок
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xFFFFFF);

        // Викликаємо супер-метод для рендерингу віджетів (кнопки, вкладки тощо)
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void runCommand(String command) {
        if (minecraft != null && minecraft.getConnection() != null) {
            double delaySeconds = ModConfig.getCommandDelaySeconds();
            if (delaySeconds > 0) {
                MacroMenu.SCHEDULER.schedule(() -> {
                    if (minecraft.getConnection() != null) {
                        minecraft.getConnection().sendCommand(command.startsWith("/") ? command.substring(1) : command);
                    }
                }, (long) (delaySeconds * 1000), TimeUnit.MILLISECONDS);
            } else {
                minecraft.getConnection().sendCommand(command.startsWith("/") ? command.substring(1) : command);
            }
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}