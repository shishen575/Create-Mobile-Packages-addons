package com.example.create_mobile_packages_addons.index;

import com.example.create_mobile_packages_addons.BeeTier;
import com.example.create_mobile_packages_addons.CMPAddons;
import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CMPAddonsItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, CMPAddons.MOD_ID);

    // ─── Tier 1 ───
    public static final DeferredHolder<Item, TieredRoboBeeItem> ROBO_BEE_T1 =
            ITEMS.register("robo_bee_t1",
                    () -> new TieredRoboBeeItem(BeeTier.TIER_1, new Item.Properties()));

    // ─── Tier 2 ───
    public static final DeferredHolder<Item, TieredRoboBeeItem> ROBO_BEE_T2 =
            ITEMS.register("robo_bee_t2",
                    () -> new TieredRoboBeeItem(BeeTier.TIER_2, new Item.Properties()));

    // ─── Tier 3 ───
    public static final DeferredHolder<Item, TieredRoboBeeItem> ROBO_BEE_T3 =
            ITEMS.register("robo_bee_t3",
                    () -> new TieredRoboBeeItem(BeeTier.TIER_3, new Item.Properties()));
}
