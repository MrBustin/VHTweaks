package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.client.ClientEventData;
import io.github.a1qs.vaultadditions.data.EventData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class EventSyncMessage {
    private final boolean globeExpanderRequired;
    private final boolean isEventActive;

    public EventSyncMessage(boolean globeExpanderRequired, boolean isEventActive) {
        this.globeExpanderRequired = globeExpanderRequired;
        this.isEventActive = isEventActive;
    }

    public static void encode(EventSyncMessage msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.globeExpanderRequired);
        buffer.writeBoolean(msg.isEventActive);
    }

    public static EventSyncMessage decode(FriendlyByteBuf buffer) {
        return new EventSyncMessage(buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(EventSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ClientEventData.update(msg.globeExpanderRequired, msg.isEventActive);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
