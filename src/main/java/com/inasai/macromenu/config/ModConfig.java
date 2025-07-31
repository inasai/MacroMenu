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

    private static List<TabConfig> tabs = new ArrayList<>();
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

        if (!tabs.isEmpty()) {
            if (activeTabName == null || tabs.stream().noneMatch(tab -> tab.name.equals(activeTabName))) {
                activeTabName = tabs.get(0).name;
            }
        }

        ClientConfig.loadConfig();
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(tabs, writer);
        } catch (IOException e) {
            MacroMenu.LOGGER.error("Failed to save MacroMenu config", e);
        }
        ClientConfig.saveConfig();
    }

    private static void createDefaultConfig() {
        tabs.clear();
        TabConfig defaultTab = new TabConfig("Default", new ArrayList<>());
        tabs.add(defaultTab);
        activeTabName = "Default";
        saveConfig();
    }

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

    public static void renameTab(String oldName, String newName) {
        if (tabs.stream().noneMatch(tab -> tab.name.equals(newName))) {
            tabs.stream()
                    .filter(tab -> tab.name.equals(oldName))
                    .findFirst()
                    .ifPresent(tab -> tab.name = newName);

            if (oldName.equals(activeTabName)) {
                activeTabName = newName;
            }
            saveConfig();
        }
    }

    public static void setActiveTab(String name) {
        activeTabName = name;
    }

    public static void addMacro(MacroButtonData data) {
        TabConfig activeTab = getActiveTab();
        if (activeTab != null) {
            activeTab.buttons.add(data);
            saveConfig();
        }
    }

    public static void addMacroToTab(String tabName, MacroButtonData data) {
        tabs.stream()
                .filter(tab -> tab.name.equals(tabName))
                .findFirst()
                .ifPresent(tab -> tab.buttons.add(data));
        saveConfig();
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

    public static void moveMacro(String fromTabName, int macroIndex, String toTabName) {
        TabConfig fromTab = tabs.stream()
                .filter(tab -> tab.name.equals(fromTabName))
                .findFirst()
                .orElse(null);

        TabConfig toTab = tabs.stream()
                .filter(tab -> tab.name.equals(toTabName))
                .findFirst()
                .orElse(null);

        if (fromTab != null && toTab != null && macroIndex >= 0 && macroIndex < fromTab.buttons.size()) {
            MacroButtonData macro = fromTab.buttons.remove(macroIndex);
            toTab.buttons.add(macro);
            saveConfig();
        }
    }

    public static List<MacroButtonData> getButtons() {
        TabConfig activeTab = getActiveTab();
        return (activeTab != null) ? Collections.unmodifiableList(activeTab.buttons) : Collections.emptyList();
    }

    public static class TabConfig {
        public String name;
        public List<MacroButtonData> buttons;

        public TabConfig(String name, List<MacroButtonData> buttons) {
            this.name = name;
            this.buttons = buttons;
        }
    }

    public static boolean showTooltips() { return ClientConfig.showTooltips; }
    public static void setShowTooltips(boolean value) { ClientConfig.showTooltips = value; saveConfig(); }
    public static double getBackgroundTransparency() { return ClientConfig.backgroundTransparency; }
    public static void setBackgroundTransparency(double value) { ClientConfig.backgroundTransparency = value; saveConfig(); }
    public static ButtonSize getButtonSize() { return ClientConfig.buttonSize; }
    public static void setButtonSize(ButtonSize size) { ClientConfig.buttonSize = size; saveConfig(); }
    public static double getCommandDelaySeconds() { return ClientConfig.commandDelaySeconds; }
    public static void setCommandDelaySeconds(double value) { ClientConfig.commandDelaySeconds = value; saveConfig(); }

    public static class ButtonSize {
        public static final ButtonSize SMALL = new ButtonSize(0.75);
        public static final ButtonSize MEDIUM = new ButtonSize(1.0);
        public static final ButtonSize LARGE = new ButtonSize(1.25);
        public static final ButtonSize[] values = {SMALL, MEDIUM, LARGE};

        private final double scale;
        private ButtonSize(double scale) { this.scale = scale; }
        public double getScale() { return scale; }

        public static ButtonSize fromName(String name) {
            for (ButtonSize size : values) {
                if (size.name().equalsIgnoreCase(name)) {
                    return size;
                }
            }
            return MEDIUM;
        }

        public String name() {
            if (this.scale == SMALL.scale) return "SMALL";
            if (this.scale == LARGE.scale) return "LARGE";
            return "MEDIUM";
        }
    }
}