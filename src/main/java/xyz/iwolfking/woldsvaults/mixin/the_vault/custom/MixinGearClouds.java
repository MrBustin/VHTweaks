package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.cloud.GearClouds;
import iskallia.vault.gear.cloud.GearCloudTrigger;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.Entropy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

@Mixin(value = GearClouds.class, remap = false)
public abstract class MixinGearClouds {
    @Redirect(method = "roll", at = @At(value = "INVOKE",
            target = "Liskallia/vault/util/Entropy;canExecute(Lnet/minecraft/world/entity/Entity;Liskallia/vault/util/Entropy$Stat;F)Z"))
    private static boolean woldsvaults$cloudChance(Entity entity, Entropy.Stat stat, float chance,
                                                  @Local(argsOnly = true) GearCloudTrigger trigger) {
        if (trigger == GearCloudTrigger.ON_HIT && entity instanceof Player player) {
            chance += AttributeSnapshotHelper.getInstance().getSnapshot(player)
                    .getAttributeValue(ModGearAttributes.INCREASED_EFFECT_CLOUD_CHANCE, VaultGearAttributeTypeMerger.floatSum());
        }
        return Entropy.canExecute(entity, stat, Math.min(1.0F, chance));
    }
}
