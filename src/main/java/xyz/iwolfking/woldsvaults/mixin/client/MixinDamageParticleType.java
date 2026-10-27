package xyz.iwolfking.woldsvaults.mixin.client;

import iskallia.vault.client.render.healthbar.DamageParticleSystem;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.events.WoldActiveFlags;

@Mixin(value = DamageParticleSystem.DamageType.class, remap = false)
public class MixinDamageParticleType {
    @Inject(method = "fromDamageSource", at = @At("HEAD"), cancellable = true)
    private static void woldsvaults$fireNovaDamageType(DamageSource source,
            CallbackInfoReturnable<DamageParticleSystem.DamageType> cir) {
        if (WoldActiveFlags.IS_FIRE_NOVA_ATTACKING.isSet()) {
            cir.setReturnValue(DamageParticleSystem.DamageType.FIRE);
        }
    }
}
