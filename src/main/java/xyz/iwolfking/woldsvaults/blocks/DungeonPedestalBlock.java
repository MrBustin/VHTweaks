package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.init.ModSounds;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xyz.iwolfking.woldsvaults.blocks.tiles.DungeonPedestalTileEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;


public class DungeonPedestalBlock extends Block implements EntityBlock, GameMasterBlock {
    public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public DungeonPedestalBlock() {
        super(Properties.of(Material.METAL, MaterialColor.GOLD).noOcclusion().strength(3600000.0F, 3600000.0F));
    }


    @javax.annotation.Nullable
    public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level world, BlockState state, BlockEntityType<A> type) {
        return BlockHelper.getTicker(type, ModBlocks.DUNGEON_PEDESTAL_TILE_ENTITY_BLOCK_ENTITY_TYPE, DungeonPedestalTileEntity::tick);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            this.breakPedestal(state, level, pos);
        }

    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            this.breakPedestal(state, level, pos);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private void breakPedestal(BlockState state, Level level, BlockPos pos) {
        if (level instanceof ServerLevel sWorld) {
            BlockEntity var5 = sWorld.getBlockEntity(pos);
            if (var5 instanceof DungeonPedestalTileEntity pedestal) {
                if (!pedestal.getContained().isEmpty()) {
                    popResource(sWorld, pos, pedestal.getContained());
                    pedestal.setContained(ItemStack.EMPTY);
                }

                Vec3 vec3 = new Vec3((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F));
                sWorld.playSound((Player)null, vec3.x, vec3.y, vec3.z, ModSounds.CRATE_OPEN, SoundSource.PLAYERS, 1.0F, 1.0F);
                sWorld.removeBlock(pos, false);
                BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
                sWorld.sendParticles(particle, vec3.x, vec3.y, vec3.z, 300, 1.0, 1.0, 1.0, 0.5);
            }
        }
    }

    @javax.annotation.Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlocks.DUNGEON_PEDESTAL_TILE_ENTITY_BLOCK_ENTITY_TYPE.create(pPos, pState);
    }
}
