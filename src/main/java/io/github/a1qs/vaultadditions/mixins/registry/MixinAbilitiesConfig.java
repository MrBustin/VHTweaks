package io.github.a1qs.vaultadditions.mixins.registry;

import io.github.a1qs.vaultadditions.vault.skill.ability.IdonasBarrageAbility;
import iskallia.vault.config.AbilitiesConfig;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(value = AbilitiesConfig.class, remap = false)
public class MixinAbilitiesConfig {
    @Inject(method = "reset", at = @At("TAIL"))
    private void addIdonasBarrage(CallbackInfo ci) {
        AbilitiesConfig config = (AbilitiesConfig)(Object)this;
        config.tree.skills.add(spec("Idonas_Barrage", "Idona's Barrage",
                tier("Idonas_Barrage_Base", "Idona's Barrage",
                        skill("idonas_barrage", "Idona's Barrage", new IdonasBarrageAbility(0, 1, 1, 25.0F, 0.50F, 4.0F, 80.0F, 200.0F, 0.50F, 3.0F)),
                        skill("idonas_barrage", "Idona's Barrage", new IdonasBarrageAbility(0, 1, 1, 30.0F, 0.70F, 4.5F, 100.0F, 190.0F, 0.45F, 3.0F)),
                        skill("idonas_barrage", "Idona's Barrage", new IdonasBarrageAbility(0, 1, 1, 35.0F, 0.90F, 5.0F, 120.0F, 180.0F, 0.40F, 3.0F)),
                        skill("idonas_barrage", "Idona's Barrage", new IdonasBarrageAbility(0, 1, 1, 40.0F, 1.10F, 5.5F, 140.0F, 170.0F, 0.35F, 3.0F)),
                        skill("idonas_barrage", "Idona's Barrage", new IdonasBarrageAbility(0, 1, 1, 45.0F, 1.30F, 6.0F, 160.0F, 160.0F, 0.30F, 3.0F))
                )
        ));
    }

    private static LearnableSkill skill(String id, String name, LearnableSkill skill) {
        return setIdAndName(id, name, skill);
    }

    private static TieredSkill tier(String id, String name, LearnableSkill... tiers) {
        return setIdAndName(id, name, new TieredSkill(0, 1, 1, Stream.of(tiers)));
    }

    private static SpecializedSkill spec(String id, String name, LearnableSkill... specializations) {
        return setIdAndName(id, name, new SpecializedSkill(0, 1, 1, Stream.of(specializations)));
    }

    private static <S extends Skill> S setIdAndName(String id, String name, S skill) {
        skill.setId(id);
        skill.setName(name);
        return skill;
    }
}
