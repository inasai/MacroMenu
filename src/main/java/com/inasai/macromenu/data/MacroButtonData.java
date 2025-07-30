package com.inasai.macromenu.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.CompoundTag;

public class MacroButtonData {
    private static final String LABEL_KEY = "label";
    private static final String COMMAND_KEY = "command";
    private static final String COLOR_KEY = "color";
    private static final String DESCRIPTION_KEY = "description";

    @SerializedName(LABEL_KEY)
    private String label;
    @SerializedName(COMMAND_KEY)
    private String command;
    @SerializedName(COLOR_KEY)
    private int color;
    @SerializedName(DESCRIPTION_KEY)
    private String description;

    public MacroButtonData() {
        this("", "", 0xFFFFFFFF, "");
    }

    public MacroButtonData(String label, String command) {
        this(label, command, 0xFFFFFFFF, "");
    }

    public MacroButtonData(String label, String command, int color, String description) {
        this.label = label;
        this.command = command;
        this.color = color;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }

    public int getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static MacroButtonData fromTag(CompoundTag tag) {
        String label = tag.getString(LABEL_KEY);
        String command = tag.getString(COMMAND_KEY);
        int color = tag.contains(COLOR_KEY) ? tag.getInt(COLOR_KEY) : 0xFFFFFFFF;
        String description = tag.contains(DESCRIPTION_KEY) ? tag.getString(DESCRIPTION_KEY) : "";
        return new MacroButtonData(label, command, color, description);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString(LABEL_KEY, this.label);
        tag.putString(COMMAND_KEY, this.command);
        tag.putInt(COLOR_KEY, this.color);
        tag.putString(DESCRIPTION_KEY, this.description);
        return tag;
    }
}