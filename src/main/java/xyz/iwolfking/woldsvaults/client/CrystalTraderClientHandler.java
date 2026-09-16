package xyz.iwolfking.woldsvaults.client;

import net.minecraft.client.Minecraft;
import xyz.iwolfking.woldsvaults.container.CrystalTraderContainer;
import xyz.iwolfking.woldsvaults.network.ClientboundCrystalTraderSyncMessage;

public class CrystalTraderClientHandler {
    public static void handle(ClientboundCrystalTraderSyncMessage message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.containerMenu instanceof CrystalTraderContainer container) {
            container.setOffers(message.getOffers());
            container.setSoldOutIndices(message.getSoldOutIndices());
            container.setReputationBalance(message.getReputationBalance());
            container.setResetCost(message.getResetCost());
            container.setPlayerCoinCount(message.getPlayerCoinCount());
        }
    }
}
