package io.github.a1qs.vaultadditions.block;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.block.blockentity.GlobeExpanderBlockEntity;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.data.PlayerAdditionalVaultStatData;
import io.github.a1qs.vaultadditions.data.PowerCrystalData;
import io.github.a1qs.vaultadditions.events.VaultAdditionsEvent;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import io.github.a1qs.vaultadditions.init.ModItems;
import io.github.a1qs.vaultadditions.item.PowerCrystal;
import io.github.a1qs.vaultadditions.util.TimeUtil;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GlobeExpanderBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(.1, .1, .1, .9, .9, .9);

    public GlobeExpanderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @javax.annotation.Nullable BlockGetter pLevel, List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
        pTooltip.add(new TextComponent("Upon clicking with a Power Crystal:").withStyle(ChatFormatting.YELLOW));
        pTooltip.add(new TextComponent("Expands the World Border by " + ServerConfigs.POWER_CRYSTAL_INCREASE.get() + " Blocks").withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide() || pHand != InteractionHand.MAIN_HAND || !(pPlayer instanceof ServerPlayer player)) {
            return InteractionResult.PASS;
        }

        // Handle non-crystal interactions
        if (!(pPlayer.getMainHandItem().getItem() instanceof PowerCrystal) && !pPlayer.isSecondaryUseActive()) {
            handleWorldBorderInfo(pPlayer);
            return InteractionResult.SUCCESS;
        }

        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        EventData data = EventData.get(srv);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        boolean isActiveBorderEvent = false;
        if(data.isEventActive()) isActiveBorderEvent = data.getActiveEvent().isCrystalSubmissionEvent() || data.getActiveEvent().getEventId().equals(VaultAdditionsEvent.BORDER_EXPANSION_ENABLED);

        // If it's past the configured date AND if there's NOT an active crystal submission event AND Config option returns true
        if((TimeUtil.pastDate() && !isActiveBorderEvent) && ServerConfigs.LIMIT_TIME_FOR_EXPANSION.get()) {
            pPlayer.displayClientMessage(new TextComponent("Nothing happened...").withStyle(ChatFormatting.RED), true);
            return InteractionResult.PASS;
        }

        List<ServerLevel> validDimensions = Stream.of(
                srv.getLevel(Level.OVERWORLD),
                srv.getLevel(Level.NETHER),
                srv.getLevel(Level.END)
        ).filter(Objects::nonNull).toList();

        if (blockEntity instanceof GlobeExpanderBlockEntity expanderEntity) {
            if(expanderEntity.shouldAnimate()) {
                return InteractionResult.PASS;
            }

            boolean consumedCrystals = false;
            if (pPlayer.isSecondaryUseActive()) {
                consumedCrystals = useAllFoundCrystals(player, isActiveBorderEvent, data, validDimensions, expanderEntity);
            }

            if(pPlayer.getMainHandItem().getItem() == ModItems.POWER_CRYSTAL.get() && !pPlayer.isSecondaryUseActive()) {
                int powerCrystalIncrease = ServerConfigs.POWER_CRYSTAL_INCREASE.get();
                int handCount = pPlayer.getMainHandItem().getCount();
                consumedCrystals = true;


                if(!isActiveBorderEvent || (data.getActiveEvent().getEventId().equals(VaultAdditionsEvent.BORDER_EXPANSION_ENABLED) && data.getActiveEvent().isModifierActive())) {
                    for (ServerLevel dimension : validDimensions) {
                        WorldBorder dimensionBorder = dimension.getWorldBorder();

                        double blocksExpanded = calculateDimensionSpecificExpansion(dimension, powerCrystalIncrease, handCount);
                        double newSize = dimensionBorder.getSize() + blocksExpanded;
                        if(newSize >= 5.9999968E7) {
                            VaultAdditions.LOGGER.error("Cannot increase border in {}, size would be {} but the max allowed value is {}", dimension.dimension(), newSize, 5.9999968E7);
                            pPlayer.displayClientMessage(new TextComponent("Please report this to a server admin, see logs for more information.").withStyle(ChatFormatting.RED), true);
                            return InteractionResult.FAIL;
                        }
                        dimensionBorder.lerpSizeBetween(dimensionBorder.getSize(), newSize, 1000);
                    }

                    expanderEntity.setBroadcastMessage(pPlayer.getDisplayName().getString(), powerCrystalIncrease * handCount);

                    if (!pPlayer.getAbilities().instabuild) {
                        pPlayer.getMainHandItem().setCount(0);
                    }

                    PlayerAdditionalVaultStatData.get(srv).addPowerPoints(player, handCount);
                    PowerCrystalData.getServer().addContribution(player, handCount);
                } else {
                    if(!data.getActiveEvent().isModifierActive()) {
                        int consumed = Math.min(handCount,  data.getActiveEvent().getRequiredCrystals() - data.getActiveEvent().getCrystalsSubmitted());
                        data.getActiveEvent().addCrystalsSubmitted(consumed);
                        if(data.getActiveEvent().isModifierActive()) {
                            srv.getPlayerList().broadcastMessage(data.getActiveEvent().getEventEnabledMessage(), ChatType.SYSTEM, Util.NIL_UUID);
                        }
                        PlayerAdditionalVaultStatData.get(srv).addPowerPoints(player, consumed);
                        PowerCrystalData.getServer().addContribution(player, consumed);

                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(consumed);
                        }
                    } else {
                        return InteractionResult.PASS;
                    }
                }
            }

            if(consumedCrystals) {
                Objects.requireNonNull(expanderEntity.getLevel()).playSound(null, pPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.75F, 0.9F);

                expanderEntity.resetSpinTime();
                expanderEntity.setChanged();
                pLevel.sendBlockUpdated(pPos, expanderEntity.getBlockState(), expanderEntity.getBlockState(), 3);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide && pBlockEntityType == ModBlockEntities.GLOBE_EXPANDER_ENTITY.get()) {
            return GlobeExpanderBlockEntity::clientTick;
        }
        return pLevel.isClientSide() ? GlobeExpanderBlockEntity::clientTick : GlobeExpanderBlockEntity::serverTick;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new GlobeExpanderBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    private double calculateDimensionSpecificExpansion(ServerLevel dimension, int baseIncrease, int itemCount) {
        if (dimension.dimension() == Level.OVERWORLD) {
            return itemCount * baseIncrease;
        } else if (dimension.dimension() == Level.NETHER) {
            return itemCount * baseIncrease * ServerConfigs.NETHER_BORDER_INCREASE.get();
        } else if (dimension.dimension() == Level.END) {
            return itemCount * baseIncrease * ServerConfigs.END_BORDER_INCREASE.get();
        }
        return itemCount * baseIncrease;
    }

    private void handleWorldBorderInfo(Player player) {
        double worldBorderSize = player.getLevel().getWorldBorder().getSize();
        player.displayClientMessage(new TextComponent("Current World Border size diameter: " + worldBorderSize), true);
    }

    private boolean useAllFoundCrystals(ServerPlayer pPlayer, boolean isActiveBorderEvent, EventData data, List<ServerLevel> validDimensions, GlobeExpanderBlockEntity expanderEntity) {
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        int powerCrystalCount = 0;
        int powerCrystalIncrease = ServerConfigs.POWER_CRYSTAL_INCREASE.get();

        // Discover all power crystals in deep inventories and add them to a list for future removal.
        List<InventoryUtil.ItemAccess> powerCrystalItems = new ArrayList<>();
        for (InventoryUtil.ItemAccess item : InventoryUtil.findAllItems(pPlayer)) {
            if(item.getStack().getItem() == ModItems.POWER_CRYSTAL.get()) {
                powerCrystalCount += item.getStack().getCount();
                powerCrystalItems.add(item);
            }
        }

        if(powerCrystalItems.isEmpty()) return false;


        if(!isActiveBorderEvent || (data.getActiveEvent().getEventId().equals(VaultAdditionsEvent.BORDER_EXPANSION_ENABLED) && data.getActiveEvent().isModifierActive())) {
            for (ServerLevel dimension : validDimensions) {
                WorldBorder dimensionBorder = dimension.getWorldBorder();

                double blocksExpanded = calculateDimensionSpecificExpansion(dimension, powerCrystalIncrease, powerCrystalCount);
                double newSize = dimensionBorder.getSize() + blocksExpanded;
                if(newSize >= 5.9999968E7) {
                    VaultAdditions.LOGGER.error("Cannot increase border in {}, size would be {} but the max allowed value is {}", dimension.dimension(), newSize, 5.9999968E7);
                    pPlayer.displayClientMessage(new TextComponent("Please report this to a server admin, see logs for more information.").withStyle(ChatFormatting.RED), true);
                    return false;
                }
                dimensionBorder.lerpSizeBetween(dimensionBorder.getSize(), newSize, 1000);
            }

            expanderEntity.setBroadcastMessage(pPlayer.getDisplayName().getString(), powerCrystalIncrease * powerCrystalCount);

            if (!pPlayer.getAbilities().instabuild) {
                for (InventoryUtil.ItemAccess item : powerCrystalItems) {
                    item.setStack(ItemStack.EMPTY);
                }
            }

            PlayerAdditionalVaultStatData.get(srv).addPowerPoints(pPlayer, powerCrystalCount);
            PowerCrystalData.get(srv).addContribution(pPlayer, powerCrystalCount);
        } else if(!data.getActiveEvent().isModifierActive()) {
            int crystalsNeeded = data.getActiveEvent().getRequiredCrystals() - data.getActiveEvent().getCrystalsSubmitted();
            int crystalsToConsume = Math.min(powerCrystalCount, crystalsNeeded);

            data.getActiveEvent().addCrystalsSubmitted(crystalsToConsume);


            if(data.getActiveEvent().isModifierActive()) {
                srv.getPlayerList().broadcastMessage(data.getActiveEvent().getEventEnabledMessage(), ChatType.SYSTEM, Util.NIL_UUID);
            }

            PlayerAdditionalVaultStatData.get(srv).addPowerPoints(pPlayer, crystalsToConsume);
            PowerCrystalData.get(srv).addContribution(pPlayer, crystalsToConsume);

            if (!pPlayer.getAbilities().instabuild) {
                int remainingToConsume = crystalsToConsume;
                for (InventoryUtil.ItemAccess item : powerCrystalItems) {
                    ItemStack stack = item.getStack();
                    int stackSize = stack.getCount();

                    if (stackSize <= remainingToConsume) {
                        item.setStack(ItemStack.EMPTY);
                        remainingToConsume -= stackSize;
                    } else {
                        stack.shrink(remainingToConsume);
                        item.setStack(stack);
                        break;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isCurrentlyInUse() {
        return !ServerConfigs.LIMIT_TIME_FOR_EXPANSION.get() || !TimeUtil.pastDate() || EventData.getServer().globeExpanderRequired();
    }
}
