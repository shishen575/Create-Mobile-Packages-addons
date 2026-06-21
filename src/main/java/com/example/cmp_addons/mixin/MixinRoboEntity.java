package com.example.cmp_addons.mixin;

import com.example.cmp_addons.tier.ITieredEntity;
import com.example.cmp_addons.tier.ITieredRobo;
import de.theidler.create_mobile_packages.entities.robo_entity.RoboEntity;
import de.theidler.create_mobile_packages.robo.VirtualRobo;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * RoboEntity にTierキーをクライアント同期データとして持たせる。
 * 描画側（MixinDroneEntityRenderer）はここから読んでテクスチャを切り替える。
 */
@Mixin(value = RoboEntity.class, remap = false)
public abstract class MixinRoboEntity implements ITieredEntity {

    @Unique
    private static final EntityDataAccessor<String> cmpa$TIER_KEY =
            SynchedEntityData.defineId(RoboEntity.class, EntityDataSerializers.STRING);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void cmpa$defineTier(CallbackInfo ci) {
        RoboEntity self = (RoboEntity) (Object) this;
        self.getEntityData().define(cmpa$TIER_KEY, "");
    }

    @Inject(method = "syncFromVirtual", at = @At("TAIL"))
    private void cmpa$syncTier(VirtualRobo virtualRobo, CallbackInfo ci) {
        RoboEntity self = (RoboEntity) (Object) this;
        String tierKey = ((ITieredRobo) virtualRobo).cmpa$getTierKey();
        self.getEntityData().set(cmpa$TIER_KEY, tierKey != null ? tierKey : "");
    }

    @Override
    public @Nullable String cmpa$getTierKey() {
        RoboEntity self = (RoboEntity) (Object) this;
        return self.getEntityData().get(cmpa$TIER_KEY);
    }
}
