package xyz.iwolfking.woldsvaults.network;

import iskallia.vault.greed.GreedTree;
import iskallia.vault.world.data.PlayerGreedTreeData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.container.CrystalTraderContainer;
import xyz.iwolfking.woldsvaults.data.PlayerCrystalTraderData;

import java.util.function.Supplier;

public class ServerboundCrystalTraderResetMessage {
    public static void encode(ServerboundCrystalTraderResetMessage message, FriendlyByteBuf buf) {
    }

    public static ServerboundCrystalTraderResetMessage decode(FriendlyByteBuf buf) {
        return new ServerboundCrystalTraderResetMessage();
    }

    public static void handle(ServerboundCrystalTraderResetMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof CrystalTraderContainer container)) {
                return;
            }

            java.util.UUID traderId = container.getTraderId();
            PlayerCrystalTraderData traderData = PlayerCrystalTraderData.get(player.getLevel());
            PlayerGreedTreeData treeData = PlayerGreedTreeData.get(player.getLevel());
            GreedTree tree = treeData.getGreedTree(player);
            if (tree == null) {
                return;
            }

            int resetCost = traderData.getResetCost(traderId);
            int reputation = tree.getGreedReputation();
            if (reputation < resetCost) {
                return;
            }

            tree.setGreedReputation(reputation - resetCost);
            treeData.setDirty();
            treeData.sync(player);
            traderData.resetShop(player, traderId);
        });
        context.setPacketHandled(true);
    }
}
