package com.inasai.macromenu;

import com.inasai.macromenu.client.gui.MacroMenuScreen;
import com.inasai.macromenu.config.ModConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.inasai.macromenu.client.gui.SelectMacroScreen;

@Mod(MacroMenu.MOD_ID)
public class MacroMenu {
    public static final String MOD_ID = "macromenu";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final SimpleScheduler SCHEDULER = new SimpleScheduler();

    public MacroMenu() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(SCHEDULER::onClientTick);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        ModConfig.loadConfigs();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("MacroMenu client setup");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            // Цей рядок буде залежати від того, як ви реєстрували свою оригінальну клавішу G.
            // Можливо, у вас є інше поле KeyMapping, наприклад, YourOriginalKey.
            // Якщо так, то використовуйте його тут.
            // Приклад:
            // if (YourOriginalKey.consumeClick()) {
            //     if (!(mc.screen instanceof MacroMenuScreen)) { // Викликаємо головне меню, якщо це потрібно
            //         mc.setScreen(new MacroMenuScreen());
            //     }
            // }

            // Якщо onClientTick був ЄДИНИМ місцем, де оброблялися KeyMappings,
            // і "Відкрити Макро Меню - M" було єдиною активною,
            // тоді можливо, вам потрібно просто змінити ідентифікатор KeyMapping.
            // Але оскільки ви кажете, що "key.macromenu.open_menu - G" вже працює,
            // це означає, що логіка для неї десь вже є.

            // Наразі, просто видаліть або закоментуйте цей блок if:
            /*
            if (OPEN_MACRO_MENU_KEY != null && OPEN_MACRO_MENU_KEY.consumeClick()) {
                if (!(mc.screen instanceof SelectMacroScreen)) {
                    mc.setScreen(new SelectMacroScreen(mc.screen, SelectMacroScreen.Mode.SELECT));
                }
            }
            */
        }
    }

    public static class SimpleScheduler {
        private final Queue<ScheduledTask> tasks = new ConcurrentLinkedQueue<>();

        public void schedule(Runnable task, long delayMillis) {
            tasks.add(new ScheduledTask(task, System.currentTimeMillis() + delayMillis));
        }

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                long currentTime = System.currentTimeMillis();
                tasks.removeIf(task -> {
                    if (task.executionTime <= currentTime) {
                        task.run();
                        return true;
                    }
                    return false;
                });
            }
        }

        private static class ScheduledTask implements Runnable {
            private final Runnable task;
            private final long executionTime;

            public ScheduledTask(Runnable task, long executionTime) {
                this.task = task;
                this.executionTime = executionTime;
            }

            @Override
            public void run() {
                task.run();
            }
        }
    }
}