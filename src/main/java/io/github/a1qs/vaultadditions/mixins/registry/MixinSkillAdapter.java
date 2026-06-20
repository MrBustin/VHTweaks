package io.github.a1qs.vaultadditions.mixins.registry;

import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import io.github.a1qs.vaultadditions.vault.skill.ability.BladeFrenzyAbility;
import io.github.a1qs.vaultadditions.vault.skill.ability.IdonasBarrageAbility;
import io.github.a1qs.vaultadditions.vault.skill.ability.LegacyManaShieldAbility;
import io.github.a1qs.vaultadditions.vault.skill.ability.ShieldWallAbility;
import io.github.a1qs.vaultadditions.vault.skill.ability.ThornedFrenzyAbility;
import io.github.a1qs.vaultadditions.vault.skill.power.*;
import iskallia.vault.skill.base.Skill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Skill.Adapter.class, remap = false)
public class MixinSkillAdapter {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onAdapterInit(CallbackInfo ci) {
        Skill.Adapter instance = (Skill.Adapter) (Object) this;

        /* Power Skills */
        instance.register("vaultadditions_expertises", PowerTree.class, PowerTree::new);

        instance.register("power_vanilla_attribute", VanillaAttributePower.class, VanillaAttributePower::new);
        instance.register("power_vanilla_attribute_2", VanillaAttributePowerAdditional.class, VanillaAttributePowerAdditional::new);
        instance.register("power_gear_attribute", GearAttributePower.class, GearAttributePower::new);
        instance.register("power_gear_attribute_2", GearAttributePowerAdditional.class, GearAttributePowerAdditional::new);
        instance.register("power_skill_point_increase", SkillPointIncreasePower.class, SkillPointIncreasePower::new);
        instance.register("power_expertise_point_increase", ExpertisePointIncreasePower.class, ExpertisePointIncreasePower::new);
        instance.register("power_fall_reduction", FallReductionPower.class, FallReductionPower::new);
        instance.register("power_kinetic_reduction", KineticReductionPower.class, KineticReductionPower::new);

        instance.register("power_air_mobility", AirMobilityPower.class, AirMobilityPower::new);

        /* Abilities */

        instance.register("shield_wall", ShieldWallAbility.class, ShieldWallAbility::new);
        instance.register("legacy_mana_shield", LegacyManaShieldAbility.class, LegacyManaShieldAbility::new);
        instance.register("blade_frenzy", BladeFrenzyAbility.class, BladeFrenzyAbility::new);
        instance.register("thorned_frenzy", ThornedFrenzyAbility.class, ThornedFrenzyAbility::new);
        instance.register("idonas_barrage", IdonasBarrageAbility.class, IdonasBarrageAbility::new);
    }
}
