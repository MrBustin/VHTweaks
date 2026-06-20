package xyz.iwolfking.woldsvaults.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import iskallia.vault.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import xyz.iwolfking.woldsvaults.items.gear.*;

@Mixin(value = ItemStack.class, priority = 1501)
public abstract class MixinItemStack extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack>
        implements net.minecraftforge.common.extensions.IForgeItemStack  {
    protected MixinItemStack(Class<ItemStack> baseClass) {
        super(baseClass);
    }



    @WrapOperation(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"),
            slice = @Slice(from = @At(value = "INVOKE", target = "iskallia/vault/util/calc/DurabilityWearReductionHelper.getDurabilityWearReduction (Lnet/minecraft/world/entity/LivingEntity;)F", remap = false))
    )
    public Item gearDurabilityPrestigeForWoldItems(ItemStack instance, Operation<Item> original) {
        Item item = original.call(instance);

        // Treat as weapons (using VaultSwordItem as reference)
        if (item instanceof VaultBattleStaffItem
                || item instanceof VaultTridentItem
                || item instanceof VaultRangItem) {
            return ModItems.SWORD;
        }

        // Treat as focus items
        if (item instanceof VaultLootSackItem
                || item instanceof VaultPlushieItem) {
            return ModItems.FOCUS;
        }

        return item;
    }
}
