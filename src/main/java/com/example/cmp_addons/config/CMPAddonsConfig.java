package com.example.cmp_addons.config;

import com.example.cmp_addons.BeeTier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Create: Mobile Packages の輸送速度を調整するサーバーサイドコンフィグ。
 *
 * <p>ゲームフォルダの {@code config/cmp_addons-server.toml} で編集できます。
 */
public class CMPAddonsConfig {

    public static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        Pair<Server, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER      = pair.getLeft();
        SERVER_SPEC = pair.getRight();
    }

    /** コンフィグをForgeに登録（メインクラスのコンストラクタから呼ぶ） */
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC, "cmp_addons-server.toml");
    }

    public static class Server {

        /** 無印の Robo Bee（Tierなし）に掛ける速度倍率 */
        public final ForgeConfigSpec.DoubleValue roboBeeSpeedMultiplier;

        /** Tier 1 アイテムの速度倍率 */
        public final ForgeConfigSpec.DoubleValue tier1SpeedMultiplier;

        /** Tier 2 アイテムの速度倍率 */
        public final ForgeConfigSpec.DoubleValue tier2SpeedMultiplier;

        /** Tier 3 アイテムの速度倍率 */
        public final ForgeConfigSpec.DoubleValue tier3SpeedMultiplier;

        Server(ForgeConfigSpec.Builder builder) {

            builder.comment("Create: Mobile Packages - 輸送速度調整")
                   .push("transportSpeed");

            roboBeeSpeedMultiplier = builder
                    .comment("無印の Robo Bee（Tierアイテムを持たない場合）の移動速度に掛ける倍率。",
                             "1.0 = CMP本体のデフォルト速度のまま。")
                    .defineInRange("roboBeeSpeedMultiplier", 1.0D, 0.05D, 20.0D);

            builder.pop().comment("Robo Bee Tier 別の速度倍率（Tierアイテムを持っている場合に適用）")
                   .push("beeTier");

            tier1SpeedMultiplier = builder
                    .comment("Tier 1 の速度倍率", "デフォルト: 1.0 → 無印と同じ速度")
                    .defineInRange("tier1SpeedMultiplier", 1.0D, 0.1D, 20.0D);

            tier2SpeedMultiplier = builder
                    .comment("Tier 2 の速度倍率", "デフォルト: 1.5 → 1.5倍速")
                    .defineInRange("tier2SpeedMultiplier", 1.5D, 0.1D, 20.0D);

            tier3SpeedMultiplier = builder
                    .comment("Tier 3 の速度倍率", "デフォルト: 2.5 → 2.5倍速")
                    .defineInRange("tier3SpeedMultiplier", 2.5D, 0.1D, 20.0D);

            builder.pop();
        }
    }

    /** Tierから速度倍率を取得 */
    public static double getMultiplierForTier(BeeTier tier) {
        return switch (tier) {
            case TIER_1 -> SERVER.tier1SpeedMultiplier.get();
            case TIER_2 -> SERVER.tier2SpeedMultiplier.get();
            case TIER_3 -> SERVER.tier3SpeedMultiplier.get();
        };
    }
}
