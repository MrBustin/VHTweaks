package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.client.vault.ClientPowerData;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KnownPowerMessage {
    private final PowerTree tree;

    public KnownPowerMessage(PowerTree tree) {
        this.tree = tree;
    }

    public PowerTree getTree() {
        return this.tree;
    }

    public static void encode(KnownPowerMessage message, FriendlyByteBuf buffer) {
        ArrayBitBuffer bits = ArrayBitBuffer.empty();
        message.tree.writeBits(bits);
        buffer.writeLongArray(bits.toLongArray());
    }

    public static KnownPowerMessage decode(FriendlyByteBuf buffer) {
        BitBuffer bits = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
        PowerTree tree = new PowerTree();
        tree.readBits(bits);
        return new KnownPowerMessage(tree);
    }

    public static void handle(KnownPowerMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPowerData.updateTalents(message));
        context.setPacketHandled(true);
    }
}