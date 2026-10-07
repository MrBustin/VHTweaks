package xyz.iwolfking.woldsvaults.objectives;

import com.google.gson.JsonObject;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.item.crystal.CrystalData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.init.ModCustomVaultObjectiveEntries;
import xyz.iwolfking.woldsvaults.world.vault.layout.ElytraCrystalLayout;

import javax.annotation.Nullable;
import java.util.Optional;

public class ElytraCrystalObjective extends WoldCrystalObjective {
    private static final float PILLAR_PROBABILITY = 1.0F;

    @Override
    public void configure(Vault vault, RandomSource random, @Nullable String sigil) {
        int level = vault.get(Vault.LEVEL).get();

        // Elytra objectives always use their branch-free course, including when
        // the objective was assigned with a command rather than a seal.
        new ElytraCrystalLayout().configure(vault, random, sigil);

        vault.ifPresent(Vault.OBJECTIVES, objectives -> {
            objectives.add(ElytraObjective.of(PILLAR_PROBABILITY)
                    .add(AwardCrateObjective.ofConfig(
                            VaultCrateBlock.Type.ELIXIR, "enchanted_elixir", level, true))
                    .add(VictoryObjective.of(300)));
            objectives.add(BailObjective.create(true, ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
        });
    }

    @Override
    public Optional<Integer> getColor(float time) {
        return Optional.of(0x9B7BFF);
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return Optional.of(new CompoundTag());
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        // This first version has no configurable crystal data.
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject());
    }

    @Override
    public void readJson(JsonObject json) {
        // This first version has no configurable crystal data.
    }

    @Override
    ResourceLocation getObjectiveId() {
        return ModCustomVaultObjectiveEntries.ELYTRA.getRegistryName();
    }
}
