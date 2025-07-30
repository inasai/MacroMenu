package com.inasai.macromenu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.inasai.macromenu.MacroMenu;
import com.inasai.macromenu.data.MacroButtonData;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TAB_LIST_TYPE = new TypeToken<List<TabConfig>>(){}.getType();
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "macromenu.json");

    // Нова структура для зберігання вкладок
    private static List<TabConfig> tabs = new ArrayList<>();

    // Інші налаштування
    private static boolean showTooltips = true;
    private static double backgroundTransparency = 0.5;
    private static ButtonSize buttonSize = ButtonSize.MEDIUM;
    private static double commandDelaySeconds = 0.0;

    // Нове поле для відстеження активної вкладки
    private static String activeTabName;

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            createDefaultConfig();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            List<TabConfig> loadedTabs = GSON.fromJson(reader, TAB_LIST_TYPE);
            if (loadedTabs != null && !loadedTabs.isEmpty()) {
                tabs = loadedTabs;
            } else {
                createDefaultConfig();
            }
        } catch (IOException e) {
            MacroMenu.LOGGER.error("Failed to load MacroMenu config", e);
            createDefaultConfig();
        }

        // Встановлюємо активну вкладку, якщо вона є
        if (!tabs.isEmpty()) {
            if (activeTabName == null || tabs.stream().noneMatch(tab -> tab.name.equals(activeTabName))) {
                activeTabName = tabs.get(0).name;
            }
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(tabs, writer);
        } catch (IOException e) {
            MacroMenu.LOGGER.error("Failed to save MacroMenu config", e);
        }
    }

    private static void createDefaultConfig() {
        tabs.clear();
        TabConfig defaultTab = new TabConfig("Default", new ArrayList<>());
        tabs.add(defaultTab);
        activeTabName = "Default";
        saveConfig();
    }

    // Новий метод для отримання активної вкладки
    public static TabConfig getActiveTab() {
        return tabs.stream()
                .filter(tab -> tab.name.equals(activeTabName))
                .findFirst()
                .orElse(tabs.isEmpty() ? null : tabs.get(0));
    }

    public static List<TabConfig> getTabs() {
        return Collections.unmodifiableList(tabs);
    }

    public static void addTab(String name) {
        if (tabs.stream().noneMatch(tab -> tab.name.equals(name))) {
            tabs.add(new TabConfig(name, new ArrayList<>()));
            saveConfig();
        }
    }

    public static void removeTab(String name) {
        tabs.removeIf(tab -> tab.name.equals(name));
        saveConfig();
    }

    public static void setActiveTab(String name) {
        activeTabName = name;
    }

    // Оновлені методи для роботи з макросами
    public static void addMacro(MacroButtonData data) {
        TabConfig activeTab = getActiveTab();
        if (activeTab != null) {
            activeTab.buttons.add(data);
            saveConfig();
        }
    }

    public static void updateMacro(int index, MacroButtonData data) {
        TabConfig activeTab = getActiveTab();
        if (activeTab != null && index >= 0 && index < activeTab.buttons.size()) {
            activeTab.buttons.set(index, data);
            saveConfig();
        }
    }

    public static void removeMacro(int index) {
        TabConfig activeTab = getActiveTab();
        if (activeTab != null && index >= 0 && index < activeTab.buttons.size()) {
            activeTab.buttons.remove(index);
            saveConfig();
        }
    }

    // Тепер отримуємо кнопки з активної вкладки
    public static List<MacroButtonData> getButtons() {
        TabConfig activeTab = getActiveTab();
        return (activeTab != null) ? Collections.unmodifiableList(activeTab.buttons) : Collections.emptyList();
    }

    // Внутрішній клас для зберігання конфігурації вкладки
    public static class TabConfig {
        public String name;
        public List<MacroButtonData> buttons;

        public TabConfig(String name, List<MacroButtonData> buttons) {
            this.name = name;
            this.buttons = buttons;
        }
    }

    // --- Методи для інших налаштувань залишаються без змін ---
    public static boolean showTooltips() { return showTooltips; }
    public static void setShowTooltips(boolean value) { showTooltips = value; saveConfig(); }
    public static double getBackgroundTransparency() { return backgroundTransparency; }
    public static void setBackgroundTransparency(double value) { backgroundTransparency = value; saveConfig(); }
    public static ButtonSize getButtonSize() { return buttonSize; }
    public static void setButtonSize(ButtonSize size) { buttonSize = size; saveConfig(); }
    public static double getCommandDelaySeconds() { return commandDelaySeconds; }
    public static void setCommandDelaySeconds(double value) { commandDelaySeconds = value; saveConfig(); }

    public enum ButtonSize {
        SMALL(0.75), MEDIUM(1.0), LARGE(1.25);
        private final double scale;
        ButtonSize(double scale) { this.scale = scale; }
        public double getScale() { return scale; }
    }
}