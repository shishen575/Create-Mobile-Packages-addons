package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import de.theidler.create_mobile_packages.blocks.bee_port.BeePortMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * CMP本体の {@code BeePortMenu#quickMoveStack}（シフトクリック移動）は
 * {@code stack.getItem() == CMPItems.ROBO_BEE.get()} という厳密一致チェックで
 * Robo Bee スロットへの移動先を判定しているため、{@link TieredRoboBeeItem} を
 * シフトクリックでスロットへ送ることができない。
 *
 * <p>ここでTier付きアイテムの場合の移動処理を横取りして実現する。
 * （Beeスロットからプレイヤー側へ戻す方向は元々アイテム種別を問わず動作するため対応不要）
 */
@Mixin(value = BeePortMenu.class, remap = false)
public abstract class MixinBeePortMenu {

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void cmpa$allowTieredQuickMove(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        BeePortMenu self = (BeePortMenu) (Object) this;
        if (index == 54) {
            return;
        }

        Slot slot = self.slots.get(index);
        if (!slot.hasItem()) {
            return;
        }

        ItemStack stack = slot.getItem();
        if (!TieredRoboBeeItem.isTiered(stack)) {
            return;
        }

        Slot roboBeeSlot = self.slots.get(54);
        ItemStack targetStack = roboBeeSlot.getItem();

        int maxStackSize = stack.getMaxStackSize();
        int space = maxStackSize - (targetStack.isEmpty() ? 0 : targetStack.getCount());

        if (space <= 0) {
            cir.setReturnValue(ItemStack.EMPTY);
            cir.cancel();
            return;
        }

        int toMove = Math.min(space, stack.getCount());
        if (targetStack.isEmpty()) {
            ItemStack moved = stack.copy();
            moved.setCount(toMove);
            roboBeeSlot.set(moved);
        } else {
            targetStack.grow(toMove);
            roboBeeSlot.setChanged();
        }
        stack.shrink(toMove);
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        ItemStack result = stack.copy();
        result.setCount(toMove);
        cir.setReturnValue(result);
        cir.cancel();
    }
}
