package xyz.iwolfking.woldsvaults.blocks.tiles.challenge;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleChallengeManager;
import iskallia.vault.core.world.storage.BlockCuboid;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleAnimation;

public class TempleControllerTileEntity extends ChallengeControllerBlockEntity<TempleChallengeManager> {

    @Getter
    private final TempleAnimation animation = new TempleAnimation();

    @Getter
    private BlockCuboid zone;
    @Getter @Setter
    private ChallengeAction<?> positivePool;
    @Getter @Setter
    private ChallengeAction<?> negativePool;
    @Getter
    private final List<BlockPos> proxyPositions = new ArrayList<>();
    @Getter
    private final List<BlockPos> spawnPositions = new ArrayList<>();

    public TempleControllerTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TEMPLE_CONTROLLER_TILE_ENTITY, pos, state);
        this.setRenderer(0x4ae268, 0x43d61e, 0x39ed4b);
        this.zone = null;
    }

    @Override
    public TempleChallengeManager createManager() {
        TempleChallengeManager manager = new TempleChallengeManager(
                this.uuid, this.level.dimension(), this.getBlockPos(),
                this.zone, this.positivePool, this.negativePool);
        manager.getProxyPositions().addAll(this.proxyPositions);
        manager.getSpawnPositions().addAll(this.spawnPositions);
        this.animation.getGates().forEach(manager.getAnimation()::addGate);
        return manager;
    }

    public void setZone(BlockCuboid zone) {
        this.zone = zone;
        this.sendUpdates();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.animation.writeNbt(nbt);
        Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
        Adapters.CHALLENGE_ACTION.writeNbt(this.positivePool).ifPresent(tag -> nbt.put("positivePool", tag));
        Adapters.CHALLENGE_ACTION.writeNbt(this.negativePool).ifPresent(tag -> nbt.put("negativePool", tag));

        ListTag proxyPosTag = new ListTag();
        this.proxyPositions.forEach(p -> Adapters.BLOCK_POS.writeNbt(p).ifPresent(proxyPosTag::add));
        nbt.put("proxyPositions", proxyPosTag);

        ListTag spawnPosTag = new ListTag();
        this.spawnPositions.forEach(p -> Adapters.BLOCK_POS.writeNbt(p).ifPresent(spawnPosTag::add));
        nbt.put("spawnPositions", spawnPosTag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.animation.readNbt(nbt);
        this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
        this.positivePool = Adapters.CHALLENGE_ACTION.readNbt(nbt.get("positivePool")).orElse(null);
        this.negativePool = Adapters.CHALLENGE_ACTION.readNbt(nbt.get("negativePool")).orElse(null);

        this.proxyPositions.clear();
        if (nbt.get("proxyPositions") instanceof ListTag list) {
            for (Tag tag : list) {
                Adapters.BLOCK_POS.readNbt(tag).ifPresent(this.proxyPositions::add);
            }
        }

        this.spawnPositions.clear();
        if (nbt.get("spawnPositions") instanceof ListTag list) {
            for (Tag tag : list) {
                Adapters.BLOCK_POS.readNbt(tag).ifPresent(this.spawnPositions::add);
            }
        }
    }
}
