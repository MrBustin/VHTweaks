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
