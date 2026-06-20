package io.github.a1qs.vaultadditions.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.a1qs.vaultadditions.init.vault.ModGearAttributes;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(value = VaultChestBlock.class, remap = false, priority = 100)
public class MixinVaultChestBlock extends ChestBlock {
    public MixinVaultChestBlock(Properties p_51490_, Supplier<BlockEntityType<? extends ChestBlockEntity>> p_51491_) {
        super(p_51490_, p_51491_);
    }

    @Redirect(method = "onDestroyedByPlayer", at = @At(value = "INVOKE", target = "Liskallia/vault/block/VaultChestBlock;hasStepBreaking(Liskallia/vault/block/entity/VaultChestTileEntity;)Z"))
    private boolean onDestroyedBreaching(VaultChestBlock instance, VaultChestTileEntity tileEntity, @Local(argsOnly = true) Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        return (!AttributeGearData.hasData(mainHandItem) || !VaultGearData.read(mainHandItem).hasAttribute(ModGearAttributes.BREACHING)) && instance.hasStepBreaking(tileEntity);
    }

    @Redirect(method = "playerDestroy", at = @At(value = "INVOKE", target = "Liskallia/vault/block/VaultChestBlock;hasStepBreaking(Liskallia/vault/block/entity/VaultChestTileEntity;)Z", remap = false), remap = true)
    private boolean playerDestroyBreaching(VaultChestBlock instance, VaultChestTileEntity tileEntity, @Local(argsOnly = true) Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        return (!AttributeGearData.hasData(mainHandItem) || !VaultGearData.read(mainHandItem).hasAttribute(ModGearAttributes.BREACHING)) && instance.hasStepBreaking(tileEntity);
    }

    @Override
    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        boolean hasBreach = GearDataCache.of(pPlayer.getMainHandItem()).hasAttribute(ModGearAttributes.BREACHING);
        if (!hasBreach) {
            return super.getDestroyProgress(pState, pPlayer, pLevel, pPos);
        }

        float f = Math.max(40.0F, pState.getDestroySpeed(pLevel, pPos));
        int i = ForgeHooks.isCorrectToolForDrops(pState, pPlayer) ? 30 : 100;
        return pPlayer.getDigSpeed(pState, pPos) / f / (float) i;
    }
}
