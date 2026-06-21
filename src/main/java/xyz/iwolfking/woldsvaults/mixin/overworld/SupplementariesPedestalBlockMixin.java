package xyz.iwolfking.woldsvaults.mixin.overworld;

import xyz.iwolfking.woldsvaults.util.overworld.OverworldVaultCrystalUtil;
import xyz.iwolfking.woldsvaults.world.overworld.ActiveCrystalSavedData;
import net.mehvahdjukaar.selene.blocks.ItemDisplayTile;
import net.mehvahdjukaar.supplementaries.common.block.blocks.PedestalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PedestalBlock.class)
public class SupplementariesPedestalBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void collectOverworldVaultCrystal(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ActiveCrystalSavedData activeCrystalData = ActiveCrystalSavedData.get(serverLevel);
        if (!activeCrystalData.hasActiveCrystal()
                || !activeCrystalData.getDimension().equals(level.dimension())
                || OverworldVaultCrystalUtil.findActiveCrystalPedestalPos(serverLevel, activeCrystalData.getPedestalPos())
                .filter(pos::equals)
                .isEmpty()) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ItemDisplayTile displayTile)) {
            return;
        }

        ItemStack crystalStack = displayTile.getDisplayedItem();
        if (!OverworldVaultCrystalUtil.isOverworldVaultCrystal(crystalStack)) {
            return;
        }

        String crystalTitle = activeCrystalData.getClaimCrystalTitle();
        OverworldVaultCrystalUtil.setCrystalLevelFromCollector(crystalStack, serverPlayer);
        displayTile.setDisplayedItem(ItemStack.EMPTY);

        if (state.hasProperty(PedestalBlock.HAS_ITEM)) {
            level.setBlock(pos, state.setValue(PedestalBlock.HAS_ITEM, false), 3);
        }

        activeCrystalData.clearActiveCrystal();
        OverworldVaultCrystalUtil.clearCompassInfo(serverLevel.getServer());
        OverworldVaultCrystalUtil.giveCrystalToCollector(serverPlayer, hand, crystalStack);
        OverworldVaultCrystalUtil.broadcastCrystalClaim(serverPlayer, crystalTitle);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
