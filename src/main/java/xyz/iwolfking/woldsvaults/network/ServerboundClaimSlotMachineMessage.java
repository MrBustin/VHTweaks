package xyz.iwolfking.woldsvaults.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.container.SlotMachineContainer;

import java.util.function.Supplier;

public class ServerboundClaimSlotMachineMessage {
    public static void encode(ServerboundClaimSlotMachineMessage message, FriendlyByteBuf buffer) {}

    public static ServerboundClaimSlotMachineMessage decode(FriendlyByteBuf buffer) {
        return new ServerboundClaimSlotMachineMessage();
    }

    public static void handle(ServerboundClaimSlotMachineMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof SlotMachineContainer container) {
                container.claim(player);
                player.closeContainer();
            }
        });
        context.setPacketHandled(true);
    }
}
