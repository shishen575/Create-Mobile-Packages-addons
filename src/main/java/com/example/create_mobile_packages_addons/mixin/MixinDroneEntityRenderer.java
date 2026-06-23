package com.example.create_mobile_packages_addons.mixin;

import com.example.create_mobile_packages_addons.CMPAddonsTextures;
import com.example.create_mobile_packages_addons.tier.ITieredEntity;
import de.theidler.create_mobile_packages.entities.RoboBeeEntity;
import de.theidler.create_mobile_packages.entities.render.DroneEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 飛行中の RoboBeeEntity の描画テクスチャを、保持しているTierに応じて切り替える。
 * クライアントサイドのみで有効（create_mobile_packages_addons.mixins.json の "client" に登録）。
 */
@Mixin(value = DroneEntityRenderer.class, remap = false)
public abstract class MixinDroneEntityRenderer {

    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
    private void cmpa$tieredTexture(RoboBeeEntity entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!(entity instanceof ITieredEntity tiered)) return;
        ResourceLocation override = CMPAddonsTextures.forTierKey(tiered.cmpa$getTierKey());
        if (override != null) {
            cir.setReturnValue(override);
            cir.cancel();
        }
    }
}
