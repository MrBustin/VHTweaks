package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.block.VaultCrateBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

@Mixin(value = VaultCrateBlock.class, remap = false)
public abstract class MixinVaultCrateBlock {
    @Inject(method = "getCrateBlock", at = @At("HEAD"), cancellable = true)
    private static void handleWoldCrate(VaultCrateBlock.Type type, CallbackInfoReturnable<Block> cir) {
        if ("ALCHEMY".equals(type.name())) {
            cir.setReturnValue(ModBlocks.VAULT_CRATE_ALCHEMY);
        } else if ("BRUTAL_BOSSES".equals(type.name())) {
            cir.setReturnValue(ModBlocks.VAULT_CRATE_BRUTAL_BOSSES);
        } else if ("HAUNTED_BRAZIERS".equals(type.name())) {
            cir.setReturnValue(ModBlocks.VAULT_CRATE_HAUNTED_BRAZIERS);
        } else if ("BALLISTIC_BINGO".equals(type.name())) {
            cir.setReturnValue(ModBlocks.VAULT_CRATE_BALLISTIC_BINGO);
        } else if ("UNHINGED_SCAVENGER".equals(type.name())) {
            cir.setReturnValue(ModBlocks.VAULT_CRATE_UNHINGED_SCAVENGER);
        }
    }
}
