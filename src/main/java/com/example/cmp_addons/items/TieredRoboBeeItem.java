package com.example.cmp_addons.items;

import com.example.cmp_addons.BeeTier;
import com.example.cmp_addons.config.CMPAddonsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Tier 情報を NBT で保持する Robo Bee アイテム。
 *
 * <p>NBT構造:
 * <pre>
 *   {
 *     "cmp_tier": "tier1" | "tier2" | "tier3"
 *   }
 * </pre>
 *
 * <p>Bee Port に入れるとそのTierの速度倍率で飛行する RoboBeeEntity を生成します。
 */
public class TieredRoboBeeItem extends Item {

    /** ItemStackからTierを読み取るNBTキー */
    public static final String NBT_TIER_KEY = "cmp_tier";

    private final BeeTier tier;

    public TieredRoboBeeItem(BeeTier tier, Properties props) {
        super(props.stacksTo(16));
        this.tier = tier;
    }

    public BeeTier getTier() {
        return tier;
    }

    /** ItemStackがTier付きRoboBeeかどうか */
    public static boolean isTiered(ItemStack stack) {
        return stack.getItem() instanceof TieredRoboBeeItem;
    }

    /** このItemStackのTierを取得（NBT優先、なければフィールド値） */
    public static BeeTier getTierFromStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_TIER_KEY)) {
            return BeeTier.fromKey(tag.getString(NBT_TIER_KEY));
        }
        if (stack.getItem() instanceof TieredRoboBeeItem tiered) {
            return tiered.tier;
        }
        return BeeTier.TIER_1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);

        BeeTier t = getTierFromStack(stack);
        double multiplier = CMPAddonsConfig.getMultiplierForTier(t);
        int pct = (int) ((multiplier - 1.0) * 100);
        String sign = pct >= 0 ? "+" : "";

        lines.add(Component.translatable("item.cmp_addons.tiered_bee.tier",
                        Component.literal(t.key.toUpperCase()).withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("item.cmp_addons.tiered_bee.speed",
                        Component.literal(sign + pct + "%").withStyle(ChatFormatting.AQUA))
                .withStyle(ChatFormatting.GRAY));
    }
}
