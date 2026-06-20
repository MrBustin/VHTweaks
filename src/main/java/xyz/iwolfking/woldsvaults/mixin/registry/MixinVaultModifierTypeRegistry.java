package xyz.iwolfking.woldsvaults.mixin.registry;

import xyz.iwolfking.woldsvaults.modifiers.ChancePowerCrystalModifier;
import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.modifier.registry.VaultModifierType;
import iskallia.vault.core.vault.modifier.registry.VaultModifierTypeRegistry;
import iskallia.vault.core.vault.modifier.spi.AbstractChanceModifier;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = VaultModifierTypeRegistry.class, remap = false)
public class MixinVaultModifierTypeRegistry {
    @Shadow @Final private static Map<ResourceLocation, VaultModifierType<?, ?>> MODIFIER_TYPE_REGISTRY;

    static  {
        MODIFIER_TYPE_REGISTRY.put(VaultMod.id("modifier_type/chance_power_crystal"), VaultModifierType.of(ChancePowerCrystalModifier.class, AbstractChanceModifier.Properties.class, ChancePowerCrystalModifier::new));
    }
}
