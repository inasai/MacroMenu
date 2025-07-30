package com.inasai.macromenu;

import com.inasai.macromenu.client.ClientEvents;
import com.inasai.macromenu.config.ModConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Mod(MacroMenu.MOD_ID)
public class MacroMenu {
    public static final String MOD_ID = "macromenu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    public MacroMenu() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup finished.");
        ModConfig.loadConfig(); // Виправлений метод
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup finished.");
    }
}