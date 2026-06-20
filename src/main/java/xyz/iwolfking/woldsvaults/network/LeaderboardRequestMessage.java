package xyz.iwolfking.woldsvaults.network;

import xyz.iwolfking.woldsvaults.data.EventData;
import xyz.iwolfking.woldsvaults.data.PowerCrystalData;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.time.ZoneId;
import java.util.function.Supplier;

public class LeaderboardRequestMessage {
    public LeaderboardRequestMessage() {}

    public static void encode(LeaderboardRequestMessage msg, FriendlyByteBuf buffer) {}

    public static LeaderboardRequestMessage decode(FriendlyByteBuf buffer) {
        return new LeaderboardRequestMessage();
    }

    public static void handle(LeaderboardRequestMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer player = contextSupplier.get().getSender();
        if (player != null) {
            PowerCrystalData powerData = PowerCrystalData.getServer();
            EventData eventData = EventData.getServer();
            ModNetwork.sendToClient(new LeaderboardDataMessage(
                    powerData.getPlayerContributionsMap(),
                    eventData.getNextScheduledEvent(),
                    ZoneId.systemDefault().toString(),
                    eventData.getActiveEvent(),
                    eventData.getEventDuration(),
                    powerData.getTotalContributedCrystals()),
                    player);
        }
        contextSupplier.get().setPacketHandled(true);
    }
}
