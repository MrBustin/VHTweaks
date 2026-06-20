package xyz.iwolfking.woldsvaults.items;

import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.item.BasicItem;
import iskallia.vault.world.data.PlayerVaultAltarData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.mixin.accessors.VaultAltarTileEntityAccessor;

import java.util.List;

public class AltarResetItem extends BasicItem {
    public AltarResetItem(ResourceLocation id, Properties properties) {
        super(id, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stackInHand = context.getItemInHand();
        if(level.isClientSide) {
            return InteractionResult.PASS;
        }
        BlockEntity clickedBLock = level.getBlockEntity(context.getClickedPos());
        if(clickedBLock instanceof VaultAltarTileEntity altar && stackInHand.getItem() instanceof AltarResetItem item) {
            if(altar.getAltarState().equals(VaultAltarTileEntity.AltarState.ACCEPTING)) {
                if(AltarResetItem.resetAltar((ServerPlayer) context.getPlayer())) {
                    stackInHand.shrink(1);
                    level.playSound(null, context.getClickedPos(), AMSoundRegistry.COSMAW_IDLE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    return InteractionResult.CONSUME;
                }
                else {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static boolean resetAltar(ServerPlayer player) {
        ServerLevel level = player.getLevel();
        PlayerVaultAltarData data = PlayerVaultAltarData.get(level);
        List<BlockPos> altars = data.getAltars(player.getUUID());
        altars.stream().filter((pos) -> {
           return level.isLoaded(pos);
        }).map((pos) -> {
            return player.getLevel().getBlockEntity(pos);
        }).filter((te) -> {
            return te instanceof VaultAltarTileEntity;
        }).map((te) -> {
            return (VaultAltarTileEntity)te;
        }).filter((altar) -> {
            return altar.getAltarState() == VaultAltarTileEntity.AltarState.ACCEPTING;
        }).forEach((altar) -> {
            // ((VaultAltarTileEntityInterface)altar).invokeResetAltar(level);
        });
        data.removeRecipe(player.getUUID());
        regenerateAltars(altars, player);
        return true;
    }

    private static void regenerateAltar(VaultAltarTileEntity altar, ServerPlayer player, AltarInfusionRecipe recipe) {
        PlayerVaultAltarData altarData = PlayerVaultAltarData.get(player.getLevel());
        if(altar.getOwner().equals(player.getUUID())) {
            if(altar.getAltarState().equals(VaultAltarTileEntity.AltarState.IDLE)) {
                altar.setRecipe(recipe);
                ((VaultAltarTileEntityAccessor)altar).invokeUpdateDisplayedIndex(recipe);
                altar.setAltarState(VaultAltarTileEntity.AltarState.ACCEPTING);
                altar.sendUpdates();
            }
        }
    }


    private static void regenerateAltars(List<BlockPos> altars, ServerPlayer player) {
        PlayerVaultAltarData altarData = PlayerVaultAltarData.get(player.getLevel());
        altars.forEach(blockPos -> {
            if(player.getLevel().isLoaded(blockPos) && player.getLevel().getBlockEntity(blockPos) instanceof VaultAltarTileEntity altar) {
                if(altar.getOwner().equals(player.getUUID())) {
                    if(altar.getAltarState().equals(VaultAltarTileEntity.AltarState.ACCEPTING)) {
                        ((VaultAltarTileEntityAccessor)altar).invokeResetAltar(player.getLevel());
                        AltarInfusionRecipe recipe = altarData.getRecipe(player, blockPos);
                        altar.setRecipe(recipe);
                        ((VaultAltarTileEntityAccessor)altar).invokeUpdateDisplayedIndex(recipe);
                        altar.setAltarState(VaultAltarTileEntity.AltarState.ACCEPTING);
                        altar.sendUpdates();
                    }
                    else if(altar.getAltarState().equals(VaultAltarTileEntity.AltarState.IDLE)) {
                        ((VaultAltarTileEntityAccessor)altar).invokeResetAltar(player.getLevel());
                    }
                }
            }
        });
    }


}
