package com.inasai.macromenu.client.gui;

import com.inasai.macromenu.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

import com.inasai.macromenu.client.gui.CommandDelaySliderWidget; // Додай цей рядок

public class ModSettingsScreen extends Screen {
    private final Screen parentScreen;
    private Button toggleTooltipsButton;
    private TransparencySliderWidget transparencySlider;
    private CycleButton<ModConfig.ButtonSize> buttonSizeCycleButton;
    private CommandDelaySliderWidget commandDelaySlider;

    public ModSettingsScreen(Screen parentScreen) {
        super(Component.translatable("screen.macromenu.settings_title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int startY = this.height / 4;
        int buttonWidth = 200;
        int buttonHeight = MacroButtonWidget.BASE_BUTTON_HEIGHT;
        int spacing = 24;

        // --- Кнопка перемикання підказок ---
        this.toggleTooltipsButton = Button.builder(
                getTooltipButtonText(),
                btn -> {
                    ModConfig.setShowTooltips(!ModConfig.showTooltips());
                    btn.setMessage(getTooltipButtonText());
                }
        ).bounds(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.toggleTooltipsButton);

        // --- Слайдер прозорості фону ---
        this.transparencySlider = new TransparencySliderWidget(
                centerX - buttonWidth / 2,
                startY + spacing,
                buttonWidth,
                buttonHeight,
                ModConfig.getBackgroundTransparency(),
                value -> ModConfig.setBackgroundTransparency(value)
        );
        this.addRenderableWidget(this.transparencySlider);

        // --- Кнопка циклічного перемикання розміру кнопок ---
        this.buttonSizeCycleButton = CycleButton.builder(
                        (ModConfig.ButtonSize buttonSizeEnum) ->
                                Component.translatable("macromenu.gui.button_size",
                                        Component.translatable("macromenu.gui.button_size." + buttonSizeEnum.name().toLowerCase()))
                )
                .withValues(ModConfig.ButtonSize.values())
                .withInitialValue(ModConfig.getButtonSize())
                .create(centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight,
                        Component.translatable("macromenu.gui.button_size",
                                Component.translatable("macromenu.gui.button_size." + ModConfig.getButtonSize().name().toLowerCase())),
                        (button, currentValue) -> {
                            ModConfig.ButtonSize nextSize = currentValue.next();
                            ModConfig.setButtonSize(nextSize);
                            button.setMessage(Component.translatable("macromenu.gui.button_size",
                                    Component.translatable("macromenu.gui.button_size." + nextSize.name().toLowerCase())));
                            this.minecraft.setScreen(new ModSettingsScreen(this.parentScreen));
                        });
        this.addRenderableWidget(this.buttonSizeCycleButton);

        // --- Слайдер затримки виконання команди ---
        this.commandDelaySlider = new CommandDelaySliderWidget(
                centerX - buttonWidth / 2,
                startY + spacing * 3,
                buttonWidth,
                buttonHeight,
                // Передаємо поточне значення затримки, нормалізоване до діапазону 0.0-1.0
                ModConfig.getCommandDelaySeconds() / 60.0D,
                // Consumer, який буде викликаний, коли значення слайдера зміниться
                value -> ModConfig.setCommandDelaySeconds(Mth.clamp(value, 0.0D, 60.0D))
        );
        this.addRenderableWidget(this.commandDelaySlider);

        // --- Кнопка "Назад" ---
        this.addRenderableWidget(Button.builder(
                Component.translatable("macromenu.gui.back"),
                btn -> this.minecraft.setScreen(this.parentScreen)
        ).bounds(centerX - 100, this.height - 40, 200, buttonHeight).build());
    }

    private Component getTooltipButtonText() {
        return Component.translatable(
                "macromenu.gui.show_tooltips",
                Component.translatable(ModConfig.showTooltips() ? "macromenu.gui.enabled" : "macromenu.gui.disabled")
        );
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // --- РУЧНА РЕАЛІЗАЦІЯ ПІДКАЗКИ ДЛЯ КНОПКИ РОЗМІРУ ---
        if (this.buttonSizeCycleButton.isHoveredOrFocused()) {
            ModConfig.ButtonSize currentSize = ModConfig.getButtonSize();
            Component tooltipText = Component.translatable("macromenu.gui.button_size." + currentSize.name().toLowerCase() + ".tooltip");
            guiGraphics.renderTooltip(font, tooltipText, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}