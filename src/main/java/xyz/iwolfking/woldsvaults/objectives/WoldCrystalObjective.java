package xyz.iwolfking.woldsvaults.objectives;

import iskallia.vault.item.crystal.objective.CrystalObjective;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class WoldCrystalObjective extends CrystalObjective {

    @Override
    public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int level) {
        MutableComponent objectiveTooltip = new TextComponent("Objective: ")
                .withStyle(ChatFormatting.WHITE);

        Component objectiveName = new TranslatableComponent(
                "vault_objective." + getObjectiveId().toString().replace(":", "."))
                .withStyle(Style.EMPTY.withColor(this.getColor(time).orElseThrow()));

        tooltip.add(objectiveTooltip.append(objectiveName));
    }


    abstract ResourceLocation getObjectiveId();
}
