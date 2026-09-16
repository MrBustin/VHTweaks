package xyz.iwolfking.woldsvaults.data;

import iskallia.vault.config.gear.VaultEtchingConfig;
import iskallia.vault.config.greed.GreedTraderConfig;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.world.generator.layout.ArchitectRoomEntry;
import iskallia.vault.entity.entity.GreedTraderData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.modifiers.DefaultCrystalModifiers;
import iskallia.vault.item.data.InscriptionData;
import iskallia.vault.item.gear.EtchingItem;
import iskallia.vault.util.LootInitialization;
import iskallia.vault.world.data.PlayerGreedTreeData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.network.ClientboundCrystalTraderSyncMessage;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerCrystalTraderData extends SavedData {
    protected static final String DATA_NAME = "woldsvaults_PlayerCrystalTrader";
    private final Map<UUID, PlayerCrystalTraderData.PlayerOfferData> playerOffers = new HashMap();

    public PlayerCrystalTraderData() {
    }

    public List<PlayerCrystalTraderData.TradeOffer> getOffers(UUID playerUuid, int greedTier, int playerVaultLevel) {
        PlayerCrystalTraderData.PlayerOfferData data = (PlayerCrystalTraderData.PlayerOfferData)this.playerOffers.get(playerUuid);
        if (data != null && !data.offers.isEmpty()) {
            int slotCount = this.calculateSlotCount(greedTier);
            if (greedTier != data.generatedAtTier || data.offers.size() != slotCount) {
                List<PlayerCrystalTraderData.TradeOffer> newOffers = this.rollOffers(greedTier, slotCount, playerVaultLevel);
                data.offers.clear();
                data.offers.addAll(newOffers);
                data.soldOutIndices.clear();
                data.generatedAtTier = greedTier;
                this.setDirty();
            }

            return data.offers;
        } else {
            int slotCount = this.calculateSlotCount(greedTier);
            List<PlayerCrystalTraderData.TradeOffer> offers = this.rollOffers(greedTier, slotCount, playerVaultLevel);
            this.playerOffers.put(playerUuid, new PlayerCrystalTraderData.PlayerOfferData(offers, greedTier));
            this.setDirty();
            return offers;
        }
    }

    public void rerollOffers(ServerPlayer player) {
        int greedTier = PlayerGreedTreeData.get(player.getLevel()).getGreedTier(player);
        int playerVaultLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player).getVaultLevel();
        int slotCount = this.calculateSlotCount(greedTier);
        List<PlayerCrystalTraderData.TradeOffer> newOffers = this.rollOffers(greedTier, slotCount, playerVaultLevel);
        PlayerCrystalTraderData.PlayerOfferData existing = (PlayerCrystalTraderData.PlayerOfferData)this.playerOffers.get(player.getUUID());
        PlayerCrystalTraderData.PlayerOfferData data = new PlayerCrystalTraderData.PlayerOfferData(newOffers, greedTier);
        if (existing != null) {
            data.resetCount = existing.resetCount;
        }

        this.playerOffers.put(player.getUUID(), data);
        this.setDirty();
        this.syncToClient(player);
    }

    private int calculateSlotCount(int greedTier) {
        return 6;
    }

    private List<PlayerCrystalTraderData.TradeOffer> rollOffers(int greedTier, int count, int playerVaultLevel) {
        List<PlayerCrystalTraderData.TradeOffer> offers = new ArrayList();
        GreedTraderConfig config = ModConfigs.GREED_TRADER;
        if (config == null) {
            return offers;
        } else {
            List<GreedTraderConfig.TradeEntry> availableEntries = config.getAvailableEntries(greedTier);
            if (availableEntries.isEmpty()) {
                return offers;
            } else {
                Random random = new Random();
                int totalWeight = availableEntries.stream().mapToInt(GreedTraderConfig.TradeEntry::getWeight).sum();
                if (totalWeight <= 0) {
                    return offers;
                } else {
                    int attempts = 0;
                    int maxAttempts = Math.max(count * 20, 20);
                    while(offers.size() < count && attempts++ < maxAttempts) {
                        int roll = random.nextInt(totalWeight);
                        int cumulative = 0;
                        GreedTraderConfig.TradeEntry entry = null;

                        for(GreedTraderConfig.TradeEntry candidate : availableEntries) {
                            cumulative += candidate.getWeight();
                            if (roll < cumulative) {
                                entry = candidate;
                                break;
                            }
                        }

                        if (entry != null) {
                            PlayerCrystalTraderData.TradeOffer offer = this.rollSingleOffer(entry, greedTier, config, random, playerVaultLevel);
                            if (offer != null) {
                                offers.add(offer);
                            }
                        }
                    }

                    return offers;
                }
            }
        }
    }

    private PlayerCrystalTraderData.TradeOffer rollSingleOffer(GreedTraderConfig.TradeEntry entry, int greedTier, GreedTraderConfig config, Random random, int playerVaultLevel) {
        PlayerCrystalTraderData.TradeOffer tradeOffer;
        if (entry.getType().equals("pool")) {
            tradeOffer = this.rollFromPool(entry, greedTier, config, random, playerVaultLevel);
        } else {
            tradeOffer = this.rollNormalItem(entry, config, random);
        }

        return tradeOffer;
    }

    private PlayerCrystalTraderData.TradeOffer rollNormalItem(GreedTraderConfig.TradeEntry entry, GreedTraderConfig config, Random random) {
        ItemStack templateStack = entry.getItemStack();
        if (templateStack != null && !templateStack.isEmpty()) {
            ItemStack offerStack = templateStack.copy();
            int range = Math.max(1, entry.getMaxAmount() - entry.getMinAmount() + 1);
            int amount = entry.getMinAmount() + random.nextInt(range);
            offerStack.setCount(amount);
            int coinCost = config.rollCoinCost(entry, random);
            return new PlayerCrystalTraderData.TradeOffer(offerStack, coinCost);
        } else {
            return null;
        }
    }

    private PlayerCrystalTraderData.TradeOffer rollFromPool(GreedTraderConfig.TradeEntry entry, int greedTier, GreedTraderConfig config, Random random, int playerVaultLevel) {
        String poolId = entry.getPoolId();
        if (poolId == null) {
            return null;
        } else {
            List<GreedTraderConfig.PoolEntry> poolEntries = config.getPoolEntries(poolId, greedTier);
            if (poolEntries.isEmpty()) {
                return null;
            } else {
                int totalWeight = poolEntries.stream().mapToInt(GreedTraderConfig.PoolEntry::getWeight).sum();
                if (totalWeight <= 0) {
                    return null;
                } else {
                    int roll = random.nextInt(totalWeight);
                    int cumulative = 0;
                    GreedTraderConfig.PoolEntry chosen = null;

                    for(GreedTraderConfig.PoolEntry poolEntry : poolEntries) {
                        cumulative += poolEntry.getWeight();
                        if (roll < cumulative) {
                            chosen = poolEntry;
                            break;
                        }
                    }

                    if (chosen == null) {
                        return null;
                    } else {
                        ItemStack stack = this.buildPoolEntryStack(chosen, random, playerVaultLevel);
                        if (stack != null && !stack.isEmpty()) {
                            int coinCost = chosen.rollCoinCost(random, config.rollCoinCost(entry, random));
                            return new PlayerCrystalTraderData.TradeOffer(LootInitialization.initializeVaultLoot(stack, playerVaultLevel), coinCost);
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
    }

    private ItemStack buildPoolEntryStack(GreedTraderConfig.PoolEntry poolEntry, Random random, int playerVaultLevel) {
        ItemStack stack;
        if (poolEntry.getType().equals("random_crystal")) {
            stack = this.buildCrystal(poolEntry.getCrystal(), random, playerVaultLevel);
        } else {
            stack = poolEntry.getItemStack() != null ? poolEntry.getItemStack().copy() : null;
        }

        return stack;
    }

    private ItemStack buildCrystal(GreedTraderConfig.CrystalBuilder builder, Random random, int playerVaultLevel) {
        if (builder == null) {
            return null;
        } else {
            GreedTraderConfig config = ModConfigs.GREED_TRADER;
            ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
            CrystalData crystal = CrystalData.read(stack);
            crystal.getProperties().setLevel(playerVaultLevel);
            if (config != null) {
                DefaultCrystalModifiers modifiers = new DefaultCrystalModifiers();
                if (builder.getModifierPool() != null) {
                    for(String modifierId : config.rollModifiersFromPool(builder.getModifierPool(), builder.getMinModifiers(), builder.getMaxModifiers(), random, true)) {
                        ResourceLocation id = ResourceLocation.parse(modifierId);
                        VaultModifierRegistry.getOpt(id).ifPresent((modifier) -> modifiers.add(VaultModifierStack.of(modifier)));
                    }
                }

                if (builder.getModifierPoolTwo() != null) {
                    for(String modifierId : config.rollModifiersFromPool(builder.getModifierPoolTwo(), builder.getMinModifiersTwo(), builder.getMaxModifiersTwo(), random, true)) {
                        ResourceLocation id = ResourceLocation.parse(modifierId);
                        VaultModifierRegistry.getOpt(id).ifPresent((modifier) -> modifiers.add(VaultModifierStack.of(modifier)));
                    }
                }

                if (builder.getModifierPoolThree() != null) {
                    for(String modifierId : config.rollModifiersFromPool(builder.getModifierPoolThree(), builder.getMinModifiersThree(), builder.getMaxModifiersThree(), random, true)) {
                        ResourceLocation id = ResourceLocation.parse(modifierId);
                        VaultModifierRegistry.getOpt(id).ifPresent((modifier) -> modifiers.add(VaultModifierStack.of(modifier)));
                    }
                }

                crystal.setModifiers(modifiers);
            }

            crystal.write(stack);
            return stack;
        }
    }

    public boolean isSoldOut(UUID playerUuid, int index) {
        PlayerCrystalTraderData.PlayerOfferData data = this.playerOffers.get(playerUuid);
        return data != null && data.soldOutIndices.contains(index);
    }

    public void markSoldOut(UUID playerUuid, int index) {
        PlayerCrystalTraderData.PlayerOfferData data = this.playerOffers.get(playerUuid);
        if (data != null) {
            data.soldOutIndices.add(index);
            this.setDirty();
        }

    }

    public Set<Integer> getSoldOutIndices(UUID playerUuid) {
        PlayerCrystalTraderData.PlayerOfferData data = this.playerOffers.get(playerUuid);
        return data != null ? Collections.unmodifiableSet(data.soldOutIndices) : Collections.emptySet();
    }

    public int getResetCount(UUID playerUuid) {
        PlayerCrystalTraderData.PlayerOfferData data = this.playerOffers.get(playerUuid);
        return data != null ? data.resetCount : 0;
    }

    public int getResetCost(UUID playerUuid) {
        int resetCount = this.getResetCount(playerUuid);
        return 3 + resetCount;
    }

    public void resetShop(ServerPlayer player) {
        PlayerCrystalTraderData.PlayerOfferData existing = this.playerOffers.get(player.getUUID());
        if (existing != null) {
            ++existing.resetCount;
        }

        this.rerollOffers(player);
    }

    public void writeOffers(UUID playerUuid, int greedTier, int playerVaultLevel, FriendlyByteBuf buf) {
        List<PlayerCrystalTraderData.TradeOffer> offers = this.getOffers(playerUuid, greedTier, playerVaultLevel);
        Set<Integer> soldOut = this.getSoldOutIndices(playerUuid);
        buf.writeInt(offers.size());

        for(int i = 0; i < offers.size(); ++i) {
            PlayerCrystalTraderData.TradeOffer offer = offers.get(i);
            buf.writeItem(offer.stack());
            buf.writeInt(offer.coinCost());
            buf.writeBoolean(soldOut.contains(i));
        }

    }

    public void syncToClient(ServerPlayer player) {
        int greedTier = PlayerGreedTreeData.get(player.getLevel()).getGreedTier(player);
        int playerVaultLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player).getVaultLevel();
        List<PlayerCrystalTraderData.TradeOffer> offers = this.getOffers(player.getUUID(), greedTier, playerVaultLevel);
        Set<Integer> soldOut = this.getSoldOutIndices(player.getUUID());
        int reputation = PlayerGreedTreeData.get(player.getLevel()).getGreedReputation(player);
        int resetCost = this.getResetCost(player.getUUID());
        int coinCount = GreedTraderData.getGreedCoinCount(player);
        ModNetwork.sendToClient(new ClientboundCrystalTraderSyncMessage(offers, soldOut, reputation, resetCost, coinCount), player);
    }

    private static PlayerCrystalTraderData create(CompoundTag tag) {
        PlayerCrystalTraderData data = new PlayerCrystalTraderData();
        data.load(tag);
        return data;
    }

    public void load(CompoundTag nbt) {
        this.playerOffers.clear();
        CompoundTag playersTag = nbt.getCompound("Players");

        for(String uuidStr : playersTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            CompoundTag playerTag = playersTag.getCompound(uuidStr);
            int generatedAtTier = playerTag.getInt("GeneratedAtTier");
            ListTag offersTag = playerTag.getList("Offers", 10);
            List<PlayerCrystalTraderData.TradeOffer> offers = new ArrayList();

            for(int i = 0; i < offersTag.size(); ++i) {
                CompoundTag offerTag = offersTag.getCompound(i);
                ItemStack stack = ItemStack.of(offerTag.getCompound("Stack"));
                int coinCost = offerTag.getInt("CoinCost");
                if (!stack.isEmpty()) {
                    offers.add(new PlayerCrystalTraderData.TradeOffer(stack, coinCost));
                }
            }

            PlayerCrystalTraderData.PlayerOfferData offerData = new PlayerCrystalTraderData.PlayerOfferData(offers, generatedAtTier);
            if (playerTag.contains("SoldOut")) {
                for(int idx : playerTag.getIntArray("SoldOut")) {
                    offerData.soldOutIndices.add(idx);
                }
            }

            offerData.resetCount = playerTag.getInt("ResetCount");
            this.playerOffers.put(uuid, offerData);
        }

    }

    public CompoundTag save(CompoundTag nbt) {
        CompoundTag playersTag = new CompoundTag();
        this.playerOffers.forEach((uuid, data) -> {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putInt("GeneratedAtTier", data.generatedAtTier);
            playerTag.putIntArray("SoldOut", data.soldOutIndices.stream().mapToInt(Integer::intValue).toArray());
            playerTag.putInt("ResetCount", data.resetCount);
            ListTag offersTag = new ListTag();

            for(PlayerCrystalTraderData.TradeOffer offer : data.offers) {
                CompoundTag offerTag = new CompoundTag();
                offerTag.put("Stack", offer.stack().save(new CompoundTag()));
                offerTag.putInt("CoinCost", offer.coinCost());
                offersTag.add(offerTag);
            }

            playerTag.put("Offers", offersTag);
            playersTag.put(uuid.toString(), playerTag);
        });
        nbt.put("Players", playersTag);
        return nbt;
    }

    public static PlayerCrystalTraderData get(ServerLevel level) {
        return get(level.getServer());
    }

    public static PlayerCrystalTraderData get(MinecraftServer server) {
        return (PlayerCrystalTraderData)server.overworld().getDataStorage().computeIfAbsent(PlayerCrystalTraderData::create, PlayerCrystalTraderData::new, DATA_NAME);
    }

    private static class PlayerOfferData {
        final List<PlayerCrystalTraderData.TradeOffer> offers;
        final Set<Integer> soldOutIndices;
        int generatedAtTier;
        int resetCount;

        PlayerOfferData(List<PlayerCrystalTraderData.TradeOffer> offers, int generatedAtTier) {
            this.offers = offers;
            this.soldOutIndices = new HashSet();
            this.generatedAtTier = generatedAtTier;
            this.resetCount = 0;
        }
    }

    public static record TradeOffer(ItemStack stack, int coinCost) {
        public TradeOffer(ItemStack stack, int coinCost) {
            this.stack = stack;
            this.coinCost = coinCost;
        }

        public ItemStack createItemStack() {
            return this.stack.copy();
        }

        public ItemStack stack() {
            return this.stack;
        }

        public int coinCost() {
            return this.coinCost;
        }
    }
}
