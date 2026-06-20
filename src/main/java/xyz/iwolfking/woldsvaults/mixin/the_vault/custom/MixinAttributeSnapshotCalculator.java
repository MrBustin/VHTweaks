package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotCalculator;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;
import xyz.iwolfking.woldsvaults.util.calc.AttackDamageHelper;

@Mixin(value = AttributeSnapshotCalculator.class, remap = false)
public class MixinAttributeSnapshotCalculator {
    @Inject(method = "computeSnapshot", at = @At("TAIL"))
    private static void applyThornsAdaptation(ServerPlayer player, AttributeSnapshot snapshot, CallbackInfo ci) {
        float thornsScaling = snapshot.getAttributeValue(ModGearAttributes.THORNS_ADAPTATION, VaultGearAttributeTypeMerger.floatSum());
        if (thornsScaling > 0) {
            double attackDamage = AttackDamageHelper.getAttackDamage(player, snapshot);
            snapshot.addAttributeValue(iskallia.vault.init.ModGearAttributes.THORNS_DAMAGE_FLAT, (float) (attackDamage * thornsScaling));
        }
    }
}
