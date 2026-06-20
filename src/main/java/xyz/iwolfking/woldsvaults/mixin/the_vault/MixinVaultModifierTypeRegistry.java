package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.core.vault.modifier.registry.VaultModifierType;
import iskallia.vault.core.vault.modifier.registry.VaultModifierTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.modifiers.vault.*;

import java.util.Map;

@Mixin(value = VaultModifierTypeRegistry.class, remap = false)
public class MixinVaultModifierTypeRegistry {
    @Shadow @Final private static Map<ResourceLocation, VaultModifierType<?, ?>> MODIFIER_TYPE_REGISTRY;

    static {
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/mob_death_bomb"), VaultModifierType.of(MobDeathBombModifier.class, MobDeathBombModifier.Properties.class, MobDeathBombModifier::new));
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/chest_break_bomb"), VaultModifierType.of(ChestOpenBombModifier.class, ChestOpenBombModifier.Properties.class, ChestOpenBombModifier::new));
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/enchanted_event_chance"), VaultModifierType.of(EnchantedVaultModifier.class, EnchantedVaultModifier.Properties.class, EnchantedVaultModifier::new));
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/retro_spawn_modifier"), VaultModifierType.of(RetroSpawnVaultModifier.class, RetroSpawnVaultModifier.Properties.class, RetroSpawnVaultModifier::new));
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/experience_multiplier"), VaultModifierType.of(ExperienceMultiplierModifier.class, ExperienceMultiplierModifier.Properties.class, ExperienceMultiplierModifier::new));
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/difficulty_lock"), VaultModifierType.of(DifficultyLockModifier.class, DifficultyLockModifier.Properties.class, DifficultyLockModifier::new));
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/enchanted_elixir_tags"), VaultModifierType.of(EnchantedElixirPoolModifier.class, EnchantedElixirPoolModifier.Properties.class, EnchantedElixirPoolModifier::new));
        MODIFIER_TYPE_REGISTRY.put(WoldsVaults.id("modifier_type/enchanted_elixir_event_count"), VaultModifierType.of(EnchantedElixirEventAmountModifier.class, EnchantedElixirEventAmountModifier.Properties.class, EnchantedElixirEventAmountModifier::new));
    }
}
