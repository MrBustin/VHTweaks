package xyz.iwolfking.woldsvaults.api.gear.modification;

import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.modification.GearModification;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.init.ModItems;

import java.util.Random;

public class EmpoweredFundamentalGearModification extends GearModification {
    public EmpoweredFundamentalGearModification() {
        super(WoldsVaults.id("improve_implicit"));
    }

    @Override
    public ItemStack getDisplayStack() {
        return ModItems.EMPOWERED_FUNDAMENTAL_FOCUS.getDefaultInstance();
    }

    @Override
    public Result canApply(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
        if (!VaultGearCraftingHelper.reducePotential(stack.copy(), player, this)) {
            return Result.makeGenericError("out_of_potential");
        }
        return WoldGearModifierHelper.canImproveRandomImplicit(stack);
    }

    @Override
    public Result doModification(ItemStack itemStack, ItemStack itemStack1, Player player, Random random) {
        return WoldGearModifierHelper.improveRandomImplicit(itemStack, player.level.getGameTime(), random);
    }
}
