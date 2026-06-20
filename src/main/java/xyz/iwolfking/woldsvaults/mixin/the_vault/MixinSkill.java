package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.skill.base.Skill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.abilities.*;
import xyz.iwolfking.woldsvaults.expertises.CraftsmanExpertise;
import xyz.iwolfking.woldsvaults.expertises.EclecticGearExpertise;
import xyz.iwolfking.woldsvaults.expertises.NavigatorExpertise;
import xyz.iwolfking.woldsvaults.expertises.PylonPilfererExpertise;
import xyz.iwolfking.woldsvaults.prestige.CrystalSizePrestigePower;
import xyz.iwolfking.woldsvaults.prestige.GearSealerPrestigePower;
import xyz.iwolfking.woldsvaults.prestige.ToolCapacityPrestigePower;


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

        //Talents
        this.register("crit_hit_chance", CritHitChanceTalent.class, CritHitChanceTalent::new);
        this.register("crit_hit_damage", CritHitDamageTalent.class, CritHitDamageTalent::new);

        //Expertises
        this.register("craftsman", CraftsmanExpertise.class, CraftsmanExpertise::new);
        this.register("augmentation_luck", EclecticGearExpertise.class, EclecticGearExpertise::new);
        this.register("pylon_pilferer", PylonPilfererExpertise.class, PylonPilfererExpertise::new);
        this.register("navigator", NavigatorExpertise.class, NavigatorExpertise::new);

        //Prestige Powers
        this.register("tool_capacity_power", ToolCapacityPrestigePower.class, ToolCapacityPrestigePower::new);
        this.register("gear_sealer_power", GearSealerPrestigePower.class, GearSealerPrestigePower::new);
        this.register("crystal_size_power", CrystalSizePrestigePower.class, CrystalSizePrestigePower::new);
    }
}
