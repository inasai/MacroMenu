package com.inasai.macromenu.data;

// Цей клас буде представляти одну кнопку макросу
public class MacroButtonData {
    private String label;   // Текст на кнопці
    private String command; // Команда, яка буде виконана

    // Конструктор за замовчуванням для Gson (важливо для десеріалізації)
    public MacroButtonData() {
    }

    // Конструктор для створення нової кнопки
    public MacroButtonData(String label, String command) {
        this.label = label;
        this.command = command;
    }

    // Геттери для доступу до даних кнопки
    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }

    // Сеттери для зміни даних кнопки (знадобляться для редагування)
    public void setLabel(String label) {
        this.label = label;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}