package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import de.theidler.create_mobile_packages.blocks.bee_port.BeePortBeeStackHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bee Port の Bee スロットは CMP 本体の {@code mayPlace} で
 * {@code stack.getItem() == CMPItems.ROBO_BEE.get()} という厳密な一致チェックを
 * しているため、別アイテムである {@link TieredRoboBeeItem} は本来挿入できない。
 *
 * <p>ここで Tier 付きアイテムも受け入れるように許可を追加する。
 */
@Mixin(value = BeePortBeeStackHandler.class, remap = false)
public abstract class MixinBeePortBeeStackHandler {

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void cmpa$allowTieredBee(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (TieredRoboBeeItem.isTiered(stack)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
