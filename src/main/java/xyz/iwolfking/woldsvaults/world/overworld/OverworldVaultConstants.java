package xyz.iwolfking.woldsvaults.world.overworld;

import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.Comparator;

public class OverworldVaultConstants {
    public static final TicketType<ChunkPos> OVERWORLD_VAULT_TICKET = TicketType.create("woldsvaults", Comparator.comparingLong(ChunkPos::toLong));
}
