package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.BeeTier;
import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import com.example.create_mobile_packages_addons.tier.ITieredRobo;
import de.theidler.create_mobile_packages.robo.RoboManager;
import de.theidler.create_mobile_packages.robo.VirtualRobo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * RoboBeeアイテムが配置されてVirtualRoboが生成された直後に、
 * {@link TieredRoboBeeItem#PLACING_TIER} に設定されたTierをタグ付けする。
 *
 * <p>CMP本体には Bee を発進させる経路が2つある（{@code newRobo} と
 * {@code newRequestRobo}）ため、両方をフックする必要がある。
 */
@Mixin(value = RoboManager.class, remap = false)
public abstract class MixinRoboManager {

    @Inject(method = "newRobo", at = @At("RETURN"))
    private void cmpa$tagTier(ServerLevel level, ItemStack itemStack, BlockPos spawnPos, UUID logisticsNetworkId,
                               float packageHeightScale, @Nullable BlockPos homePort,
                               CallbackInfoReturnable<UUID> cir) {
        RoboManager self = (RoboManager) (Object) this;
        UUID id = cir.getReturnValue();
        VirtualRobo robo = self.get(id);
        if (robo == null) return;

        cmpa$applyPlacingTier(robo);
    }

    // newRequestRobo は生成したVirtualRoboをローカル変数にしか保持せず戻り値もないため、
    // ローカル変数キャプチャ（LVT情報）に依存する@Injectは配布jar側にLVT情報が無いと
    // クラッシュする。代わりに、メソッド内部の this.add(robo) 呼び出し自体を横取りして
    // 引数からrobo参照を直接得る（LVT不要で安全）。
    @Redirect(method = "newRequestRobo",
            at = @At(value = "INVOKE",
                    target = "Lde/theidler/create_mobile_packages/robo/RoboManager;add(Lde/theidler/create_mobile_packages/robo/VirtualRobo;)V"))
    private void cmpa$addAndTagRequestRobo(RoboManager manager, VirtualRobo robo) {
        cmpa$applyPlacingTier(robo);
        manager.add(robo);
    }

    private static void cmpa$applyPlacingTier(VirtualRobo robo) {
        BeeTier tier = TieredRoboBeeItem.PLACING_TIER.get();
        ((ITieredRobo) robo).cmpa$setTierAndApplySpeed(tier);
        TieredRoboBeeItem.PLACING_TIER.remove();
    }
}
