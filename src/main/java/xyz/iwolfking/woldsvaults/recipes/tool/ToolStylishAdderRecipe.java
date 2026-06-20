package xyz.iwolfking.woldsvaults.recipes.tool;

import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.ToolGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.recipe.anvil.AnvilContext;
import iskallia.vault.recipe.anvil.VanillaAnvilRecipe;
import iskallia.vault.item.tool.ToolItem;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;
import xyz.iwolfking.woldsvaults.init.ModItems;


public class ToolStylishAdderRecipe extends VanillaAnvilRecipe {


    @Override
    public boolean onSimpleCraft(AnvilContext context) {

        ItemStack primary = context.getInput()[0];
        ItemStack secondary = context.getInput()[1];
        if (primary.getItem() instanceof ToolItem tool && secondary.getItem() == ModItems.STYLISH_FOCUS) {
            ItemStack output = primary.copy();
            VaultGearData gear = ToolGearData.read(output);
            if(gear.getFirstValue(ModGearAttributes.ROTATING_TOOL).isPresent()) {
                return false;
            }
            else {
                gear.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.ROTATING_TOOL, true));
            }

            gear.write(output);
            context.setOutput(output);

            context.onTake(context.getTake().append(() -> {
                context.getInput()[0].shrink(1);
                context.getInput()[1].shrink(1);
            }));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRegisterJEI(IRecipeRegistration iRecipeRegistration) {

    }
}
