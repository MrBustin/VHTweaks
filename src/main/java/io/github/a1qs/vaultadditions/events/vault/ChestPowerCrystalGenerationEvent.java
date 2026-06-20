package io.github.a1qs.vaultadditions.events.vault;

import iskallia.vault.core.event.Event;
import net.minecraft.server.level.ServerPlayer;

public class ChestPowerCrystalGenerationEvent extends Event<ChestPowerCrystalGenerationEvent, ChestPowerCrystalGenerationEvent.Data> {
    public ChestPowerCrystalGenerationEvent() {}

    protected ChestPowerCrystalGenerationEvent(ChestPowerCrystalGenerationEvent parent) {
        super(parent);
    }

    @Override
    public ChestPowerCrystalGenerationEvent createChild() {
        return new ChestPowerCrystalGenerationEvent(this);
    }

    public ChestPowerCrystalGenerationEvent.Data invoke(ServerPlayer player, double probability) {
        return this.invoke(new ChestPowerCrystalGenerationEvent.Data(player, probability));
    }

    public static class Data {
        private final ServerPlayer player;
        private double probability;

        public Data(ServerPlayer player, double probability) {
            this.player = player;
            this.probability = probability;
        }

        public ServerPlayer getPlayer() {
            return this.player;
        }

        public double getProbability() {
            return this.probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }
    }
}
