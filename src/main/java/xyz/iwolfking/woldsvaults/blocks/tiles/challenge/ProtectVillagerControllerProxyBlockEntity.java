package xyz.iwolfking.woldsvaults.blocks.tiles.challenge;

import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import iskallia.vault.core.world.storage.IZonedWorld;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.blocks.challenge.ProtectVillagerControllerProxyBlock;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.ProtectVillagerControllerTileEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProtectVillagerControllerProxyBlockEntity extends BlockEntity {

    private BlockPos controller;
    private List<ChallengeAction<?>> actions;
    private String label = "";

    public ProtectVillagerControllerProxyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.PROTECT_VILLAGER_CONTROLLER_PROXY_TILE_ENTITY, pos, state);
        this.actions = new ArrayList<>();
    }

    public void setController(BlockPos controller) {
        this.controller = controller;
        this.sendUpdates();
    }

    public void setActions(List<ChallengeAction<?>> actions) {
        this.actions = actions;
        this.sendUpdates();
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
        this.sendUpdates();
    }

    public static void tick(Level world, BlockPos pos, BlockState state, ProtectVillagerControllerProxyBlockEntity entity) {
        if(world.isClientSide) {
            return;
        }

        if(entity.controller == null) {
            entity.clearInvalidProxy(world, pos);
            return;
        }

        if(!world.hasChunkAt(entity.controller)) {
            return;
        }

        if(!(state.getBlock() instanceof ProtectVillagerControllerProxyBlock)) {
            if(world instanceof ServerLevel serverLevel && entity.tryRestoreProxy(serverLevel, pos, state)) {
                return;
            }

            entity.clearInvalidProxy(world, pos);
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(entity.controller);
        if(!(blockEntity instanceof ProtectVillagerControllerTileEntity) || blockEntity.isRemoved()) {
            entity.clearInvalidProxy(world, pos);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        Adapters.BLOCK_POS.writeNbt(this.controller).ifPresent(tag -> nbt.put("controller", tag));
        ListTag actions = new ListTag();

        for(ChallengeAction<?> action : this.actions) {
            Adapters.CHALLENGE_ACTION.writeNbt(action).ifPresent(actions::add);
        }

        nbt.put("actions", actions);
        nbt.putString("label", this.label);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.controller = Adapters.BLOCK_POS.readNbt(nbt.get("controller")).orElse(null);
        this.label = nbt.getString("label");
        ListTag actions = nbt.getList("actions", Tag.TAG_COMPOUND);
        this.actions.clear();

        for(Tag tag : actions) {
            Adapters.CHALLENGE_ACTION.readNbt(tag).ifPresent(action -> this.actions.add(action));
        }
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
        if(this.level == null) return;
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        this.setChanged();
    }

    private boolean tryRestoreProxy(ServerLevel world, BlockPos pos, BlockState currentState) {
        BlockState desiredState = ModBlocks.PROTECT_VILLAGER_CONTROLLER_PROXY.defaultBlockState();

        if(currentState.is(ModBlocks.PROTECT_VILLAGER_CONTROLLER_PROXY)) {
            return true;
        }

        List<ChallengeAction<?>> storedActions = new ArrayList<>(this.actions);
        BlockPos controllerPos = this.controller;
        final boolean[] restored = {false};

        IZonedWorld.runWithBypass(world, true, () -> {
            if(world.setBlock(pos, desiredState,  Block.UPDATE_ALL)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);

                if(blockEntity instanceof ProtectVillagerControllerProxyBlockEntity proxy) {
                    proxy.controller = controllerPos;
                    proxy.actions = new ArrayList<>(storedActions);
                    proxy.sendUpdates();
                }

                restored[0] = world.getBlockState(pos).is(ModBlocks.PROTECT_VILLAGER_CONTROLLER_PROXY);
            }
        });

        if(restored[0]) {
            VaultMod.LOGGER.debug("Restored protect villager proxy block at {} linked to controller {}", pos, controllerPos);
        }

        return restored[0];
    }

    private void clearInvalidProxy(Level world, BlockPos pos) {
        if(world instanceof ServerLevel serverLevel) {
            IZonedWorld.runWithBypass(serverLevel, true, () -> {
                if(world.getBlockState(pos).getBlock() instanceof ProtectVillagerControllerProxyBlock) {
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

