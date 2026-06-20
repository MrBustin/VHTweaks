package io.github.a1qs.vaultadditions.mixins.power_crystals;

import io.github.a1qs.vaultadditions.data.PlayerPowersData;
import io.github.a1qs.vaultadditions.mixins.accessors.AccessorAttributeSnapshot;
import io.github.a1qs.vaultadditions.mixins.accessors.InvokeAttributeSnapshotAttributeValue;
import io.github.a1qs.vaultadditions.util.MiscUtil;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.GearAttributeSkill;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotCalculator;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = AttributeSnapshotCalculator.class, remap = false)
public class MixinAttributeSnapshotCalculator {

    @Inject(method = "computeSnapshot", at = @At(value = "INVOKE", target = "Liskallia/vault/snapshot/AttributeSnapshotCalculator;addExpertiseInformationToSnapshot(Lnet/minecraft/server/level/ServerPlayer;Liskallia/vault/snapshot/AttributeSnapshot;)V"))
    private static void injectPowerCompute(ServerPlayer player, AttributeSnapshot snapshot, CallbackInfo ci) {
        vaultadditions$addPowerInformationToSnapshot(player, snapshot);
    }

    @Unique
    private static void vaultadditions$addPowerInformationToSnapshot(ServerPlayer player, AttributeSnapshot snapshot) {
        PowerTree expertise = PlayerPowersData.get(player.getLevel()).getPowers(player);
        expertise.iterate(GearAttributeSkill.class,attributeSkill -> {
            if (attributeSkill instanceof Skill skill && skill.isUnlocked()) {
                attributeSkill.getGearAttributes(MiscUtil.ofPowers(player)).forEach(attributeValue -> {
                    Map<VaultGearAttribute<?>, AttributeSnapshot.AttributeValue<?, ?>> gearAttributeValues = ((AccessorAttributeSnapshot) snapshot).getGearAttributeValues();
                    VaultGearAttribute<?> attribute = attributeValue.getAttribute();
                    AttributeSnapshot.AttributeValue<?, ?> attributeSnapshotValue = gearAttributeValues.computeIfAbsent(
                            attribute,
                            attr -> InvokeAttributeSnapshotAttributeValue.invokeConstructor()
                    );

                    InvokeAttributeSnapshotAttributeValue snapshotInvoker = (InvokeAttributeSnapshotAttributeValue) attributeSnapshotValue;
                    snapshotInvoker.invokeAddCachedValue(attributeValue.getValue());
                });
            }
        });
    }

}
