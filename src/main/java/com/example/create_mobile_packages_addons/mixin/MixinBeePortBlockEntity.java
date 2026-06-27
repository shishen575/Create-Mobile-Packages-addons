package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.tier.ITieredRobo;
import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import de.theidler.create_mobile_packages.blocks.bee_port.BeePortBlockEntity;
import de.theidler.create_mobile_packages.robo.VirtualRobo;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

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

    /**
     * このポートのBeeスロットに既に「別の種類（Tier違い・無印違い）」のBeeが
     * 入っている場合、その種類が一致しないBeeの目的地としては選ばせない。
     * これにより、別Tierのアイテムが消失する代わりに、CMP本体標準の
     * 「宛先が見つかりません」表示が出るようになる（目的地が他に見つからない場合）。
     */
    @Inject(method = "canAcceptEntity", at = @At("HEAD"), cancellable = true)
    private void cmpa$rejectIncompatibleTier(VirtualRobo entity, Boolean hasPackage, CallbackInfoReturnable<Boolean> cir) {
        if (entity == null) {
            return;
        }
        BeePortBlockEntity self = (BeePortBlockEntity) (Object) this;
        ItemStack existing = self.getRoboBeeInventory().getStackInSlot(0);
        if (existing.isEmpty()) {
            return;
        }
        String entityTierKey = ((ITieredRobo) entity).cmpa$getTierKey();
        String existingTierKey = TieredRoboBeeItem.isTiered(existing)
                ? TieredRoboBeeItem.getTierFromStack(existing).key
                : null;
        if (!Objects.equals(entityTierKey, existingTierKey)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
