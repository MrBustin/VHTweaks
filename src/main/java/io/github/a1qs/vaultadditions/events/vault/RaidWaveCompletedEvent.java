package io.github.a1qs.vaultadditions.events.vault;

import iskallia.vault.core.event.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class RaidWaveCompletedEvent extends Event<RaidWaveCompletedEvent, RaidWaveCompletedEvent.Data> {
    public RaidWaveCompletedEvent() {}

    protected RaidWaveCompletedEvent(RaidWaveCompletedEvent parent) {
        super(parent);
    }

    @Override
    public RaidWaveCompletedEvent createChild() {
        return new RaidWaveCompletedEvent(this);
    }

    public RaidWaveCompletedEvent.Data invoke(ServerLevel world, ServerPlayer player) {
        return this.invoke(new RaidWaveCompletedEvent.Data(world, player));
    }

    public record Data(ServerLevel world, ServerPlayer player) {}
}
