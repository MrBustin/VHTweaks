package xyz.iwolfking.woldsvaults.mixin.the_vault.skills;



import iskallia.vault.init.ModAbilityLabelBindings;
import iskallia.vault.skill.ability.component.AbilityLabelFormatters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.abilities.*;

import java.util.Map;

@Mixin(value = ModAbilityLabelBindings.class,remap = false)
public abstract class MixinModAbilityLabelBindings {


    @Inject(method = "register()V",at = @At("TAIL"))
    private static void registerDescriptions(CallbackInfo ci) {
        ModAbilityLabelBindings.register(ColossusAbility.class, Map.of(
                "additionalResistance",
                ability -> AbilityLabelFormatters.percentRounded(ability.getAdditionalResistance()),
                "size",
                ability -> AbilityLabelFormatters.percentRounded(ability.getSize()),
                "cooldown",
                ability -> AbilityLabelFormatters.ticks(ability.getCooldownTicks()),
                "manaCost",
                ability -> AbilityLabelFormatters.integer((int) ability.getManaCost()),
                "adjustedDuration",
                ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks())

        ));
        ModAbilityLabelBindings.register(SneakyGetawayAbility.class, Map.of(
                "speed",
                ability -> AbilityLabelFormatters.percentRounded(ability.getSpeedPercentAdded()),
                "size",
                ability -> AbilityLabelFormatters.percentRounded(ability.getSize()),
                "cooldown",
                ability -> AbilityLabelFormatters.ticks(ability.getCooldownTicks()),
                "manaCost",
                ability -> AbilityLabelFormatters.integer((int) ability.getManaCost()),
                "adjustedDuration",
                ability -> AbilityLabelFormatters.ticks(ability.getDurationTicks())

        ));
        ModAbilityLabelBindings.register(VeinMinerChainAbility.class, Map.of(
                "blocks",
                ability -> AbilityLabelFormatters.integer(ability.getUnmodifiedBlockLimit()),
                "distance",
                ability -> AbilityLabelFormatters.integer(ability.getRange())

        ));
        ModAbilityLabelBindings.register(LevitateAbility.class, Map.of(
                "levitateSpeed",
                ability -> AbilityLabelFormatters.decimal(ability.getLevitateSpeed())

        ));

        ModAbilityLabelBindings.register(EvokerFangsAbility.class, Map.of(
                "radius",
                ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
                "adjustedRadius",
                ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
                "baseDamage",
                ability -> AbilityLabelFormatters.decimal(ability.getBaseDamage()),
                "damageMultiplier",
                ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getDamageMultiplier()),
                "executeThreshold",
                ability -> AbilityLabelFormatters.percentRounded(ability.getExecuteThreshold()),
                "cooldown",
                ability -> AbilityLabelFormatters.ticks(ability.getCooldownTicks()),
                "manaCost",
                ability -> AbilityLabelFormatters.integer((int) ability.getManaCost())
        ));

        ModAbilityLabelBindings.register(EvokerFangsMawAbility.class, Map.of(
                "radius",
                ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
                "adjustedRadius",
                ability -> AbilityLabelFormatters.decimal(ability.getRadius()),
                "baseDamage",
                ability -> AbilityLabelFormatters.decimal(ability.getBaseDamage()),
                "damageMultiplier",
                ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getDamageMultiplier()),
                "effectAmplifier",
                ability -> AbilityLabelFormatters.integer(ability.getEffectAmplifier()),
                "heartFragmentChance",
                ability -> AbilityLabelFormatters.percentTwoDecimalPlaces(ability.getHeartFragmentChance()),
                "cooldown",
                ability -> AbilityLabelFormatters.ticks(ability.getCooldownTicks()),
                "manaCost",
                ability -> AbilityLabelFormatters.integer((int) ability.getManaCost())
        ));

        ModAbilityLabelBindings.register(EmpowerFireAuraAbility.class, Map.of(
                "adjustedRadius",
                ability -> AbilityLabelFormatters.decimal(ability.getUnmodifiedRadius()),
                "manaCostPerSecond",
                ability -> AbilityLabelFormatters.integer((int) ability.getManaCostPerSecond()),
                "cooldown",
                ability -> AbilityLabelFormatters.ticks(ability.getCooldownTicks())
        ));
    }
}
