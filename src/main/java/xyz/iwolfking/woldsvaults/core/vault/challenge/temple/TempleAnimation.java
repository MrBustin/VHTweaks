package xyz.iwolfking.woldsvaults.core.vault.challenge.temple;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.challenge.base.animation.AbstractChallengeAnimation;
import iskallia.vault.core.vault.challenge.base.animation.AnimationState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.processor.tile.WeightedTileProcessor;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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

        if (this.state == State.CLOSE_ROOM) {
            Template template = null;

            if (this.ticker % 10 == 0) {
                this.gates.forEach((offset, rotation) ->
                        world.playSound(null, pos.offset(offset), SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 0.8F));
                int stage = Math.min(4, this.ticker / 10);
                template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/memory/gate" + stage)).get(Version.latest());
            }

            if (template != null) {
                placeGates(world, pos, template);
            }
        } else if (this.state == State.OPEN_ROOM) {
            Template template = null;

            if (this.ticker % 10 == 0) {
                this.gates.forEach((offset, rotation) ->
                        world.playSound(null, pos.offset(offset), SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.2F));
                int stage = Math.min(5, this.ticker / 10);
                template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/memory/open/gate" + stage)).get(Version.latest());
            }

            if (template != null) {
                placeGates(world, pos, template);
            }
        }

        this.ticker++;
    }

    private void placeGates(ServerLevel world, BlockPos pos, Template template) {
        for (Map.Entry<BlockPos, Rotation> entry : this.gates.entrySet()) {
            BlockPos offset = entry.getKey();
            Rotation rotation = entry.getValue();

            this.placeWithSandstone(world, template, pos, settings -> {
                settings.addProcessor(TileProcessor.translate(0, 0, -1));
                settings.addProcessor(TileProcessor.rotate(rotation, BlockPos.ZERO, true));
                settings.addProcessor(TileProcessor.translate(pos));
                settings.addProcessor(TileProcessor.translate(offset));
            });
        }
    }

    private void placeWithSandstone(ServerLevel world, Template template, BlockPos pos,
                                    Consumer<PlacementSettings> configurator) {
        this.place(world, template, pos, settings -> {
            settings.addProcessor(new WeightedTileProcessor()
                    .target("minecraft:red_concrete")
                    .into(PartialTile.of(Blocks.SANDSTONE.defaultBlockState()), 1));
            configurator.accept(settings);
        });
    }

    public enum State implements AnimationState {
        IDLE(0), CLOSE_ROOM(50), OPEN_ROOM(60);

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
