package xyz.iwolfking.woldsvaults.mixin.armor_effects;

import xyz.iwolfking.woldsvaults.configs.Configs;
import xyz.iwolfking.woldsvaults.mixin.accessors.AccessorAttributeSnapshot;
import xyz.iwolfking.woldsvaults.mixin.accessors.InvokeAttributeSnapshotAttributeValue;
import xyz.iwolfking.woldsvaults.gear.effect.AttributeTransmogEffect;
import xyz.iwolfking.woldsvaults.gear.effect.VanillaAttributeArmorTransmogEffect;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotCalculator;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(value = AttributeSnapshotCalculator.class, remap = false)
public class MixinAttributeSnapshotCalculator {
    @Unique private static final Map<UUID, List<AttributeTransmogEffect>> woldsvaults$setEffects = new HashMap<>();

    @Inject(method = "computeSnapshot", at = @At("HEAD"))
    private static void injectArmorSetCompute(ServerPlayer player, AttributeSnapshot snapshot, CallbackInfo ci) {
        woldsvaults$addArmorSetEffects(player, snapshot);
    }

    @Unique
    private static void woldsvaults$addArmorSetEffects(ServerPlayer player, AttributeSnapshot snapshot) {
        List<AttributeTransmogEffect> effects = Configs.TRANSMOG_EFFECTS_CONFIG.getEffects(player, AttributeTransmogEffect.class);
        for (AttributeTransmogEffect effect : effects) {
            if (effect instanceof VanillaAttributeArmorTransmogEffect<?> vanillaEffect) {
                vanillaEffect.apply(player);
            }

            Map<VaultGearAttribute<?>, AttributeSnapshot.AttributeValue<?, ?>> gearAttributeValues = ((AccessorAttributeSnapshot) snapshot).getGearAttributeValues();
            VaultGearAttribute<?> attribute = effect.getVaultGearAttributeInstance().getAttribute();
            AttributeSnapshot.AttributeValue<?, ?> attributeSnapshotValue = gearAttributeValues.computeIfAbsent(
                    attribute,
                    attr -> InvokeAttributeSnapshotAttributeValue.invokeConstructor()
            );
            InvokeAttributeSnapshotAttributeValue snapshotInvoker = (InvokeAttributeSnapshotAttributeValue) attributeSnapshotValue;
            snapshotInvoker.invokeAddCachedValue(effect.getVaultGearAttributeInstance().getValue());
        }

        List<AttributeTransmogEffect> oldEffects = woldsvaults$setEffects.remove(player.getUUID());
        if (oldEffects != null) {
            oldEffects.removeAll(effects);
            for (AttributeTransmogEffect<?> effect : oldEffects) {
                if (effect instanceof VanillaAttributeArmorTransmogEffect<?> vanillaEffect) {
                    vanillaEffect.remove(player);
                }
            }
        }
        woldsvaults$setEffects.put(player.getUUID(), effects);
    }
}
