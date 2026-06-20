package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.item.VaultXPFoodItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Forces VaultXPFoodItem to clamp its levelLimit at construction.
 */
@Mixin(VaultXPFoodItem.class)
public class MixinVaultXPFoodItem {

    @Unique
    private static final int DEFAULT_LEVEL_CAP = 100;

    /**
     * Modify the "levelLimit" argument inside the constructor before it's assigned.
     */
    @ModifyVariable(
            method = "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/Item$Properties;I)V",
            at = @At("HEAD"),
            index = 3, // index 0=this, 1=id, 2=properties, 3=levelLimit
            argsOnly = true
    )
    private static int clampConstructorArg(int originalLevelLimit) {
        if (originalLevelLimit == -1 || originalLevelLimit > DEFAULT_LEVEL_CAP) {
            return DEFAULT_LEVEL_CAP;
        }
        return originalLevelLimit;
    }
}
