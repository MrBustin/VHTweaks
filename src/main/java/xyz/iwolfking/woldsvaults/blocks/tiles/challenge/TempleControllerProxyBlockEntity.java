package xyz.iwolfking.woldsvaults.blocks.tiles.challenge;

import iskallia.vault.VaultMod;
import xyz.iwolfking.woldsvaults.blocks.challenge.TempleControllerProxyBlock;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import iskallia.vault.core.world.storage.IZonedWorld;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

@Getter
public class TempleControllerProxyBlockEntity extends BlockEntity {

    private BlockPos controller;
    @Nullable
    private ChallengeAction<?> positiveAction;
    @Nullable
    private ChallengeAction<?> negativeAction;

    public TempleControllerProxyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TEMPLE_CONTROLLER_PROXY_TILE_ENTITY, pos, state);
    }

    public void setController(BlockPos controller) {
        this.controller = controller;
        this.sendUpdates();
    }

    public void setPositiveAction(@Nullable ChallengeAction<?> action) {
        this.positiveAction = action;
        this.sendUpdates();
    }

    public void setNegativeAction(@Nullable ChallengeAction<?> action) {
        this.negativeAction = action;
        this.sendUpdates();
    }

    public static void tick(Level world, BlockPos pos, BlockState state, TempleControllerProxyBlockEntity entity) {
        if (world.isClientSide) return;

        if (entity.controller == null) {
            entity.clearInvalidProxy(world, pos);
            return;
        }

        if (!world.hasChunkAt(entity.controller)) return;

        if (!(state.getBlock() instanceof TempleControllerProxyBlock)) {
            if (world instanceof ServerLevel serverLevel && entity.tryRestoreProxy(serverLevel, pos, state)) {
                return;
            }
            entity.clearInvalidProxy(world, pos);
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(entity.controller);
        if (!(blockEntity instanceof TempleControllerTileEntity) || blockEntity.isRemoved()) {
            entity.clearInvalidProxy(world, pos);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        Adapters.BLOCK_POS.writeNbt(this.controller).ifPresent(tag -> nbt.put("controller", tag));
        if (this.positiveAction != null) {
            Adapters.CHALLENGE_ACTION.writeNbt(this.positiveAction).ifPresent(tag -> nbt.put("positiveAction", tag));
        }
        if (this.negativeAction != null) {
            Adapters.CHALLENGE_ACTION.writeNbt(this.negativeAction).ifPresent(tag -> nbt.put("negativeAction", tag));
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.controller = Adapters.BLOCK_POS.readNbt(nbt.get("controller")).orElse(null);
        this.positiveAction = nbt.contains("positiveAction")
                ? Adapters.CHALLENGE_ACTION.readNbt(nbt.get("positiveAction")).orElse(null) : null;
        this.negativeAction = nbt.contains("negativeAction")
                ? Adapters.CHALLENGE_ACTION.readNbt(nbt.get("negativeAction")).orElse(null) : null;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void sendUpdates() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        this.setChanged();
    }

    private boolean tryRestoreProxy(ServerLevel world, BlockPos pos, BlockState currentState) {
        if (currentState.is(ModBlocks.TEMPLE_CONTROLLER_PROXY)) return true;

        ChallengeAction<?> storedPositive = this.positiveAction;
        ChallengeAction<?> storedNegative = this.negativeAction;
        BlockPos controllerPos = this.controller;
        final boolean[] restored = {false};

        IZonedWorld.runWithBypass(world, true, () -> {
            BlockState desiredState = ModBlocks.TEMPLE_CONTROLLER_PROXY.defaultBlockState();
            if (world.setBlock(pos, desiredState, Block.UPDATE_ALL)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TempleControllerProxyBlockEntity proxy) {
                    proxy.controller = controllerPos;
                    proxy.positiveAction = storedPositive;
                    proxy.negativeAction = storedNegative;
                    proxy.sendUpdates();
                }
                restored[0] = world.getBlockState(pos).is(ModBlocks.TEMPLE_CONTROLLER_PROXY);
            }
        });

        if (restored[0]) {
            VaultMod.LOGGER.debug("Restored temple proxy block at {} linked to controller {}", pos, controllerPos);
        }

        return restored[0];
    }

    private void clearInvalidProxy(Level world, BlockPos pos) {
        if (world instanceof ServerLevel serverLevel) {
            IZonedWorld.runWithBypass(serverLevel, true, () -> {
                if (world.getBlockState(pos).getBlock() instanceof TempleControllerProxyBlock) {
                    serverLevel.removeBlock(pos, false);
                }
                world.removeBlockEntity(pos);
            });
        } else {
            world.removeBlockEntity(pos);
        }
        this.setRemoved();
    }
}
