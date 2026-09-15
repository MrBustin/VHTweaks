package xyz.iwolfking.woldsvaults.container;

import iskallia.vault.container.GreedTraderContainer;
import iskallia.vault.world.data.PlayerGreedTraderData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xyz.iwolfking.woldsvaults.init.ModContainers;

public class CrystalTraderContainer extends GreedTraderContainer {
    public CrystalTraderContainer(int windowId, Player player) {
        super(windowId, player);
    }

    public CrystalTraderContainer(int windowId, Player player, List<PlayerGreedTraderData.TradeOffer> offers, Set<Integer> soldOutIndices, int reputationBalance, int resetCost, int questRerollCost, int playerCoinCount) {
        super(windowId, player, offers, soldOutIndices, reputationBalance, resetCost, questRerollCost, playerCoinCount);
    }

    @Override
    public MenuType<?> getType() {
        return ModContainers.CRYSTAL_TRADER_CONTAINER;
    }

    public static CrystalTraderContainer fromNetwork(int windowId, Inventory inventory, FriendlyByteBuf buf) {
        int offerCount = buf.readInt();
        List<PlayerGreedTraderData.TradeOffer> offers = new ArrayList();
        Set<Integer> soldOut = new HashSet();

        for(int i = 0; i < offerCount; ++i) {
            ItemStack stack = buf.readItem();
            int cost = buf.readInt();
            boolean sold = buf.readBoolean();
            offers.add(new PlayerGreedTraderData.TradeOffer(stack, cost));
            if (sold) {
                soldOut.add(i);
            }
        }

        int reputationBalance = buf.readInt();
        int resetCost = buf.readInt();
        int questRerollCost = buf.readInt();
        int playerCoinCount = buf.readInt();
        return new CrystalTraderContainer(windowId, inventory.player, offers, soldOut, reputationBalance, resetCost, questRerollCost, playerCoinCount);
    }

}
