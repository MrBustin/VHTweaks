package xyz.iwolfking.woldsvaults.objectives;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.sigil.SigilConfig;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.*;
import iskallia.vault.item.crystal.CrystalData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.TooltipFlag;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.init.ModCustomVaultObjectiveEntries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BallisticBingoCrystalObjective extends WoldCrystalObjective {
    protected float objectiveProbability;
    protected boolean blackout;

    public BallisticBingoCrystalObjective() {
    }

    public BallisticBingoCrystalObjective(float objectiveProbability) {
        this.objectiveProbability = objectiveProbability;
    }

    public BallisticBingoCrystalObjective(float objectiveProbability, boolean blackout) {
        this.objectiveProbability = objectiveProbability;
        this.blackout = blackout;
    }

    @Override
    public void configure(Vault vault, RandomSource random, @Nullable String sigil) {
        int level = vault.get(Vault.LEVEL).get();
        Optional<SigilConfig.LevelEntry> entry = SigilConfig.getConfig(sigil).map((config) -> config.getLevel(level));
        vault.ifPresent(Vault.OBJECTIVES, objectives -> {
            ModConfigs.BALLISTIC_BINGO_CONFIG.generate(entry.map(SigilConfig.LevelEntry::getBingoPool).orElse(VaultMod.id("default")), level).ifPresent((task) -> objectives.add(BallisticBingoObjective.of(task, blackout).add(GridGatewayObjective.of(this.objectiveProbability).add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.valueOf("BALLISTIC_BINGO"), "ballistic_bingo", level, true)).add(VictoryObjective.of(300)))));
            objectives.add(BailObjective.create(true, ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
        });
    }

    public Optional<Integer> getColor(float time) {
        return Optional.of(3093081);
    }

    @Override
    public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int level) {
        MutableComponent objectiveTooltip = new TextComponent("Objective: ").withStyle(ChatFormatting.WHITE);

        if (blackout) {
            objectiveTooltip.append(new TextComponent("Blackout ").withStyle(Style.EMPTY.withColor(this.getColor(time).orElseThrow())));
        }

        Component objectiveName = new TranslatableComponent(
                "vault_objective." + getObjectiveId().toString().replace(":", "."))
                .withStyle(Style.EMPTY.withColor(this.getColor(time).orElseThrow()));

        tooltip.add(objectiveTooltip.append(objectiveName));
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        CompoundTag nbt = new CompoundTag();
        Adapters.FLOAT.writeNbt(this.objectiveProbability).ifPresent(tag -> nbt.put("objective_probability", tag));
        Adapters.BOOLEAN.writeNbt(this.blackout).ifPresent(tag -> nbt.put("blackout", tag));
        return Optional.of(nbt);
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        this.objectiveProbability = Adapters.FLOAT.readNbt(nbt.get("objective_probability")).orElse(0.0F);
        this.blackout = Adapters.BOOLEAN.readNbt(nbt.get("blackout")).orElse(false);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        JsonObject json = new JsonObject();
        Adapters.FLOAT.writeJson(this.objectiveProbability).ifPresent(tag -> json.add("objective_probability", tag));
        Adapters.BOOLEAN.writeJson(this.blackout).ifPresent(tag -> json.add("blackout", tag));
        return Optional.of(json);
    }

    @Override
    public void readJson(JsonObject json) {
        this.objectiveProbability = Adapters.FLOAT.readJson(json.get("objective_probability")).orElse(0.0F);
        this.blackout = Adapters.BOOLEAN.readJson(json.get("blackout")).orElse(false);
    }

    @Override
    public net.minecraft.resources.ResourceLocation getObjectiveId() {
        return ModCustomVaultObjectiveEntries.BALLISTIC_BINGO.getRegistryName();
    }
}
