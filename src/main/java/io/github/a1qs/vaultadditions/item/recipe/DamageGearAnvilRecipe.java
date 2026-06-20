package io.github.a1qs.vaultadditions.item.recipe;

import io.github.a1qs.vaultadditions.init.ModItems;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.recipe.anvil.AnvilContext;
import iskallia.vault.recipe.anvil.VanillaAnvilRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DamageGearAnvilRecipe extends VanillaAnvilRecipe {
    @Override
    public boolean onSimpleCraft(AnvilContext context) {
        ItemStack primary = context.getInput()[0];
        ItemStack secondary = context.getInput()[1];
        if (!(primary.getItem() instanceof VaultGearItem) || secondary.isEmpty() || secondary.getItem() != getCoreItem()) {
            return false;
        }

        VaultGearData gear = VaultGearData.read(primary);
        if (gear.getUsedRepairSlots() >= gear.getRepairSlots() && primary.getDamageValue() >= primary.getMaxDamage()) {
            return false;
        }

        ItemStack output = primary.copy();
        output.setDamageValue(output.getMaxDamage());
        gear.write(output);
        context.setOutput(output);
        context.onTake(context.getTake().append(() -> {
            context.getInput()[0].shrink(1);
            context.getInput()[1].shrink(1);
        }));
        return true;
    }

    @Override
    public void onRegisterJEI(IRecipeRegistration registration) {}

    protected Item getCoreItem() {
        return ModItems.DAMAGE_CORE.get();
    }
}
