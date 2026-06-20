package com.example.cmp_addons.mixin;

import com.example.cmp_addons.BeeTier;
import com.example.cmp_addons.config.CMPAddonsConfig;
import com.example.cmp_addons.items.TieredRoboBeeItem;
import de.theidler.create_mobile_packages.entities.RoboBeeEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * RoboBeeEntity の移動速度に config の倍率を掛ける Mixin。
 *
 * <p>Bee が Tier 付きアイテム（{@link TieredRoboBeeItem}）を保持している場合は
 * Tier 別の倍率を、それ以外は無印用の倍率を適用します。
 * 速度は {@code config/cmp_addons-server.toml} で随時変更できます。
 * tick() ごとに再適用するため、コンフィグを変更してサーバーを再起動すると
 * 次のtickから新しい速度が反映されます。
 *
 * <p><b>NOTE:</b> CMP本体のソースで RoboBeeEntity の実際のメソッド名・速度の
 * 保持方法が確認できたら、@At やbaseSpeedの取得元を合わせて修正してください。
 */
@Mixin(value = RoboBeeEntity.class, remap = false)
public abstract class MixinRoboBeeEntity {

    private double cmpa$baseSpeed = -1;

    @Inject(method = "tick", at = @At("HEAD"))
    private void cmpa$applySpeedMultiplier(CallbackInfo ci) {
        RoboBeeEntity self = (RoboBeeEntity)(Object)this;

        var speedAttr = self.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) {
            return;
        }

        // 初回 tick で CMP 本体が設定した基底速度を保存しておく
        if (cmpa$baseSpeed < 0) {
            cmpa$baseSpeed = speedAttr.getBaseValue();
        }

        ItemStack held = self.getMainHandItem();

        double multiplier;
        if (!held.isEmpty() && TieredRoboBeeItem.isTiered(held)) {
            BeeTier tier = TieredRoboBeeItem.getTierFromStack(held);
            multiplier = CMPAddonsConfig.getMultiplierForTier(tier);
        } else {
            multiplier = CMPAddonsConfig.SERVER.roboBeeSpeedMultiplier.get();
        }

        speedAttr.setBaseValue(cmpa$baseSpeed * multiplier);
    }
}
