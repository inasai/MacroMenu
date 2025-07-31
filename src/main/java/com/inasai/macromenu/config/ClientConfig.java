package com.inasai.macromenu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inasai.macromenu.MacroMenu;
import com.inasai.macromenu.client.gui.themes.Theme;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "macromenu-client.json");

    public static Theme.ThemeType currentTheme = Theme.ThemeType.CLASSIC;
    public static boolean showTooltips = true;
    public static double backgroundTransparency = 0.5;
    public static ModConfig.ButtonSize buttonSize = ModConfig.ButtonSize.MEDIUM;
    public static double commandDelaySeconds = 0.0;

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            saveConfig();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ClientConfig loadedConfig = GSON.fromJson(reader, ClientConfig.class);
            if (loadedConfig != null) {
                currentTheme = loadedConfig.currentTheme;
                showTooltips = loadedConfig.showTooltips;
                backgroundTransparency = loadedConfig.backgroundTransparency;
                buttonSize = loadedConfig.buttonSize;
                commandDelaySeconds = loadedConfig.commandDelaySeconds;
            }
        } catch (IOException e) {
            MacroMenu.LOGGER.error("Failed to load MacroMenu client config", e);
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new ClientConfig(), writer);
        } catch (IOException e) {
            MacroMenu.LOGGER.error("Failed to save MacroMenu client config", e);
        }
    }

    // ВИПРАВЛЕНО: Додані методи-сеттери
    public static void setShowTooltips(boolean value) {
        showTooltips = value;
    }

    public static void setBackgroundTransparency(double value) {
        backgroundTransparency = value;
    }

    public static void setButtonSize(ModConfig.ButtonSize size) {
        buttonSize = size;
    }

    public static void setCommandDelaySeconds(double value) {
        commandDelaySeconds = value;
    }
}