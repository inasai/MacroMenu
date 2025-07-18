package com.inasai.macromenu.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent; // Важливо!
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent; // Важливо!
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW; // Важливо!

// Важливо: перевірте, що modid тут також "macromenu"
@Mod.EventBusSubscriber(modid = "macromenu", bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings { // Клас повинен називатися KeyBindings (з 's' на кінці)

    public static KeyMapping OPEN_MACRO_MENU;

    public static void register() {
        OPEN_MACRO_MENU = new KeyMapping(
                "key.macromenu.open",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G, // Клавіша G за замовчуванням
                "key.categories.macromenu"
        );
    }

    // Цей метод автоматично викликається Forge завдяки @EventBusSubscriber та @SubscribeEvent
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        if (OPEN_MACRO_MENU == null) { // Додайте перевірку на null, щоб уникнути NullPointerException
            register(); // Якщо чомусь не було зареєстровано раніше, реєструємо тут
        }
        event.register(OPEN_MACRO_MENU);
    }
}