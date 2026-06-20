package xyz.iwolfking.woldsvaults.network;

import xyz.iwolfking.woldsvaults.client.ClientMessageHandler;
import xyz.iwolfking.woldsvaults.events.additions.WoldsVaultsEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class LeaderboardDataMessage {
    public final Map<UUID, Integer> leaderboard;
    public final String nextScheduledEvent;
    public final String serverTimeZone;
    public final WoldsVaultsEvent optionalEvent;
    public final long optionalEventDuration;
    public final int totalContributions;

    public LeaderboardDataMessage(Map<UUID, Integer> leaderboard, String nextScheduledEvent, String serverTimeZone, WoldsVaultsEvent optionalEvent, long optionalEventDuration, int totalContributions) {
        this.leaderboard = leaderboard;
        this.nextScheduledEvent = nextScheduledEvent;
        this.serverTimeZone = serverTimeZone;
        this.optionalEvent = optionalEvent;
        this.optionalEventDuration = optionalEventDuration;
        this.totalContributions = totalContributions;
    }

    public static void encode(LeaderboardDataMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.leaderboard.size());
        msg.leaderboard.forEach((uuid, count) -> {
            buffer.writeUUID(uuid);
            buffer.writeInt(count);
        });

        buffer.writeUtf(msg.nextScheduledEvent);
        buffer.writeUtf(msg.serverTimeZone);

        if (msg.optionalEvent != null) {
            buffer.writeBoolean(true); // Flag to indicate presence
            buffer.writeInt(msg.optionalEvent.getConfigIndex());
            buffer.writeInt(msg.optionalEvent.getRequiredCrystals());
            buffer.writeInt(msg.optionalEvent.getCrystalsSubmitted());
            buffer.writeLong(msg.optionalEventDuration);
        } else {
            buffer.writeBoolean(false); // No event present
        }
        buffer.writeInt(msg.totalContributions);
    }

    public static LeaderboardDataMessage decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        Map<UUID, Integer> leaderboard = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = buffer.readUUID();
            int score = buffer.readInt();
            leaderboard.put(uuid, score);
        }

        String nextScheduledEvent = buffer.readUtf();
        String serverTimeZone = buffer.readUtf();
        long eventDuration = -1L;
        WoldsVaultsEvent optionalEvent = null;
        if (buffer.readBoolean()) { // Check if event is present
            int configIndex = buffer.readInt();
            int requiredCrystals = buffer.readInt();
            int crystalsSubmitted = buffer.readInt();
            optionalEvent = new WoldsVaultsEvent(configIndex, requiredCrystals, crystalsSubmitted);
            eventDuration = buffer.readLong();
        }
        int totalContributions = buffer.readInt();

        return new LeaderboardDataMessage(leaderboard, nextScheduledEvent, serverTimeZone, optionalEvent, eventDuration, totalContributions);
    }

    public static void handle(LeaderboardDataMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            if (FMLEnvironment.dist.isClient()) {
                ClientMessageHandler.handle(msg);
            }
        });

        contextSupplier.get().setPacketHandled(true);
    }
}
