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
 * <p>ここで Tier 付きアイテムも受け入れるように許可を追加する。ただし、
 * スロットに既に「別のTier」のBeeが入っている場合は許可しない（CMP本体はスロットに
 * 単一種類のアイテムしか入らない前提で実装されているため、Tierが混在すると
 * 内部の状態管理が壊れ「ターゲットがない」表示などの不具合につながる）。
 */
@Mixin(value = BeePortBeeStackHandler.class, remap = false)
public abstract class MixinBeePortBeeStackHandler {

    // mayPlace は vanilla Slot#mayPlace のオーバーライドのため、配布jarではSRG名
    // m_5857_ にリネームされている（remap=trueはrefmap未生成のため効かないので、
    // SRG名を直接指定する。開発環境(deobf)用に元の名前も併記）。
    @Inject(method = {"mayPlace", "m_5857_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void cmpa$allowTieredBee(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (TieredRoboBeeItem.isTiered(stack)) {
            BeePortBeeStackHandler self = (BeePortBeeStackHandler) (Object) this;
            ItemStack current = self.getItem();
            boolean compatible = current.isEmpty() || ItemStack.isSameItemSameTags(current, stack);
            cir.setReturnValue(compatible);
            cir.cancel();
        }
    }
}
