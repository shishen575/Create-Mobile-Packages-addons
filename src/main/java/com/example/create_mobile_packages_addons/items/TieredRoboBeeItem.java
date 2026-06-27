package com.example.create_mobile_packages_addons.items;

import com.example.create_mobile_packages_addons.BeeTier;
import com.example.create_mobile_packages_addons.config.CMPAddonsConfig;
import de.theidler.create_mobile_packages.items.robo_bee.RoboBeeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Tier 情報を持つ Robo Bee アイテム。
 *
 * <p>CMP本体の {@link RoboBeeItem} をそのまま継承し、配置動作（useOn）はCMP本体のものを使う。
 * 配置直前に {@link #PLACING_TIER} へ自分のTierをセットしておくことで、
 * {@code MixinRoboManager} が生成された VirtualRobo にこのTierをタグ付けする。
 */
public class TieredRoboBeeItem extends RoboBeeItem {

    /**
     * 配置処理中だけ使うスレッドローカル。MixinRoboManager がここを読んで
     * 生成直後のVirtualRoboにTierを反映する。
     */
    public static final ThreadLocal<BeeTier> PLACING_TIER = new ThreadLocal<>();

    private final BeeTier tier;

    public TieredRoboBeeItem(BeeTier tier, Properties props) {
        super(props);
        this.tier = tier;
    }

    public BeeTier getTier() {
        return tier;
    }

    /**
     * 最大スタック数を config/create_mobile_packages_addons-server.toml の
     * stackSize から動的に取得する（デフォルト64）。
     */
    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return CMPAddonsConfig.SERVER.stackSize.get();
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        PLACING_TIER.set(this.tier);
        try {
            return super.useOn(context);
        } finally {
            PLACING_TIER.remove();
        }
    }

    /** ItemStackがTier付きRoboBeeかどうか */
    public static boolean isTiered(ItemStack stack) {
        return stack.getItem() instanceof TieredRoboBeeItem;
    }

    /** このItemStackのTierを取得 */
    public static BeeTier getTierFromStack(ItemStack stack) {
        if (stack.getItem() instanceof TieredRoboBeeItem tiered) {
            return tiered.tier;
        }
        return BeeTier.TIER_1;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                 List<Component> lines, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);

        BeeTier t = getTierFromStack(stack);
        double multiplier = CMPAddonsConfig.getMultiplierForTier(t);
        int pct = (int) ((multiplier - 1.0) * 100);
        String sign = pct >= 0 ? "+" : "";

        lines.add(Component.translatable("item.create_mobile_packages_addons.tiered_bee.tier",
                        Component.literal(t.key.toUpperCase()).withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("item.create_mobile_packages_addons.tiered_bee.speed",
                        Component.literal(sign + pct + "%").withStyle(ChatFormatting.AQUA))
                .withStyle(ChatFormatting.GRAY));
    }
}
