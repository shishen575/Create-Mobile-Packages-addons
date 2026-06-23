package com.example.create_mobile_packages_addons.index;

import com.example.create_mobile_packages_addons.CMPAddons;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CMPAddonsTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CMPAddons.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CMP_ADDONS_TAB = TABS.register("cmp_addons_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.create_mobile_packages_addons"))
                    .icon(() -> CMPAddonsItems.ROBO_BEE_T3.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(CMPAddonsItems.ROBO_BEE_T1.get());
                        output.accept(CMPAddonsItems.ROBO_BEE_T2.get());
                        output.accept(CMPAddonsItems.ROBO_BEE_T3.get());
                    })
                    .build());
}
