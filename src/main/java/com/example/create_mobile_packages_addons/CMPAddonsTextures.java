package com.example.create_mobile_packages_addons;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/** Tierキーから飛行中エンティティ用テクスチャの ResourceLocation を解決する */
public class CMPAddonsTextures {

    private static final ResourceLocation TIER_1 =
            new ResourceLocation(CMPAddons.MOD_ID, "textures/entity/robo_bee_t1.png");
    private static final ResourceLocation TIER_2 =
            new ResourceLocation(CMPAddons.MOD_ID, "textures/entity/robo_bee_t2.png");
    private static final ResourceLocation TIER_3 =
            new ResourceLocation(CMPAddons.MOD_ID, "textures/entity/robo_bee_t3.png");

    /** Tierキー文字列からテクスチャを返す。null/空/不明なキーの場合は null（無印テクスチャを使うべきことを示す） */
    public static @Nullable ResourceLocation forTierKey(@Nullable String tierKey) {
        if (tierKey == null || tierKey.isEmpty()) return null;
        return switch (tierKey) {
            case "tier1" -> TIER_1;
            case "tier2" -> TIER_2;
            case "tier3" -> TIER_3;
            default -> null;
        };
    }
}
