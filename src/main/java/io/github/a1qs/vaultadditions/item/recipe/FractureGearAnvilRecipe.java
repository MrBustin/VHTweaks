package io.github.a1qs.vaultadditions.item.recipe;

import io.github.a1qs.vaultadditions.init.ModItems;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.recipe.anvil.AnvilContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FractureGearAnvilRecipe extends DamageGearAnvilRecipe {
    @Override
    public boolean onSimpleCraft(AnvilContext context) {
        if (!super.onSimpleCraft(context)) {
            return false;
        }
        ItemStack output = context.getOutput();
        VaultGearData data = VaultGearData.read(output);
        data.setUsedRepairSlots(data.getRepairSlots());
        data.write(output);
        return true;
    }

    @Override
    protected Item getCoreItem() {
        return ModItems.FRACTURE_CORE.get();
    }
}