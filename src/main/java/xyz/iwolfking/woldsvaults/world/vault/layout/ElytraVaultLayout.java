package xyz.iwolfking.woldsvaults.world.vault.layout;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.generator.layout.ClassicInfiniteLayout;
import iskallia.vault.core.world.generator.layout.GridLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;

/** A finite, branch-free classic vault extending east from the start room. */
public class ElytraVaultLayout extends ClassicInfiniteLayout {
    public static final SupplierKey<GridLayout> KEY = SupplierKey.of("elytra", GridLayout.class)
            .with(Version.latest(), ElytraVaultLayout::new);
    public static final FieldRegistry FIELDS = ClassicInfiniteLayout.FIELDS.merge(new FieldRegistry());
    public static final FieldKey<Integer> ROOM_COUNT = FieldKey.of("room_count", Integer.class)
            .with(Version.latest(), Adapters.INT, DISK.all()).register(FIELDS);

    protected ElytraVaultLayout() {
        this(6);
    }

    public ElytraVaultLayout(int roomCount) {
        super(1);
        this.set(ROOM_COUNT, Math.max(1, roomCount));
    }

    @Override
    public SupplierKey<GridLayout> getKey() {
        return KEY;
    }

    @Override
    public FieldRegistry getFields() {
        return FIELDS;
    }

    @Override
    public VaultLayout.PieceType getType(Vault vault, RegionPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        int finalRoomX = this.get(ROOM_COUNT) * 2;

        if (z != 0 || x < 0 || x > finalRoomX) {
            return VaultLayout.PieceType.NONE;
        }
        if (x == 0) {
            return VaultLayout.PieceType.START_EAST;
        }
        return x % 2 == 0 ? VaultLayout.PieceType.ROOM : VaultLayout.PieceType.TUNNEL_X;
    }
}
