package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.BeeTier;
import com.example.create_mobile_packages_addons.items.TieredRoboBeeItem;
import com.example.create_mobile_packages_addons.tier.ITieredRobo;
import de.theidler.create_mobile_packages.robo.RoboManager;
import de.theidler.create_mobile_packages.robo.VirtualRobo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * RoboBeeアイテムが配置されてVirtualRoboが生成された直後に、
 * {@link TieredRoboBeeItem#PLACING_TIER} に設定されたTierをタグ付けする。
 *
 * <p>CMP本体には Bee を発進させる経路が2つある（{@code newRobo} と
 * {@code newRequestRobo}）が、どちらも内部で {@code this.add(robo)} を呼ぶ
 * ことを利用し、その呼び出し自体を横取りすることで両方を一度にフックする
 * （ローカル変数キャプチャに依存しないため、LVT情報が無い配布jarでも安全）。
 */
@Mixin(value = RoboManager.class, remap = false)
public abstract class MixinRoboManager {

    @Redirect(method = {"newRobo", "newRequestRobo"},
            at = @At(value = "INVOKE",
                    target = "Lde/theidler/create_mobile_packages/robo/RoboManager;add(Lde/theidler/create_mobile_packages/robo/VirtualRobo;)V"))
    private void cmpa$addAndTagRobo(RoboManager manager, VirtualRobo robo) {
        BeeTier tier = TieredRoboBeeItem.PLACING_TIER.get();
        ((ITieredRobo) robo).cmpa$setTierAndApplySpeed(tier);
        TieredRoboBeeItem.PLACING_TIER.remove();
        manager.add(robo);
    }
}
