package xyz.iwolfking.woldsvaults.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.client.data.ClientDiscoveredCapesData;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class DiscoveredCapesMessage {

    private final Set<String> discoveredCapes;
    private final String selectedCape;
    private final boolean capeEnabled;

    public DiscoveredCapesMessage(Set<String> discoveredCapes, @Nullable String selectedCape, boolean capeEnabled) {
        this.discoveredCapes = discoveredCapes;
        this.selectedCape = selectedCape;
        this.capeEnabled = capeEnabled;
    }

    public static void encode(DiscoveredCapesMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.discoveredCapes.size());
        for (String id : message.discoveredCapes) {
            buffer.writeUtf(id);
        }
        buffer.writeBoolean(message.selectedCape != null);
        if (message.selectedCape != null) {
            buffer.writeUtf(message.selectedCape);
        }
        buffer.writeBoolean(message.capeEnabled);
    }

    public static DiscoveredCapesMessage decode(FriendlyByteBuf buffer) {
        Set<String> discovered = new HashSet<>();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            discovered.add(buffer.readUtf());
        }
        String selected = buffer.readBoolean() ? buffer.readUtf() : null;
        boolean enabled = buffer.readBoolean();
        return new DiscoveredCapesMessage(discovered, selected, enabled);
    }

    public static void handle(DiscoveredCapesMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientDiscoveredCapesData.receiveUpdate(
                message.discoveredCapes, message.selectedCape, message.capeEnabled));
        context.setPacketHandled(true);
    }
}
