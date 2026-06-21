package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.BeeTier;
import com.example.create_mobile_packages_addons.config.CMPAddonsConfig;
import com.example.create_mobile_packages_addons.tier.ITieredRobo;
import de.theidler.create_mobile_packages.robo.VirtualRobo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * VirtualRobo に Tier 情報を持たせ、Tierに応じた速度倍率を適用する。
 *
 * <p>Tierは {@link com.example.create_mobile_packages_addons.items.TieredRoboBeeItem#PLACING_TIER} 経由で
 * 配置直後（{@code RoboManager.newRobo} 直後）に一度だけ設定され、
 * {@code serializeNBT}/{@code deserializeNBT} を介して保存・復元される。
 */
@Mixin(value = VirtualRobo.class, remap = false)
public abstract class MixinVirtualRobo implements ITieredRobo {

    @Unique
    private String cmpa$tierKey = null;

    @Override
    public @Nullable String cmpa$getTierKey() {
        return cmpa$tierKey;
    }

    @Override
    public void cmpa$setTierAndApplySpeed(@Nullable BeeTier tier) {
        VirtualRobo self = (VirtualRobo) (Object) this;
        double multiplier = (tier != null)
                ? CMPAddonsConfig.getMultiplierForTier(tier)
                : CMPAddonsConfig.SERVER.roboBeeSpeedMultiplier.get();
        this.cmpa$tierKey = (tier != null) ? tier.key : null;
        int newSpeed = (int) Math.round(self.getSpeed() * multiplier);
        ((VirtualRoboAccessor) (Object) this).cmpa$invokeSetSpeed(newSpeed);
    }

    @Override
    public void cmpa$setTierKeySilently(@Nullable String tierKey) {
        this.cmpa$tierKey = tierKey;
    }

    @Inject(method = "serializeNBT", at = @At("RETURN"))
    private void cmpa$writeTier(CallbackInfoReturnable<CompoundTag> cir) {
        if (cmpa$tierKey != null) {
            cir.getReturnValue().putString("cmpa_tier", cmpa$tierKey);
        }
    }

    @Inject(method = "deserializeNBT", at = @At("RETURN"))
    private static void cmpa$readTier(ServerLevel level, CompoundTag roboTag,
                                       CallbackInfoReturnable<VirtualRobo> cir) {
        if (roboTag.contains("cmpa_tier")) {
            ((ITieredRobo) cir.getReturnValue()).cmpa$setTierKeySilently(roboTag.getString("cmpa_tier"));
        }
    }
}
