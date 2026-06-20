package io.github.a1qs.vaultadditions.mixins.power_crystals;

import io.github.a1qs.vaultadditions.config.Configs;
import iskallia.vault.skill.SkillGates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(value = SkillGates.class, remap = false)
public class MixinSkillGates {

    @ModifyVariable(
            method = "getAllSkillIds",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private List<String> addPowerSkills(List<String> allSkillIds) {
        Configs.POWERS.getTree().skills.forEach(skill -> allSkillIds.add(skill.getId()));
        return allSkillIds;
    }
}
