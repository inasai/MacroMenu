package com.inasai.macromenu.client;

import com.inasai.macromenu.MacroMenu;
import com.inasai.macromenu.client.gui.MacroMenuScreen;
import com.inasai.macromenu.client.gui.NotificationManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MacroMenu.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.OPEN_MACRO_MENU.consumeClick()) {
            Minecraft.getInstance().setScreen(new MacroMenuScreen());
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        NotificationManager.renderAll(event.getGuiGraphics());
    }
}