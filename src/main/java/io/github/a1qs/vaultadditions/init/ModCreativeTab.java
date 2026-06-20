package io.github.a1qs.vaultadditions.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModCreativeTab extends CreativeModeTab {

    public ModCreativeTab(String label) {
        super(label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(ModItems.POWER_ORB.get());
    }
}
