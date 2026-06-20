package xyz.iwolfking.woldsvaults.mixin.the_vault.accessors;

import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.snapshot.AttributeSnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = AttributeSnapshot.class, remap = false)
public interface AttributeSnapshotAccessor {
    @Accessor
    Map<VaultGearAttribute<?>, AttributeSnapshot.AttributeValue<?, ?>> getGearAttributeValues();
}
