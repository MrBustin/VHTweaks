package xyz.iwolfking.woldsvaults.network.overworld;

import xyz.iwolfking.woldsvaults.client.overworld.gui.OverworldVaultGuiUpdateManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundOverworldVaultGuiRequestPacket {
    public ServerboundOverworldVaultGuiRequestPacket() {
    }

    public static void encode(ServerboundOverworldVaultGuiRequestPacket packet, FriendlyByteBuf buf) {
    }

    public static ServerboundOverworldVaultGuiRequestPacket decode(FriendlyByteBuf buf) {
        return new ServerboundOverworldVaultGuiRequestPacket();
    }

    public static void handle(ServerboundOverworldVaultGuiRequestPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player != null) {
                OverworldVaultGuiUpdateManager.open(player);
            }
        });
        context.setPacketHandled(true);
    }
}
