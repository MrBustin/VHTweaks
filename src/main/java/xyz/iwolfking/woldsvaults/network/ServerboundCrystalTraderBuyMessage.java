package xyz.iwolfking.woldsvaults.network;

import iskallia.vault.entity.entity.GreedTraderData;
import iskallia.vault.init.ModSounds;
import iskallia.vault.world.data.PlayerGreedTreeData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.container.CrystalTraderContainer;
import xyz.iwolfking.woldsvaults.data.PlayerCrystalTraderData;

import java.util.List;
import java.util.function.Supplier;

public class ServerboundCrystalTraderBuyMessage {
    private final int tradeIndex;

    public ServerboundCrystalTraderBuyMessage(int tradeIndex) {
        this.tradeIndex = tradeIndex;
    }

    public static void encode(ServerboundCrystalTraderBuyMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.tradeIndex);
    }

    public static ServerboundCrystalTraderBuyMessage decode(FriendlyByteBuf buf) {
        return new ServerboundCrystalTraderBuyMessage(buf.readInt());
    }

    public static void handle(ServerboundCrystalTraderBuyMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof CrystalTraderContainer container)) {
                return;
            }

            java.util.UUID traderId = container.getTraderId();
            PlayerCrystalTraderData traderData = PlayerCrystalTraderData.get(player.getLevel());
            int greedTier = PlayerGreedTreeData.get(player.getLevel()).getGreedTier(player);
            int playerVaultLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player).getVaultLevel();
            List<PlayerCrystalTraderData.TradeOffer> offers = traderData.getOffers(traderId, greedTier, playerVaultLevel);
            if (message.tradeIndex < 0 || message.tradeIndex >= offers.size() || traderData.isSoldOut(traderId, message.tradeIndex)) {
                return;
            }

            PlayerCrystalTraderData.TradeOffer offer = offers.get(message.tradeIndex);
            if (GreedTraderData.getGreedCoinCount(player) < offer.coinCost()
                    || !GreedTraderData.consumeGreedCoins(player, offer.coinCost())) {
                return;
            }

            traderData.markSoldOut(traderId, message.tradeIndex);
            ItemHandlerHelper.giveItemToPlayer(player, offer.createItemStack());
            player.level.playSound(null, player.blockPosition(), ModSounds.GREED_TRADER_PURCHASE, SoundSource.MASTER, 0.4F, 1.0F);
            player.inventoryMenu.broadcastChanges();
            traderData.syncToClient(player, traderId);
        });
        context.setPacketHandled(true);
    }
}
