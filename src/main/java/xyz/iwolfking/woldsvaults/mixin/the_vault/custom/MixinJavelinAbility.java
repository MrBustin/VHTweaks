package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.entity.entity.VaultThrownJavelin;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.abilities.flexible.FlexibleAbility;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;
import xyz.iwolfking.woldsvaults.events.WoldActiveFlags;

@Mixin(value = VaultThrownJavelin.class, remap = false)
public abstract class MixinJavelinAbility {
    @Shadow
    public abstract Player getThrower();

    @Shadow
    public abstract VaultThrownJavelin.JavelinType getJavelinType();

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Liskallia/vault/event/ActiveFlags;runIfNotSet(Ljava/lang/Runnable;)V"))
    private void enableGearConductForScatterJavelin(ActiveFlags flag, Runnable damageCall) {
        Player thrower = this.getThrower();
        if (thrower == null
                || this.getJavelinType() != VaultThrownJavelin.JavelinType.SCATTER
                || !AttributeSnapshotHelper.getInstance().getSnapshot(thrower).getAttributeValue(
                        ModGearAttributes.JAVELIN_CONDUCT,
                        VaultGearAttributeTypeMerger.anyTrue())) {
            flag.runIfNotSet(damageCall);
            return;
        }

        WoldActiveFlags.IS_GEAR_CONDUCTING_JAVELIN.runIfNotSet(
                () -> flag.runIfNotSet(damageCall));
    }

    @Inject(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Liskallia/vault/util/EntityHelper;knockbackIgnoreResist(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;F)V", remap = false), remap = true)
    private void implodeJavelin(LivingEntity pLiving, CallbackInfo ci) {
        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(this.getThrower());
        if(snapshot.getAttributeValue(ModGearAttributes.IMPLODING_JAVELIN, VaultGearAttributeTypeMerger.anyTrue())) {
            FlexibleAbility flexibleAbility = new FlexibleAbility();
            flexibleAbility.cast("Implode", this.getThrower(), pLiving);
        }
     }

}
