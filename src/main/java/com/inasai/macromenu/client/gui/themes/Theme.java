package com.inasai.macromenu.client.gui.themes;

import net.minecraft.resources.ResourceLocation;

public class Theme {

    public static final ResourceLocation MENU_BACKGROUND = new ResourceLocation("minecraft", "textures/gui/menu_background.png");

    public enum ThemeType {
        CLASSIC,
        DARK,
        CUSTOM
    }

    public static ThemeType currentTheme = ThemeType.CLASSIC;

    public static ResourceLocation getBackgroundTexture() {
        // У майбутньому тут можна буде додати логіку для вибору текстур залежно від теми
        return MENU_BACKGROUND;
    }
}