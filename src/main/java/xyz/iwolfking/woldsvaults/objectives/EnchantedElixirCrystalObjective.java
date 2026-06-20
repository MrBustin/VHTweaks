package xyz.iwolfking.woldsvaults.objectives;

import com.google.gson.JsonObject;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.sigil.SigilConfig;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.objective.*;
import iskallia.vault.item.crystal.CrystalData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import xyz.iwolfking.woldsvaults.init.ModCustomVaultObjectiveEntries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EnchantedElixirCrystalObjective extends WoldCrystalObjective {
    protected float objectiveProbability;

    public EnchantedElixirCrystalObjective() {
    }

    @Override
    public void configure(Vault vault, RandomSource random, @Nullable String sigil) {
        int level = ((VaultLevel)vault.get(Vault.LEVEL)).get();
        Optional<SigilConfig.LevelEntry> entry = SigilConfig.getConfig(sigil).map(config -> config.getLevel(level));
        float targetMultiplier = entry.map(SigilConfig.LevelEntry::getElixirTargetMultiplier).orElse(1.0F);
        vault.ifPresent(Vault.OBJECTIVES, (objectives) -> {
            objectives.add(EnchantedElixirObjective.create().withTargetMultiplier(targetMultiplier).add(LodestoneObjective.of(this.objectiveProbability).add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.ELIXIR, "enchanted_elixir", level, true)).add(VictoryObjective.of(300))));
            objectives.add(BailObjective.create(true, new ResourceLocation[]{ClassicPortalLogic.EXIT}));
            objectives.add(DeathObjective.create(true));
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
        });
    }


    public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
        tooltip.add((new TextComponent("Objective: ")).append((new TextComponent("Enchanted Elixir")).withStyle(Style.EMPTY.withColor((Integer)this.getColor(time).orElseThrow()))));
    }

    public Optional<Integer> getColor(float time) {
        return Optional.of(7012807);
    }

    public Optional<CompoundTag> writeNbt() {
        CompoundTag nbt = new CompoundTag();
        Adapters.FLOAT.writeNbt(this.objectiveProbability).ifPresent((tag) -> {
            nbt.put("objective_probability", tag);
        });
        return Optional.of(nbt);
    }

    public void readNbt(CompoundTag nbt) {
        this.objectiveProbability = (Float)Adapters.FLOAT.readNbt(nbt.get("objective_probability")).orElse(0.0F);
    }

    public Optional<JsonObject> writeJson() {
        JsonObject json = new JsonObject();
        Adapters.FLOAT.writeJson(this.objectiveProbability).ifPresent((tag) -> {
            json.add("objective_probability", tag);
        });
        return Optional.of(json);
    }

    public void readJson(JsonObject json) {
        this.objectiveProbability = (Float)Adapters.FLOAT.readJson(json.get("objective_probability")).orElse(0.0F);
    }

    @Override
    ResourceLocation getObjectiveId() {
        return ModCustomVaultObjectiveEntries.ENCHANTED_ELIXIR.getRegistryName();
    }
}
