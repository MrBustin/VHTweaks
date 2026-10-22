package xyz.iwolfking.woldsvaults.mixin.the_vault.accessors;

import iskallia.vault.config.gear.VaultGearModificationConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = VaultGearModificationConfig.class, remap = false)
public interface VaultGearModificationConfigAccessor {
    @Accessor("operationConfiguration")
    Map<ResourceLocation, VaultGearModificationConfig.OperationConfig> woldsvaults$getOperationConfiguration();
}
