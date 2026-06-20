package io.github.a1qs.vaultadditions.block;

import io.github.a1qs.vaultadditions.block.blockentity.StatueCauldronBlockEntity;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class StatueCauldronBlock extends Block implements EntityBlock  {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    private static final int DEFAULT_STATUE_REQUIREMENT = 100;
    private static final Map<String, Integer> HARDCODED_PLAYER_REQUIREMENTS;

    static {
        HARDCODED_PLAYER_REQUIREMENTS = new HashMap<>();
        HARDCODED_PLAYER_REQUIREMENTS.put("TigerShrimp88", 50);
        HARDCODED_PLAYER_REQUIREMENTS.put("Jrowez", 50);
    }
    public StatueCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 1));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.STATUE_CAULDRON_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if(itemstack.isEmpty()) return InteractionResult.PASS;
        if(itemstack.getItem() != ModItems.INFINITE_WATER_BUCKET && itemstack.getItem() != Items.WATER_BUCKET) return InteractionResult.PASS;
        int i = pState.getValue(LEVEL);
        Item item = itemstack.getItem();
        if (item instanceof BucketItem && ((BucketItem)item).getFluid() == Fluids.WATER) {
            if (i < 3 && !pLevel.isClientSide()) {
                if (!pPlayer.isCreative()) {
                    LazyOptional<IFluidHandlerItem> providerOptional = itemstack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
                    providerOptional.ifPresent((provider) -> {
                        provider.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    });
                }
                pPlayer.awardStat(Stats.FILL_CAULDRON);
                pLevel.setBlock(pPos, pState.setValue(LEVEL, 3), 3);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
                pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return InteractionResult.PASS;
    }


    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState toPlace = this.defaultBlockState();
        ItemStack stack = context.getItemInHand();
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", 10)) {
            int cauldronLevel = stack.getTagElement("BlockEntityTag").getInt("Level");
            if(cauldronLevel != 1 && cauldronLevel != 2 && cauldronLevel != 3) {
                cauldronLevel = 1;
            }
            return toPlace.setValue(LEVEL, cauldronLevel);
        } else {
            return toPlace;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @org.jetbrains.annotations.Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide() && pPlacer instanceof Player player) {

            BlockEntity te = pLevel.getBlockEntity(pPos);
            if (te instanceof StatueCauldronBlockEntity be) {
                if (pStack.getOrCreateTag().contains("BlockEntityTag")) {
                } else {
                    be.setOwner(player.getUUID());
                    String playerName = player.getDisplayName().getString();
                    int requirement = HARDCODED_PLAYER_REQUIREMENTS.getOrDefault(
                            playerName,
                            DEFAULT_STATUE_REQUIREMENT
                    );
                    be.setRequiredAmount(requirement);
                }
                be.sendUpdates();
            }
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if(!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            ItemStack itemStack = new ItemStack(this.asBlock());
            if(blockEntity instanceof StatueCauldronBlockEntity be) {
                CompoundTag statueTag = be.serializeNBT();
                CompoundTag stackTag = itemStack.getOrCreateTag();
                statueTag.putInt("Level", pState.getValue(LEVEL));
                stackTag.put("BlockEntityTag", statueTag);
                itemStack.setTag(stackTag);
            }
            ItemEntity item = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, itemStack);
            item.setDefaultPickUpDelay();
            pLevel.addFreshEntity(item);
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.STATUE_CAULDRON_BLOCK_ENTITY.get(), StatueCauldronBlockEntity::tick);
    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> aBlockEntityType,
            BlockEntityType<E> eBlockEntityType,
            BlockEntityTicker<? super E> blockEntityTicker) {
        return eBlockEntityType == aBlockEntityType ? (BlockEntityTicker<A>) blockEntityTicker : null;
    }

    @Override
    public void handlePrecipitation(BlockState pState, Level pLevel, BlockPos pPos, Biome.Precipitation pPrecipitation) {

    }
}