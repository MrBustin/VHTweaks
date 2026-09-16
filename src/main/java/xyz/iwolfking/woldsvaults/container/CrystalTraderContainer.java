package xyz.iwolfking.woldsvaults.container;

import iskallia.vault.container.spi.AbstractElementContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.data.PlayerCrystalTraderData;
import xyz.iwolfking.woldsvaults.init.ModContainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrystalTraderContainer extends AbstractElementContainer {
    private final List<PlayerCrystalTraderData.TradeOffer> offers;
    private final Set<Integer> soldOutIndices;
    private int reputationBalance;
    private int resetCost;
    private int playerCoinCount;

    public CrystalTraderContainer(int windowId, Player player) {
        super(ModContainers.CRYSTAL_TRADER_CONTAINER, windowId, player);
        this.offers = new ArrayList<>();
        this.soldOutIndices = new HashSet<>();
        this.reputationBalance = 0;
        this.resetCost = 1;
        this.playerCoinCount = 0;
    }

    public CrystalTraderContainer(int windowId, Player player, List<PlayerCrystalTraderData.TradeOffer> offers, Set<Integer> soldOutIndices, int reputationBalance, int resetCost, int playerCoinCount) {
        super(ModContainers.CRYSTAL_TRADER_CONTAINER, windowId, player);
        this.offers = new ArrayList<>(offers);
        this.soldOutIndices = new HashSet<>(soldOutIndices);
        this.reputationBalance = reputationBalance;
        this.resetCost = resetCost;
        this.playerCoinCount = playerCoinCount;
    }

    @Override
    public MenuType<?> getType() {
        return ModContainers.CRYSTAL_TRADER_CONTAINER;
    }

    public List<PlayerCrystalTraderData.TradeOffer> getOffers() {
        return this.offers;
    }

    public boolean isSoldOut(int index) {
        return this.soldOutIndices.contains(index);
    }

    public Set<Integer> getSoldOutIndices() {
        return this.soldOutIndices;
    }

    public int getReputationBalance() {
        return this.reputationBalance;
    }

    public void setReputationBalance(int reputationBalance) {
        this.reputationBalance = reputationBalance;
    }

    public int getResetCost() {
        return this.resetCost;
    }

    public void setResetCost(int resetCost) {
        this.resetCost = resetCost;
    }

    public int getPlayerCoinCount() {
        return this.playerCoinCount;
    }

    public void setPlayerCoinCount(int playerCoinCount) {
        this.playerCoinCount = playerCoinCount;
    }

    public void setOffers(List<PlayerCrystalTraderData.TradeOffer> offers) {
        this.offers.clear();
        this.offers.addAll(offers);
    }

    public void setSoldOutIndices(Set<Integer> soldOutIndices) {
        this.soldOutIndices.clear();
        this.soldOutIndices.addAll(soldOutIndices);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static CrystalTraderContainer fromNetwork(int windowId, Inventory inventory, FriendlyByteBuf buf) {
        int offerCount = buf.readInt();
        List<PlayerCrystalTraderData.TradeOffer> offers = new ArrayList<>();
        Set<Integer> soldOut = new HashSet();

        for(int i = 0; i < offerCount; ++i) {
            ItemStack stack = buf.readItem();
            int cost = buf.readInt();
            boolean sold = buf.readBoolean();
            offers.add(new PlayerCrystalTraderData.TradeOffer(stack, cost));
            if (sold) {
                soldOut.add(i);
            }
        }

        int reputationBalance = buf.readInt();
        int resetCost = buf.readInt();
        int playerCoinCount = buf.readInt();
        return new CrystalTraderContainer(windowId, inventory.player, offers, soldOut, reputationBalance, resetCost, playerCoinCount);
    }

}
