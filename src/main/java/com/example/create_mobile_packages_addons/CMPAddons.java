package com.example.create_mobile_packages_addons;

import com.example.create_mobile_packages_addons.config.CMPAddonsConfig;
import com.example.create_mobile_packages_addons.index.CMPAddonsItems;
import com.example.create_mobile_packages_addons.index.CMPAddonsTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CMPAddons.MOD_ID)
public class CMPAddons {

    public static final String MOD_ID = "create_mobile_packages_addons";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CMPAddons(IEventBus modBus, ModContainer modContainer) {
        // コンフィグ登録（最初に行う）
        CMPAddonsConfig.register(modContainer);

        // アイテム登録
        CMPAddonsItems.ITEMS.register(modBus);

        // クリエイティブタブ登録
        CMPAddonsTabs.TABS.register(modBus);
    }
}
