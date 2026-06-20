package io.github.a1qs.vaultadditions.vault.core.time.modifier;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.vault.time.modifier.ClockModifier;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class PowerCrystalExtension extends ClockModifier {
    public static final SupplierKey<ClockModifier> KEY = SupplierKey.of("power_crystal", ClockModifier.class).with(Version.v1_53, PowerCrystalExtension::new);
    public static final FieldRegistry FIELDS = ClockModifier.FIELDS.merge(new FieldRegistry());
    public static final FieldKey<UUID> VAULT_ID = FieldKey.of("vault_id", UUID.class).with(Version.v1_0, Adapters.UUID, DISK.all()).register(FIELDS);
    public static final FieldKey<Integer> INCREMENT = FieldKey.of("increment", Integer.class).with(Version.v1_0, Adapters.INT_SEGMENTED_7, DISK.all()).register(FIELDS);

    public PowerCrystalExtension(UUID vaultId, int increment) {
        this.set(VAULT_ID, vaultId);
        this.set(INCREMENT, increment);
    }

    protected PowerCrystalExtension() {
    }

    @Override
    public SupplierKey<ClockModifier> getKey() {
        return KEY;
    }

    @Override
    public FieldRegistry getFields() {
        return FIELDS;
    }

    @Override
    protected void apply(ServerLevel serverLevel, TickClock tickClock) {
        tickClock.set(TickClock.DISPLAY_TIME, tickClock.get(TickClock.DISPLAY_TIME) + this.get(INCREMENT));
        this.set(CONSUMED);
    }

}
