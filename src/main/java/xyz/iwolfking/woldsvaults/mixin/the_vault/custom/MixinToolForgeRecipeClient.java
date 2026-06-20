package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.gear.crafting.recipe.ToolForgeRecipe;
import iskallia.vault.skill.prestige.helper.PrestigeHelper;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import xyz.iwolfking.woldsvaults.prestige.ToolCapacityPrestigePower;

@Mixin(value = ToolForgeRecipe.class, remap = false)
public class MixinToolForgeRecipeClient {
    @ModifyVariable(method = "addCraftingDisplayTooltip", at = @At(value = "STORE"), ordinal = 0)
    private int addPrestigeCapacityToTooltip(int originalCapacity) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return originalCapacity;
        }

        int[] extraCapacity = {0};
        PrestigeHelper.getClientPrestige().iterate(ToolCapacityPrestigePower.class,
                power -> extraCapacity[0] += power.getCapacityIncrease());

        return extraCapacity[0] > 0 ? originalCapacity + extraCapacity[0] : originalCapacity;
    }
}
