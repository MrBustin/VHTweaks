package xyz.iwolfking.woldsvaults.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.api.extension.PlayerCustomisationStationContainerExtension;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record SelectCapeMessage(@Nullable String capeId, boolean enabled) {

    public static void encode(SelectCapeMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.capeId != null);
        if (message.capeId != null) {
            buffer.writeUtf(message.capeId);
        }
        buffer.writeBoolean(message.enabled);
    }

    public static SelectCapeMessage decode(FriendlyByteBuf buffer) {
        String capeId = buffer.readBoolean() ? buffer.readUtf() : null;
        return new SelectCapeMessage(capeId, buffer.readBoolean());
    }

    public static void handle(SelectCapeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            if (player.containerMenu instanceof PlayerCustomisationStationContainerExtension extension) {
                extension.woldsvaults$applyCapeSelection(player, message.capeId, message.enabled);
            }
        });
        context.setPacketHandled(true);
    }
}
