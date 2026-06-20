package io.github.a1qs.vaultadditions.events;

import io.github.a1qs.vaultadditions.events.vault.ChestPowerCrystalGenerationEvent;
import io.github.a1qs.vaultadditions.events.vault.RaidWaveCompletedEvent;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.Event;

public class VaultCommonEvents {
    public static final ChestPowerCrystalGenerationEvent CHEST_POWER_CRYSTAL_GENERATION_EVENT = register(new ChestPowerCrystalGenerationEvent());
    public static final RaidWaveCompletedEvent RAID_WAVE_COMPLETED = register(new RaidWaveCompletedEvent());

    private static <T extends Event<?, ?>> T register(T event) {
        CommonEvents.REGISTRY.add(event);
        return event;
    }

}
