package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import de.theidler.create_mobile_packages.blocks.bee_port.BeePortBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Bee Port に保管されているRobo Beeを発進させる際（{@code tryConsumeDrone}）、
 * CMP本体は取り出したアイテムのTierを見ずに捨てている（{@code !usedBee.isEmpty()}しか使わない）。
 * これが原因で Bee Port から発進した Robo Bee は常に無印として扱われ、速度Tierが
 * 適用されなかった（直接右クリックで配置した場合は {@link TieredRoboBeeItem#useOn} 経由で
 * 正しくTierが伝わるため発生しなかった）。
 *
 * <p>ここで roboBeeInventory からの取り出しを横取りし、取り出したアイテムのTierを
 * {@link TieredRoboBeeItem#PLACING_TIER} にセットしておく。直後に呼ばれる
 * {@code RoboManager#newRobo} を {@code MixinRoboManager} が捉えてVirtualRoboに反映する。
 */
@Mixin(value = BeePortBlockEntity.class, remap = false)
public abstract class MixinBeePortBlockEntity {

    @Redirect(method = "tryConsumeDrone",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/items/ItemStackHandler;extractItem(IIZ)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack cmpa$captureTierOnExtract(ItemStackHandler handler, int slot, int amount, boolean simulate) {
        ItemStack extracted = handler.extractItem(slot, amount, simulate);
        if (TieredRoboBeeItem.isTiered(extracted)) {
            TieredRoboBeeItem.PLACING_TIER.set(TieredRoboBeeItem.getTierFromStack(extracted));
        } else {
            TieredRoboBeeItem.PLACING_TIER.remove();
        }
        return extracted;
    }
}
