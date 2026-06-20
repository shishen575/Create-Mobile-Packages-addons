package com.example.cmp_addons;

import com.example.cmp_addons.config.CMPAddonsConfig;
import com.example.cmp_addons.index.CMPAddonsItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CMPAddons.MOD_ID)
public class CMPAddons {

    public static final String MOD_ID = "cmp_addons";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CMPAddons() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // コンフィグ登録（最初に行う）
        CMPAddonsConfig.register();

        // アイテム登録
        CMPAddonsItems.ITEMS.register(modBus);
    }
}
