package xyz.iwolfking.woldsvaults.core.vault.challenge.temple;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.challenge.base.animation.AbstractChallengeAnimation;
import iskallia.vault.core.vault.challenge.base.animation.AnimationState;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import iskallia.vault.core.data.adapter.Adapters;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Rotation;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TempleAnimation extends AbstractChallengeAnimation<TempleAnimation.State> {

    @Getter
    private final Map<BlockPos, Rotation> gates = new HashMap<>();

    public TempleAnimation(){
        this.state = State.IDLE;
    }

    public void addGate(BlockPos offset, Rotation rotation) {
        this.gates.put(offset, rotation);
    }

    public void writeNbt(CompoundTag tag) {
        ListTag gates = new ListTag();
        this.gates.forEach((pos, rotation) -> {
            CompoundTag gate = new CompoundTag();
            Adapters.BLOCK_POS.writeNbt(pos).ifPresent(value -> gate.put("pos", value));
            gate.putInt("rotation", rotation.ordinal());
            gates.add(gate);
        });
        tag.put("gates", gates);
        tag.putString("gateAnimationState", this.state.name());
        tag.putInt("gateAnimationTick", this.ticker);
    }

    public void readNbt(CompoundTag tag) {
        this.gates.clear();
        for (Tag entry : tag.getList("gates", Tag.TAG_COMPOUND)) {
            CompoundTag gate = (CompoundTag) entry;
            Rotation rotation = Rotation.values()[Math.floorMod(gate.getInt("rotation"), Rotation.values().length)];
            Adapters.BLOCK_POS.readNbt(gate.get("pos")).ifPresent(pos -> this.addGate(pos, rotation));
        }
        try {
            this.state = State.valueOf(tag.getString("gateAnimationState"));
        } catch (IllegalArgumentException ignored) {
            this.state = State.IDLE;
        }
        this.ticker = Math.max(0, tag.getInt("gateAnimationTick"));
    }

    @Override
    protected State getIdleState() {
        return State.IDLE;
    }

    @Override
    public void onTick(ServerLevel world, BlockPos pos) {
        if (this.gates.isEmpty() || this.state == State.IDLE || this.isCompleted()) {
            this.ticker++;
            return;
        }

        Template template = null;
        if (this.state == State.CLOSE_ROOM) {
            if (this.ticker == 0) {
                template = getTemplate("templedoor1");
            } else if (this.ticker == 20) {
                playAtGates(world, pos, ModSounds.RAID_CHAIN_LOCK, 1.0F);
                template = getTemplate("templedoor2");
            }
        } else if (this.state == State.OPEN_ROOM) {
            if (this.ticker == 0) {
                template = getTemplate("templedoor2");
            } else if (this.ticker == 20) {
                template = getTemplate("templedoor1");
            } else if (this.ticker == 30) {
                template = getTemplate("templedoor0");
                playAtGates(world, pos, ModSounds.RAID_CHAIN_LOCK, 1.0F);
            }
        }

        if (template != null) {
            placeGates(world, pos, template);
        }
        this.ticker++;
    }

    private Template getTemplate(String name) {
        return VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/temple/" + name)).get(Version.latest());
    }

    private void playAtGates(ServerLevel world, BlockPos origin, net.minecraft.sounds.SoundEvent sound, float pitch) {
        this.gates.keySet().forEach(offset ->
                world.playSound(null, origin.offset(offset), sound, SoundSource.BLOCKS, 0.8F, pitch));
    }

    private void placeGates(ServerLevel world, BlockPos origin, Template template) {
        for (Map.Entry<BlockPos, Rotation> entry : this.gates.entrySet()) {
            BlockPos offset = entry.getKey();
            Rotation rotation = entry.getValue();
            this.place(world, template, origin, settings -> {
                settings.addProcessor(TileProcessor.translate(0, 0, -1));
                settings.addProcessor(TileProcessor.rotate(rotation, BlockPos.ZERO, true));
                settings.addProcessor(TileProcessor.translate(origin));
                settings.addProcessor(TileProcessor.translate(offset));
            });
        }
    }

    public enum State implements AnimationState {
        IDLE(0), CLOSE_ROOM(35), OPEN_ROOM(35);

        private final int time;

        State(int time) {
            this.time = time;
        }

        @Override
        public int getTime() {
            return this.time;
        }
    }
}
