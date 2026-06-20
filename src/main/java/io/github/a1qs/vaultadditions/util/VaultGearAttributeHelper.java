package io.github.a1qs.vaultadditions.util;

import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.ability.AbilityCooldownPercentAttribute;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.attribute.ability.AbilityManaCostPercentAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import iskallia.vault.gear.attribute.talent.TalentLevelAttribute;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.world.effect.MobEffect;

public class VaultGearAttributeHelper {
    public static VaultGearAttributeInstance<AbilityLevelAttribute> abilityLevel(String name, int levelChange) {
        return VaultGearAttributeInstance.cast(ModGearAttributes.ABILITY_LEVEL, new AbilityLevelAttribute(name, levelChange));
    }

    public static VaultGearAttributeInstance<TalentLevelAttribute> talentLevel(String name, int levelChange) {
        return VaultGearAttributeInstance.cast(ModGearAttributes.TALENT_LEVEL, new TalentLevelAttribute(name, levelChange));
    }

    public static VaultGearAttributeInstance<AbilityCooldownPercentAttribute> abilityCooldownPercentage(String abilityName, float amount) {
        return VaultGearAttributeInstance.cast(ModGearAttributes.ABILITY_COOLDOWN_PERCENT, new AbilityCooldownPercentAttribute(abilityName, amount));
    }

    public static VaultGearAttributeInstance<AbilityManaCostPercentAttribute> abilityManaCostPercentage(String abilityName, float amount) {
        return VaultGearAttributeInstance.cast(ModGearAttributes.ABILITY_MANACOST_PERCENT, new AbilityManaCostPercentAttribute(abilityName, amount));
    }

    public static VaultGearAttributeInstance<EffectGearAttribute> potionEffect(MobEffect effect, int amplifier) {
        return VaultGearAttributeInstance.cast(ModGearAttributes.EFFECT, new EffectGearAttribute(effect, amplifier));
    }

    public static VaultGearAttributeInstance<Float> airMobilitySpeed(float playerBaseSpeed) {
        return VaultGearAttributeInstance.cast(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.AIR_MOBILITY_SPEED, playerBaseSpeed);
    }

    public static VaultGearAttributeInstance<Float> airMobilityControl(float playerBaseAirMovement) {
        return VaultGearAttributeInstance.cast(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.AIR_MOBILITY_CONTROL, playerBaseAirMovement);
    }
}
