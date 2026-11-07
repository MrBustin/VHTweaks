package xyz.iwolfking.woldsvaults.mixin.the_vault.skills.modifications;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.skill.ability.effect.IceBoltChunkAbility;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.iwolfking.woldsvaults.modifiers.gear.special.GlacialBlastModification;

@Mixin(value = IceBoltChunkAbility.class, remap = false)
public abstract class MixinIceBoltChunkAbility {
    @ModifyExpressionValue(method = "applyDirectGlacialPrison", at = @At(value = "FIELD",
            target = "Liskallia/vault/skill/ability/effect/IceBoltChunkAbility;glacialChance:F"))
    private float woldsvaults$glacialBlastChance(float chance, @Local(argsOnly = true) ServerPlayer player) {
        int multiplier = 1;
        for (var mod : SpecialAbilityModification.getModifications(player, GlacialBlastModification.class)) {
            multiplier = mod.value().getValue();
        }
        return Math.min(1.0F, chance * Math.max(1, multiplier));
    }
}