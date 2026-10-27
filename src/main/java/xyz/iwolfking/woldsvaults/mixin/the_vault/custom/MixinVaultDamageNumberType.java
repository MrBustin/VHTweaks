package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.entity.entity.VaultDamageNumberEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.events.WoldActiveFlags;

@Mixin(value = VaultDamageNumberEntity.DamageType.class, remap = false)
public class MixinVaultDamageNumberType {
    @Inject(method = "fromDamageSource", at = @At("HEAD"), cancellable = true)
    private static void woldsvaults$fireNovaDamageType(DamageSource source,
            CallbackInfoReturnable<VaultDamageNumberEntity.DamageType> cir) {
        if (WoldActiveFlags.IS_FIRE_NOVA_ATTACKING.isSet()) {
            cir.setReturnValue(VaultDamageNumberEntity.DamageType.FIRE);
        }
    }
}
