package xyz.iwolfking.woldsvaults.mixin.power_crystals;

import xyz.iwolfking.woldsvaults.configs.Configs;
import iskallia.vault.skill.SkillGates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = SkillGates.class, remap = false)
public class MixinSkillGates {

    @Inject(
            method = "getAllSkillIds",
            at = @At("RETURN")
    )
    private void addPowerSkills(CallbackInfoReturnable<List<String>> cir) {
        List<String> allSkillIds = cir.getReturnValue();
        Configs.POWERS.getTree().skills.forEach(skill -> allSkillIds.add(skill.getId()));
    }
}
