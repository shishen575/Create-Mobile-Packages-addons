package com.example.create_mobile_packages_addons.config;

import com.example.create_mobile_packages_addons.BeeTier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Create: Mobile Packages の輸送速度を調整するサーバーサイドコンフィグ。
 *
 * <p>ゲームフォルダの {@code config/create_mobile_packages_addons-server.toml} で編集できます。
 */
public class CMPAddonsConfig {

    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        Pair<Server, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Server::new);
        SERVER      = pair.getLeft();
        SERVER_SPEC = pair.getRight();
    }

    /** コンフィグをNeoForgeに登録（メインクラスのコンストラクタから呼ぶ） */
    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC, "create_mobile_packages_addons-server.toml");
    }

    public static class Server {

        /** 無印の Robo Bee（Tierなし）に掛ける速度倍率 */
        public final ModConfigSpec.DoubleValue roboBeeSpeedMultiplier;

        /** Tier 1 アイテムの速度倍率 */
        public final ModConfigSpec.DoubleValue tier1SpeedMultiplier;

        /** Tier 2 アイテムの速度倍率 */
        public final ModConfigSpec.DoubleValue tier2SpeedMultiplier;

        /** Tier 3 アイテムの速度倍率 */
        public final ModConfigSpec.DoubleValue tier3SpeedMultiplier;

        /** Tier付きRoboBeeアイテムの最大スタック数 */
        public final ModConfigSpec.IntValue stackSize;

        Server(ModConfigSpec.Builder builder) {

            builder.comment("Create: Mobile Packages - 輸送速度調整")
                   .push("transportSpeed");

            roboBeeSpeedMultiplier = builder
                    .comment("無印の Robo Bee（Tierアイテムを持たない場合）の移動速度に掛ける倍率。",
                             "1.0 = CMP本体のデフォルト速度のまま。")
                    .defineInRange("roboBeeSpeedMultiplier", 1.0D, 0.05D, 20.0D);

            builder.pop().comment("Robo Bee Tier 別の速度倍率（Tierアイテムを持っている場合に適用）")
                   .push("beeTier");

            tier1SpeedMultiplier = builder
                    .comment("Tier 1 の速度倍率", "デフォルト: 2.0 → 基本速度の+100%")
                    .defineInRange("tier1SpeedMultiplier", 2.0D, 0.1D, 20.0D);

            tier2SpeedMultiplier = builder
                    .comment("Tier 2 の速度倍率", "デフォルト: 4.0 → 基本速度の+300%")
                    .defineInRange("tier2SpeedMultiplier", 4.0D, 0.1D, 20.0D);

            tier3SpeedMultiplier = builder
                    .comment("Tier 3 の速度倍率", "デフォルト: 6.0 → 基本速度の+500%")
                    .defineInRange("tier3SpeedMultiplier", 6.0D, 0.1D, 20.0D);

            builder.pop();

            builder.comment("Robo Bee Tier アイテムのスタック設定")
                   .push("itemStack");

            stackSize = builder
                    .comment("Robo Bee Tier アイテム（Tier I/II/III）1スタックあたりの最大個数。",
                             "デフォルト: 64")
                    .defineInRange("stackSize", 64, 1, 1024);

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
