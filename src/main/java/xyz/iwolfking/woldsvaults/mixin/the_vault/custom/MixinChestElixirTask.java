package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import org.spongepowered.asm.mixin.injection.Redirect;
import iskallia.vault.core.vault.objective.elixir.ChestElixirTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChestElixirTask.class, remap = false)
public abstract class MixinChestElixirTask {

    // Barrels extend VaultChestBlock but are explicitly skipped; make that check always false
    @Redirect(
            method = "lambda$initServer$0",
            at = @At(value = "CONSTANT", args = "classValue=iskallia/vault/block/VaultBarrelBlock")
    )
    private boolean woldsvaults$countBarrelsForElixir(Object block, Class<?> barrelClass) {
        return false;
    }
}
