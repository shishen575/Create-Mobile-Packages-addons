package com.example.create_mobile_packages_addons.index;

import com.example.create_mobile_packages_addons.BeeTier;
import com.example.create_mobile_packages_addons.CMPAddons;
import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CMPAddons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CMPAddonsItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CMPAddons.MOD_ID);

    // ─── Tier 1 ───
    public static final RegistryObject<TieredRoboBeeItem> ROBO_BEE_T1 =
            ITEMS.register("robo_bee_t1",
                    () -> new TieredRoboBeeItem(BeeTier.TIER_1, new Item.Properties()));

    // ─── Tier 2 ───
    public static final RegistryObject<TieredRoboBeeItem> ROBO_BEE_T2 =
            ITEMS.register("robo_bee_t2",
                    () -> new TieredRoboBeeItem(BeeTier.TIER_2, new Item.Properties()));

    // ─── Tier 3 ───
    public static final RegistryObject<TieredRoboBeeItem> ROBO_BEE_T3 =
            ITEMS.register("robo_bee_t3",
                    () -> new TieredRoboBeeItem(BeeTier.TIER_3, new Item.Properties()));

    /**
     * クリエイティブインベントリ（およびJEI等のアイテムリスト）に登録する。
     * JEIは1.20.1ではクリエイティブタブの内容を元にアイテムリストを構築するため、
     * ここに追加しないとクラフトレシピがあってもJEIに表示されない。
     */
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ROBO_BEE_T1.get());
            event.accept(ROBO_BEE_T2.get());
            event.accept(ROBO_BEE_T3.get());
        }
    }
}
