package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.skill.base.Skill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.abilities.*;
import xyz.iwolfking.woldsvaults.expertises.*;
import xyz.iwolfking.woldsvaults.prestige.CrystalSizePrestigePower;
import xyz.iwolfking.woldsvaults.prestige.GearSealerPrestigePower;
import xyz.iwolfking.woldsvaults.prestige.ToolCapacityPrestigePower;
import xyz.iwolfking.woldsvaults.talent.*;
import xyz.iwolfking.woldsvaults.talent.lucky_hit.CooldownReductionLuckyHitTalent;
import xyz.iwolfking.woldsvaults.talent.lucky_hit.ExecutionDamageLuckyHitTalent;
import xyz.iwolfking.woldsvaults.talent.lucky_hit.FangedStrikeLuckyHitTalent;


@Mixin(value = Skill.Adapter.class, remap = false)
public abstract class MixinSkill extends TypeSupplierAdapter<Skill> {
    public MixinSkill(String key, boolean nullable) {
        super(key, nullable);
    }

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void addSkills(CallbackInfo ci) {

        //Abilities
        this.register("colossus", ColossusAbility.class,ColossusAbility::new);
        this.register("sneaky_getaway", SneakyGetawayAbility.class,SneakyGetawayAbility::new);
        this.register("vein_miner_chain", VeinMinerChainAbility.class, VeinMinerChainAbility::new);
        this.register("levitate", LevitateAbility.class,LevitateAbility::new);
        this.register("fangs", EvokerFangsAbility.class, EvokerFangsAbility::new);
        this.register("fangs_maw", EvokerFangsMawAbility.class, EvokerFangsMawAbility::new);
        this.register("nova_burn", NovaBurnAbility.class, NovaBurnAbility::new);



        //Talents
        this.register("crit_hit_chance", CritHitChanceTalent.class, CritHitChanceTalent::new);
        this.register("crit_hit_damage", CritHitDamageTalent.class, CritHitDamageTalent::new);
        this.register("potent_elixir", PotentElixirTalent.class, PotentElixirTalent::new);
        this.register("healthy_elixir", HealthyElixirTalent.class, HealthyElixirTalent::new);
        this.register("execution_lucky_hit", ExecutionDamageLuckyHitTalent.class, ExecutionDamageLuckyHitTalent::new);
        this.register("cooldown_lucky_hit", CooldownReductionLuckyHitTalent.class, CooldownReductionLuckyHitTalent::new);
        this.register("fanged_lucky_hit", FangedStrikeLuckyHitTalent.class, FangedStrikeLuckyHitTalent::new);
        this.register("momentum_engine", MomentumEngineTalent.class, MomentumEngineTalent::new);
        this.register("voltaic_distance", VoltaicDistanceTalent.class, VoltaicDistanceTalent::new);


        //Expertises
        this.register("craftsman", CraftsmanExpertise.class, CraftsmanExpertise::new);
        this.register("augmentation_luck", EclecticGearExpertise.class, EclecticGearExpertise::new);
        this.register("pylon_pilferer", PylonPilfererExpertise.class, PylonPilfererExpertise::new);
        this.register("navigator", NavigatorExpertise.class, NavigatorExpertise::new);
        this.register("grave_insurance", GraveInsurance.class, GraveInsurance::new);
        this.register("gambler", GamblerExpertise.class, GamblerExpertise::new);
        this.register("surprise_favors", SurpriseModifiersExpertise.class, SurpriseModifiersExpertise::new);



        //Prestige Powers
        this.register("tool_capacity_power", ToolCapacityPrestigePower.class, ToolCapacityPrestigePower::new);
        this.register("gear_sealer_power", GearSealerPrestigePower.class, GearSealerPrestigePower::new);
        this.register("crystal_size_power", CrystalSizePrestigePower.class, CrystalSizePrestigePower::new);
    }
}
