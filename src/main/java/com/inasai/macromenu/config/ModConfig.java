package com.inasai.macromenu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.inasai.macromenu.MacroMenu;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.util.Mth;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve(MacroMenu.MOD_ID);
    private static final Path BUTTONS_FILE = CONFIG_DIR.resolve("buttons.json");
    private static final Path SETTINGS_FILE = CONFIG_DIR.resolve("settings.json");

    private static List<MacroButtonData> buttons = new ArrayList<>();
    private static double backgroundTransparency = 0.5D;
    private static boolean showTooltips = true;
    private static ButtonSize buttonSize = ButtonSize.MEDIUM; // НОВЕ: Розмір кнопок за замовчуванням
    private static double commandDelaySeconds = 0.0D; // НОВЕ: Затримка виконання команди за замовчуванням (0 секунд)

    public static void loadConfigs() {
        if (!CONFIG_DIR.toFile().exists()) {
            CONFIG_DIR.toFile().mkdirs();
        }
        loadButtons();
        loadSettings();
    }

    private static void loadButtons() {
        if (BUTTONS_FILE.toFile().exists()) {
            try (FileReader reader = new FileReader(BUTTONS_FILE.toFile())) {
                Type listType = new TypeToken<ArrayList<MacroButtonData>>() {}.getType();
                buttons = GSON.fromJson(reader, listType);
                if (buttons == null) {
                    buttons = new ArrayList<>();
                }
                MacroMenu.LOGGER.info("Loaded {} macros from {}", buttons.size(), BUTTONS_FILE.getFileName());
            } catch (IOException | JsonSyntaxException e) {
                MacroMenu.LOGGER.error("Failed to load macros from {}. Creating new file. Error: {}", BUTTONS_FILE.getFileName(), e.getMessage());
                buttons = new ArrayList<>();
                saveButtons();
            }
        } else {
            MacroMenu.LOGGER.info("Macro buttons file not found. Creating new one: {}", BUTTONS_FILE.getFileName());
            saveButtons();
        }
    }

    private static void loadSettings() {
        if (SETTINGS_FILE.toFile().exists()) {
            try (FileReader reader = new FileReader(SETTINGS_FILE.toFile())) {
                ModSettings tempSettings = GSON.fromJson(reader, ModSettings.class);
                if (tempSettings != null) {
                    backgroundTransparency = Mth.clamp(tempSettings.backgroundTransparency, 0.0D, 1.0D);
                    showTooltips = tempSettings.showTooltips;
                    // НОВЕ: Завантаження розміру кнопок
                    if (tempSettings.buttonSize != null) {
                        buttonSize = tempSettings.buttonSize;
                    } else {
                        buttonSize = ButtonSize.MEDIUM; // За замовчуванням, якщо відсутнє в файлі
                    }
                    // НОВЕ: Завантаження затримки команди
                    commandDelaySeconds = Mth.clamp(tempSettings.commandDelaySeconds, 0.0D, 60.0D); // Обмежуємо 0-60 секунд
                    MacroMenu.LOGGER.info("Loaded settings from {}: Transparency={}, Tooltips={}, ButtonSize={}, CommandDelay={}",
                            SETTINGS_FILE.getFileName(), backgroundTransparency, showTooltips, buttonSize, commandDelaySeconds);
                } else {
                    MacroMenu.LOGGER.warn("Settings file is empty or malformed. Using default settings.");
                    saveSettings();
                }
            } catch (IOException | JsonSyntaxException e) {
                MacroMenu.LOGGER.error("Failed to load settings from {}. Using default settings. Error: {}", SETTINGS_FILE.getFileName(), e.getMessage());
                saveSettings();
            }
        } else {
            MacroMenu.LOGGER.info("Mod settings file not found. Creating new one: {}", SETTINGS_FILE.getFileName());
            saveSettings();
        }
    }

    public static void saveConfig() {
        saveButtons();
        saveSettings();
    }

    private static void saveButtons() {
        try (FileWriter writer = new FileWriter(BUTTONS_FILE.toFile())) {
            GSON.toJson(buttons, writer);
        } catch (IOException e) {
            MacroMenu.LOGGER.error("Failed to save macros to {}. Error: {}", BUTTONS_FILE.getFileName(), e.getMessage());
        }
    }

    private static void saveSettings() {
        try (FileWriter writer = new FileWriter(SETTINGS_FILE.toFile())) {
            // НОВЕ: Додаємо buttonSize та commandDelaySeconds до ModSettings
            ModSettings currentSettings = new ModSettings(backgroundTransparency, showTooltips, buttonSize, commandDelaySeconds);
            GSON.toJson(currentSettings, writer);
        } catch (IOException e) {
            MacroMenu.LOGGER.error("Failed to save settings to {}. Error: {}", SETTINGS_FILE.getFileName(), e.getMessage());
        }
    }

    // --- Методи для доступу до налаштувань кнопок ---
    public static List<MacroButtonData> getButtons() {
        return buttons;
    }

    public static void addMacro(MacroButtonData data) {
        buttons.add(data);
        saveButtons();
    }

    public static void updateMacro(int index, MacroButtonData newData) {
        if (index >= 0 && index < buttons.size()) {
            buttons.set(index, newData);
            saveButtons();
        }
    }

    public static void removeMacro(int index) {
        if (index >= 0 && index < buttons.size()) {
            buttons.remove(index);
            saveButtons();
        }
    }

    // --- Методи для доступу до налаштувань мода ---
    public static double getBackgroundTransparency() {
        return backgroundTransparency;
    }

    public static void setBackgroundTransparency(double value) {
        backgroundTransparency = Mth.clamp(value, 0.0D, 1.0D);
        saveSettings();
    }

    public static boolean showTooltips() {
        return showTooltips;
    }

    public static void setShowTooltips(boolean value) {
        showTooltips = value;
        saveSettings();
    }

    // НОВЕ: Методи для розміру кнопок
    public static ButtonSize getButtonSize() {
        return buttonSize;
    }

    public static void setButtonSize(ButtonSize size) {
        buttonSize = size;
        saveSettings();
    }

    // НОВЕ: Методи для затримки команди
    public static double getCommandDelaySeconds() {
        return commandDelaySeconds;
    }

    public static void setCommandDelaySeconds(double delay) {
        commandDelaySeconds = Mth.clamp(delay, 0.0D, 60.0D); // Обмежуємо 0-60 секунд
        saveSettings();
    }

    // НОВЕ: Enum для розмірів кнопок
    public enum ButtonSize {
        SMALL(0.8D), // 80% від стандартного розміру
        MEDIUM(1.0D), // 100% від стандартного розміру
        LARGE(1.2D); // 120% від стандартного розміру

        private final double scale;

        ButtonSize(double scale) {
            this.scale = scale;
        }

        public double getScale() {
            return scale;
        }

        // Метод для отримання наступного розміру в циклі
        public ButtonSize next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    // Внутрішній клас для GSON
    private static class ModSettings {
        double backgroundTransparency;
        boolean showTooltips;
        ButtonSize buttonSize; // НОВЕ
        double commandDelaySeconds; // НОВЕ

        public ModSettings(double backgroundTransparency, boolean showTooltips, ButtonSize buttonSize, double commandDelaySeconds) {
            this.backgroundTransparency = backgroundTransparency;
            this.showTooltips = showTooltips;
            this.buttonSize = buttonSize;
            this.commandDelaySeconds = commandDelaySeconds;
        }
    }
}