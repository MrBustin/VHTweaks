package xyz.iwolfking.woldsvaults.mixin.accessors;

import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = VaultAltarTileEntity.class, remap = false)
public interface VaultAltarTileEntityAccessor {
    @Invoker("resetAltar")
    void invokeResetAltar(ServerLevel world) ;

    @Invoker("updateDisplayedIndex")
    void invokeUpdateDisplayedIndex(AltarInfusionRecipe recipe);

}
