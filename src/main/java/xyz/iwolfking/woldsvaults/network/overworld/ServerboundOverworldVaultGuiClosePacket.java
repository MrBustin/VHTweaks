package xyz.iwolfking.woldsvaults.network.overworld;

import xyz.iwolfking.woldsvaults.client.overworld.gui.OverworldVaultGuiUpdateManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundOverworldVaultGuiClosePacket {
    public ServerboundOverworldVaultGuiClosePacket() {
    }

    public static void encode(ServerboundOverworldVaultGuiClosePacket packet, FriendlyByteBuf buf) {
    }

    public static ServerboundOverworldVaultGuiClosePacket decode(FriendlyByteBuf buf) {
        return new ServerboundOverworldVaultGuiClosePacket();
    }

    public static void handle(ServerboundOverworldVaultGuiClosePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player != null) {
                OverworldVaultGuiUpdateManager.close(player.getUUID());
            }
        });
        context.setPacketHandled(true);
    }
}
