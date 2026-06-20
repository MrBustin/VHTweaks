package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinitionRegistry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import java.util.function.Supplier;

@Mixin(value = LootInfoGroupDefinitionRegistry.class, remap = false)
public abstract class MixinLootInfoGroupDefinitionRegistry {
    static {
        register("iskallian_leaves", () -> {
            return new ItemStack(ModBlocks.ISKALLIAN_LEAVES_BLOCK);
        });
        register("hellish_sand", () -> {
            return new ItemStack(ModBlocks.HELLISH_SAND_BLOCK);
        });
        register("dungeon_pedestal", () -> {
            return new ItemStack(ModBlocks.DUNGEON_PEDESTAL_BLOCK);
        });
        register("treasure_pedestal", () -> {
            return new ItemStack(iskallia.vault.init.ModBlocks.TREASURE_PEDESTAL);
        });
//        register("vendor_pedestal", () -> {
//            return new ItemStack(iskallia.vault.init.ModBlocks.SHOP_PEDESTAL);
//        });
        register("digsite_sand", () -> {
            return new ItemStack(iskallia.vault.init.ModBlocks.TREASURE_SAND);
        });
        register("brazier_pillage", () -> {
            return new ItemStack(iskallia.vault.init.ModBlocks.MONOLITH);
        });
        register("haunted_brazier_pillage", () -> {
            return new ItemStack(iskallia.vault.init.ModBlocks.MONOLITH);
        });
    }

    @Shadow
    protected static void register(String path, Supplier<ItemStack> catalystItemStackSupplier) {
    }
}
