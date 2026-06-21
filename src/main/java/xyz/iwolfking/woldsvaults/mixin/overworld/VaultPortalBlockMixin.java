package xyz.iwolfking.woldsvaults.mixin.overworld;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.overworld.VaultConfigRegistry;
import xyz.iwolfking.woldsvaults.util.overworld.OverworldVaultCrystalUtil;
import xyz.iwolfking.woldsvaults.world.overworld.PortalData;
import xyz.iwolfking.woldsvaults.world.overworld.PortalSavedData;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultPortalBlock.class)
public class VaultPortalBlockMixin {
    @Unique private static final ResourceLocation BEGINNERS_INSURANCE_ID = new ResourceLocation("the_vault", "beginners_insurance");
    @Unique private static final ResourceLocation BEGINNERS_GRACE_ID = new ResourceLocation("the_vault", "beginners_grace");

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Liskallia/vault/block/entity/VaultPortalTileEntity;getData()Ljava/util/Optional;"))
    public void setPortalToPlayerLevel(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel) {
            PortalSavedData savedData = PortalSavedData.get(serverLevel);
            if (savedData.hasActiveOverworldVault()) {
                PortalData data = savedData.getFirstActivePortalData();
                BlockPos portalFrameCenter = data.getPortalFrameCenterPos();
                if (isPortalBlock(pos, portalFrameCenter, data)) {
                    BlockEntity te = level.getBlockEntity(pos);
                    VaultPortalTileEntity portal = te instanceof VaultPortalTileEntity ? (VaultPortalTileEntity)te : null;
                    if(portal != null && portal.getData().isPresent()) {
                        CrystalData crystalData = portal.getData().get();
                        int playerVaultLevel = 0;
                        if (entity instanceof ServerPlayer player) {
                            playerVaultLevel = OverworldVaultCrystalUtil.capOverworldVaultLevel(PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player).getVaultLevel());
                            if (playerVaultLevel <= 20) {
                                addBeginnerModifierIfMissing(crystalData, BEGINNERS_INSURANCE_ID);
                                addBeginnerModifierIfMissing(crystalData, BEGINNERS_GRACE_ID);
                            }
                        }

                        if(crystalData.getProperties().getLevel().isEmpty()) {
                            int vaultLevel;
                            if (entity instanceof Player player && VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.SET_LEVEL_OF_ENTERING_PLAYER_OVERWORLD_VAULT) {
                                vaultLevel = playerVaultLevel;
                            } else {
                                vaultLevel = 0;

                            }
                            crystalData.getProperties().setLevel(vaultLevel);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private void addBeginnerModifierIfMissing(CrystalData crystalData, ResourceLocation modifierId) {
        if (crystalData.getModifiers().getList().stream().anyMatch(modifier -> modifierId.equals(modifier.getModifierId()))) {
            return;
        }

        VaultModifierRegistry.getOpt(modifierId).ifPresentOrElse(
                modifier -> crystalData.getModifiers().getList().add(VaultModifierStack.of(modifier, 1)),
                () -> WoldsVaults.LOGGER.warn(
                        "Failed to add beginner OverworldVault modifier '{}' because it is not registered.",
                        modifierId
                )
        );
    }

    @Unique
    // Utility method to check if the BlockPos is part of a valid Portal object
    private boolean isPortalBlock(BlockPos pos, BlockPos portalCenter, PortalData data) {
        Iterable<BlockPos> portalBlocks = data.getSize().getBlockPositions(portalCenter, data.getRotation());
        for (BlockPos blockPos : portalBlocks) {
            if (pos.equals(blockPos)) {
                return true;
            }
        }
        return false;
    }
}
