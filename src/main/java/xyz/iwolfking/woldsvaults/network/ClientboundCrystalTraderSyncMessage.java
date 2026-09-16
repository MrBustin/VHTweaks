package xyz.iwolfking.woldsvaults.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.client.CrystalTraderClientHandler;
import xyz.iwolfking.woldsvaults.data.PlayerCrystalTraderData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ClientboundCrystalTraderSyncMessage {
    private final List<PlayerCrystalTraderData.TradeOffer> offers;
    private final Set<Integer> soldOutIndices;
    private final int reputationBalance;
    private final int resetCost;
    private final int playerCoinCount;

    public ClientboundCrystalTraderSyncMessage(List<PlayerCrystalTraderData.TradeOffer> offers, Set<Integer> soldOutIndices, int reputationBalance, int resetCost, int playerCoinCount) {
        this.offers = offers;
        this.soldOutIndices = soldOutIndices;
        this.reputationBalance = reputationBalance;
        this.resetCost = resetCost;
        this.playerCoinCount = playerCoinCount;
    }

    public List<PlayerCrystalTraderData.TradeOffer> getOffers() {
        return this.offers;
    }

    public Set<Integer> getSoldOutIndices() {
        return this.soldOutIndices;
    }

    public int getReputationBalance() {
        return this.reputationBalance;
    }

    public int getResetCost() {
        return this.resetCost;
    }

    public int getPlayerCoinCount() {
        return this.playerCoinCount;
    }

    public static void encode(ClientboundCrystalTraderSyncMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.offers.size());

        for(int i = 0; i < message.offers.size(); ++i) {
            PlayerCrystalTraderData.TradeOffer offer = message.offers.get(i);
            buf.writeItem(offer.stack());
            buf.writeInt(offer.coinCost());
            buf.writeBoolean(message.soldOutIndices.contains(i));
        }

        buf.writeInt(message.reputationBalance);
        buf.writeInt(message.resetCost);
        buf.writeInt(message.playerCoinCount);
    }

    public static ClientboundCrystalTraderSyncMessage decode(FriendlyByteBuf buf) {
        int offerCount = buf.readInt();
        List<PlayerCrystalTraderData.TradeOffer> offers = new ArrayList<>();
        Set<Integer> soldOutIndices = new HashSet<>();

        for(int i = 0; i < offerCount; ++i) {
            ItemStack stack = buf.readItem();
            int coinCost = buf.readInt();
            boolean soldOut = buf.readBoolean();
            offers.add(new PlayerCrystalTraderData.TradeOffer(stack, coinCost));
            if (soldOut) {
                soldOutIndices.add(i);
            }
        }

        int reputationBalance = buf.readInt();
        int resetCost = buf.readInt();
        int playerCoinCount = buf.readInt();
        return new ClientboundCrystalTraderSyncMessage(offers, soldOutIndices, reputationBalance, resetCost, playerCoinCount);
    }

    public static void handle(ClientboundCrystalTraderSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CrystalTraderClientHandler.handle(message)));
        context.setPacketHandled(true);
    }
}
