package xyz.iwolfking.woldsvaults.init;

import iskallia.vault.item.crystal.CrystalData;
import xyz.iwolfking.woldsvaults.world.vault.layout.ElytraCrystalLayout;
import xyz.iwolfking.woldsvaults.objectives.ScalingBallisticBingoCrystalObjective;

public class ModCrystalObjectives {
    public static void init() {
        CrystalData.OBJECTIVE.register("scaling_ballistic_bingo", ScalingBallisticBingoCrystalObjective.class, ScalingBallisticBingoCrystalObjective::new);
        CrystalData.LAYOUT.register("elytra", ElytraCrystalLayout.class, ElytraCrystalLayout::new);
    }
}
