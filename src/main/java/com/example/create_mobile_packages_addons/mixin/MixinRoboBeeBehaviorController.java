package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.BeeTier;
import com.example.create_mobile_packages_addons.index.CMPAddonsItems;
import com.example.create_mobile_packages_addons.tier.ITieredRobo;
import de.theidler.create_mobile_packages.blocks.bee_port.BeePortBlockEntity;
import de.theidler.create_mobile_packages.entities.robo_entity.RoboBeeBehaviorController;
import de.theidler.create_mobile_packages.index.CMPItems;
import de.theidler.create_mobile_packages.robo.VirtualRobo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * CMP本体は到着したRobo Beeを Bee Port のスロットへ戻す際、
 * {@code BeePortBlockEntity#addBeeToRoboBeeInventory(int)} で常に無印の
 * {@code CMPItems.ROBO_BEE} を生成して戻しており、Tier情報が失われていた。
 *
 * <p>ここで {@code handleShutdown} 内の呼び出しを横取りし、
 * 配達してきた VirtualRobo の Tier に応じたアイテムを戻すようにする。
 *
 * <p>別Tierが混在するポートへ向かわせない対策は {@code MixinBeePortBlockEntity}
 * の {@code canAcceptEntity} 側で行っているため、ここに到達する時点では
 * スロットは互換性があるはずである。
 */
@Mixin(value = RoboBeeBehaviorController.class, remap = false)
public abstract class MixinRoboBeeBehaviorController {

    @Redirect(method = "handleShutdown",
            at = @At(value = "INVOKE",
                    target = "Lde/theidler/create_mobile_packages/blocks/bee_port/BeePortBlockEntity;addBeeToRoboBeeInventory(I)V"))
    private void cmpa$returnTieredBee(BeePortBlockEntity bpbe, int amount, VirtualRobo robo) {
        String tierKey = ((ITieredRobo) robo).cmpa$getTierKey();
        Item item = cmpa$itemForTierKey(tierKey);
        bpbe.getRoboBeeInventory().insertItem(0, new ItemStack(item, amount), false);
    }

    private static Item cmpa$itemForTierKey(@Nullable String tierKey) {
        if (tierKey == null) {
            return CMPItems.ROBO_BEE.get();
        }
        BeeTier tier = BeeTier.fromKey(tierKey);
        return switch (tier) {
            case TIER_1 -> CMPAddonsItems.ROBO_BEE_T1.get();
            case TIER_2 -> CMPAddonsItems.ROBO_BEE_T2.get();
            case TIER_3 -> CMPAddonsItems.ROBO_BEE_T3.get();
        };
    }
}
