package io.github.a1qs.vaultadditions.block;


import io.github.a1qs.vaultadditions.block.blockentity.LootStatueBlockEntity;
import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.container.LootStatueContainer;
import io.github.a1qs.vaultadditions.container.RenameContainer;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import io.github.a1qs.vaultadditions.util.StatueLootSanitizer;
import io.github.a1qs.vaultadditions.util.UsernameProvider;
import io.github.a1qs.vaultadditions.util.VoxelUtil;
import iskallia.vault.util.RenameType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class LootStatueBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape VAULT_SHAPE;
    private static final VoxelShape VAULT_SHAPE_90;
    private static final VoxelShape VAULT_SHAPE_180;
    private static final VoxelShape VAULT_SHAPE_270;
    private static final VoxelShape GIFT_SHAPE;
    private static final VoxelShape GIFT_SHAPE_90;
    private static final VoxelShape GIFT_SHAPE_180;
    private static final VoxelShape GIFT_SHAPE_270;
    private static final VoxelShape MEGA_GIFT_SHAPE;
    private static final VoxelShape MEGA_GIFT_SHAPE_90;
    private static final VoxelShape MEGA_GIFT_SHAPE_180;
    private static final VoxelShape MEGA_GIFT_SHAPE_270;
    private static final VoxelShape ARENA_SHAPE;
    private static final VoxelShape ARENA_SHAPE_90;
    private static final VoxelShape ARENA_SHAPE_180;
    private static final VoxelShape ARENA_SHAPE_270;

    public LootStatueBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.SOUTH));
    }


    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch (direction) {
            case SOUTH -> {
                switch(state.getBlock().getRegistryName().toString()) {
                    case "vaultadditions:loot_statue_vault" -> {
                        return VAULT_SHAPE;
                    }
                    case "vaultadditions:loot_statue_gift" -> {
                        return GIFT_SHAPE;
                    }
                    case "vaultadditions:loot_statue_gift_mega" -> {
                        return MEGA_GIFT_SHAPE;
                    }
                    case "vaultadditions:loot_statue_arena" -> {
                        return ARENA_SHAPE;
                    }
                }
            }
            case EAST -> {
                switch(state.getBlock().getRegistryName().toString()) {
                    case "vaultadditions:loot_statue_vault" -> {
                        return VAULT_SHAPE_270;
                    }
                    case "vaultadditions:loot_statue_gift" -> {
                        return GIFT_SHAPE_270;
                    }
                    case "vaultadditions:loot_statue_gift_mega" -> {
                        return MEGA_GIFT_SHAPE_270;
                    }
                    case "vaultadditions:loot_statue_arena" -> {
                        return ARENA_SHAPE_270;
                    }
                }
            }
            case NORTH -> {
                switch(state.getBlock().getRegistryName().toString()) {
                    case "vaultadditions:loot_statue_vault" -> {
                        return VAULT_SHAPE_180;
                    }
                    case "vaultadditions:loot_statue_gift" -> {
                        return GIFT_SHAPE_180;
                    }
                    case "vaultadditions:loot_statue_gift_mega" -> {
                        return MEGA_GIFT_SHAPE_180;
                    }
                    case "vaultadditions:loot_statue_arena" -> {
                        return ARENA_SHAPE_180;
                    }
                }

            }
            default -> {
                switch(state.getBlock().getRegistryName().toString()) {
                    case "vaultadditions:loot_statue_vault" -> {
                        return VAULT_SHAPE_90;
                    }
                    case "vaultadditions:loot_statue_gift" -> {
                        return GIFT_SHAPE_90;
                    }
                    case "vaultadditions:loot_statue_gift_mega" -> {
                        return MEGA_GIFT_SHAPE_90;
                    }
                    case "vaultadditions:loot_statue_arena" -> {
                        return ARENA_SHAPE_90;
                    }
                }
            }
        }
        return Shapes.block(); // Fallback
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level pLevel, BlockPos pos, BlockState pState, LivingEntity pPlacer, ItemStack stack) {
        if (pPlacer instanceof ServerPlayer player) {
            BlockEntity var9 = pLevel.getBlockEntity(pos);
            if (var9 instanceof LootStatueBlockEntity be) {

                if (!stack.getOrCreateTag().getCompound("BlockEntityTag").contains("LootItem")) {
                    final CompoundTag data = new CompoundTag();
                    ListTag itemList = new ListTag();
                    List<ItemStack> options;
                    switch (pState.getBlock().getRegistryName().toString()) {
                        case "vaultadditions:loot_statue_gift" ->
                                options = Configs.STATUE_LOOT_GIFT.getOptions();
                        case "vaultadditions:loot_statue_gift_mega" ->
                                options = Configs.STATUE_LOOT_MEGA_GIFT.getOptions();
                        case "vaultadditions:loot_statue_arena" ->
                                options = Configs.STATUE_LOOT_ARENA.getOptions();
                        default -> options = Configs.STATUE_LOOT_VAULT.getOptions();
                    }

                    for (ItemStack option : options) {
                        itemList.add(StatueLootSanitizer.sanitizeCustomStatueLoot(pState, option).serializeNBT());
                    }

                    data.put("Items", itemList);
                    data.put("Position", NbtUtils.writeBlockPos(pos));
                    if (!stack.getTag().getCompound("BlockEntityTag").contains("PlayerNickname"))
                        be.getSkin().updateSkin(UsernameProvider.getRandomUsername());
                    NetworkHooks.openGui(player, new MenuProvider() {
                        public @NotNull Component getDisplayName() {
                            return new TextComponent("Loot Statue Options");
                        }

                        public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                            return new LootStatueContainer(windowId, data);
                        }
                    }, (buffer) -> {
                        buffer.writeNbt(data);
                    });
                }
                if (stack.getOrCreateTag().getCompound("BlockEntityTag").contains("LootItem") && be.getLootItem().getTag() != null) {
                    if (be.getLootItem().getTag().contains("Charged")) {
                        be.getLootItem().getTag().remove("Charged");
                        if(be.getLootItem().getTag().isEmpty()) {
                            be.getLootItem().setTag(null);
                        }
                    }
                }
                if (be.getLootItem().is(Items.AIR)) {
                    List<ItemStack> options;
                    switch (pState.getBlock().getRegistryName().toString()) {
                        case "vaultadditions:loot_statue_gift" ->
                                options = Configs.STATUE_LOOT_GIFT.getOptions();
                        case "vaultadditions:loot_statue_gift_mega" ->
                                options = Configs.STATUE_LOOT_MEGA_GIFT.getOptions();
                        case "vaultadditions:loot_statue_arena" ->
                                options = Configs.STATUE_LOOT_ARENA.getOptions();
                        default -> options = Configs.STATUE_LOOT_VAULT.getOptions();
                    }
                    be.setLootItem(StatueLootSanitizer.sanitizeCustomStatueLoot(pState, options.get(pLevel.random.nextInt(options.size()))));
                }
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        world.getBlockEntity(pos);
        BlockEntity var9 = world.getBlockEntity(pos);
        if (var9 instanceof LootStatueBlockEntity statue) {
            ItemStack heldItem = player.getMainHandItem();
            if (player.isSecondaryUseActive()) {
                if (heldItem.isEmpty()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        final CompoundTag nbt = new CompoundTag();
                        nbt.putInt("RenameType", RenameType.PLAYER_STATUE.ordinal());
                        CompoundTag data = statue.saveWithoutMetadata();
                        data.put("Pos", NbtUtils.writeBlockPos(pos));
                        nbt.put("Data", data);
                        NetworkHooks.openGui(serverPlayer, new MenuProvider() {
                            public Component getDisplayName() {
                                return new TextComponent("Rename Player Statue");
                            }

                            public @NotNull AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                                return new RenameContainer(windowId, nbt);
                            }
                        }, (buffer) -> {
                            buffer.writeNbt(nbt);
                        });
                    }

                    return InteractionResult.sidedSuccess(world.isClientSide);
                }
            }
        }

        return super.use(state, world, pos, player, handIn, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            ItemStack itemStack = new ItemStack(this);
            if (tileEntity instanceof LootStatueBlockEntity tile) {
                CompoundTag stackNBT = new CompoundTag();
                stackNBT.put("BlockEntityTag", tile.saveWithoutMetadata());
                itemStack.setTag(stackNBT);
            }

            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack itemstack = super.getCloneItemStack(state, target, world, pos, player);
        BlockEntity var8 = world.getBlockEntity(pos);
        if (var8 instanceof LootStatueBlockEntity tile) {
            CompoundTag stackNBT = new CompoundTag();
            stackNBT.put("BlockEntityTag", tile.saveWithoutMetadata());
            if (!stackNBT.isEmpty()) {
                itemstack.setTag(stackNBT);
            }
        }

        return itemstack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        return pos.getY() < world.getMaxBuildHeight() - 3 && world.getBlockState(pos.above(1)).canBeReplaced(context) && world.getBlockState(pos.above(2)).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()) : null;
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return List.of();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.LOOT_STATUE_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.LOOT_STATUE_BLOCK_ENTITY.get(), LootStatueBlockEntity::tick);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    static {
        VoxelShape[] vaultShape = {
                Shapes.box(0.390625, 0.234375, 0.8125, 0.453125, 0.546875, 0.875),
                Shapes.box(0.640625, 0.234375, 0.8125, 0.703125, 0.546875, 0.875),
                Shapes.box(0.453125, 0.546875, 0.8125, 0.640625, 0.609375, 0.875),
                Shapes.box(0.453125, 0.171875, 0.8125, 0.640625, 0.234375, 0.875),
                Shapes.box(0.453125, 0.234375, 0.84375, 0.640625, 0.546875, 0.84375),
                Shapes.box(0.375, 0.1875, 0.59375, 0.625, 0.875, 0.71875),
                Shapes.box(0.375, 0.875, 0.53125, 0.625, 1.125, 0.78125),
                Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.1875, 0.9375),
        };
        
        VoxelShape[] giftShape = {
                Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375),
                Shapes.box(0.25, 0.0625, 0.25, 0.75, 0.1875, 0.75),
                Shapes.box(0.109375, 0.0625, 0.109375, 0.328125, 0.28125, 0.328125),
                Shapes.box(0.3125, 0.0625, 0.09375, 0.484375, 0.203125, 0.265625),
                Shapes.box(0.71875, 0.0625, 0.078125, 0.921875, 0.234375, 0.28125),
                Shapes.box(0.703125, 0.0625, 0.375, 0.9375, 0.328125, 0.609375),
                Shapes.box(0.0625, 0.0625, 0.359375, 0.265625, 0.203125, 0.5625),
                Shapes.box(0.109375, 0.0625, 0.59375, 0.34375, 0.34375, 0.828125),
                Shapes.box(0.578125, 0.0625, 0.109375, 0.71875, 0.1875, 0.25),
                Shapes.box(0.6875, 0.0625, 0.59375, 0.921875, 0.25, 0.828125),
                Shapes.box(0.390625, 0.0625, 0.671875, 0.65625, 0.296875, 0.9375),
                Shapes.box(0.21875, 0.046875, 0.765625, 0.390625, 0.203125, 0.921875),
                Shapes.box(0.640625, 0.0625, 0.8125, 0.78125, 0.171875, 0.9375),
                Shapes.box(0.375, 0.1875, 0.4375, 0.625, 0.546875, 0.5625),
                Shapes.box(0.375, 0.90625, 0.375, 0.625, 1.15625, 0.625),
                Shapes.box(0.375, 0.546875, 0.4375, 0.625, 0.90625, 0.5625),
        };
        
        VoxelShape[] megaGiftShape = {
                Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.4375, 0.8125),
                Shapes.box(0.15625, 0.4375, 0.15625, 0.84375, 0.5625, 0.84375),
                Shapes.box(0.375, 1, 0.515625, 0.625, 1.25, 0.765625),
                Shapes.box(0.375, 0.640625, 0.578125, 0.625, 1, 0.703125),
        };

        VoxelShape[] arenaShape = {
                Shapes.box(0.296875, 0, 0.15625, 0.703125, 0.015625, 0.859375),
                Shapes.box(0.125, 0, 0.21875, 0.296875, 0.015625, 0.78125),
                Shapes.box(0.703125, 0, 0.21875, 0.875, 0.015625, 0.78125),
                Shapes.box(0.375, 0.018750000000000003, 0.59375, 0.625, 0.37812500000000004, 0.71875),
                Shapes.box(0.375, 0.7375, 0.53125, 0.625, 0.9875, 0.78125),
                Shapes.box(0.375, 0.37812500000000004, 0.59375, 0.625, 0.7375, 0.71875),
                Shapes.box(0.03125, 0, 0.03125, 0.96875, 0.1875, 0.96875),
        };

        VAULT_SHAPE = VoxelUtil.mergeVoxelShapes(vaultShape);
        VAULT_SHAPE_90 = VoxelUtil.rotateShape(VAULT_SHAPE, 90);
        VAULT_SHAPE_180 = VoxelUtil.rotateShape(VAULT_SHAPE, 180);
        VAULT_SHAPE_270 = VoxelUtil.rotateShape(VAULT_SHAPE, 270);

        GIFT_SHAPE = VoxelUtil.mergeVoxelShapes(giftShape);
        GIFT_SHAPE_90 = VoxelUtil.rotateShape(GIFT_SHAPE, 90);
        GIFT_SHAPE_180 = VoxelUtil.rotateShape(GIFT_SHAPE, 180);
        GIFT_SHAPE_270 = VoxelUtil.rotateShape(GIFT_SHAPE, 270);

        MEGA_GIFT_SHAPE = VoxelUtil.mergeVoxelShapes(megaGiftShape);
        MEGA_GIFT_SHAPE_90 = VoxelUtil.rotateShape(MEGA_GIFT_SHAPE, 90);
        MEGA_GIFT_SHAPE_180 = VoxelUtil.rotateShape(MEGA_GIFT_SHAPE, 180);
        MEGA_GIFT_SHAPE_270 = VoxelUtil.rotateShape(MEGA_GIFT_SHAPE, 270);

        ARENA_SHAPE = VoxelUtil.mergeVoxelShapes(arenaShape);
        ARENA_SHAPE_90 = VoxelUtil.rotateShape(ARENA_SHAPE, 90);
        ARENA_SHAPE_180 = VoxelUtil.rotateShape(ARENA_SHAPE, 180);
        ARENA_SHAPE_270 = VoxelUtil.rotateShape(ARENA_SHAPE, 270);
    }
}
