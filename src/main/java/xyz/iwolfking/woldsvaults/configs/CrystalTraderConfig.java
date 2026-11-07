package xyz.iwolfking.woldsvaults.configs;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.roll.IntRoll;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class CrystalTraderConfig extends Config {
    @Expose
    private int globalCoinCost = 5;
    @Expose
    private Map<Integer, List<CrystalTraderConfig.TradeEntry>> tierPools = new LinkedHashMap();
    @Expose
    private Map<String, List<CrystalTraderConfig.PoolEntry>> pools = new LinkedHashMap();
    @Expose
    private Map<String, List<CrystalTraderConfig.WeightedRoomPool>> roomPoolsByType = new LinkedHashMap();
    @Expose
    private Map<String, List<CrystalTraderConfig.WeightedModifier>> modifierPools = new LinkedHashMap();
    public CrystalTraderConfig() {
    }

    public String getName() {
        return "crystal_trader";
    }

    protected void reset() {
        this.globalCoinCost = 5;
        this.tierPools = new LinkedHashMap();
        List<CrystalTraderConfig.TradeEntry> tier1 = new ArrayList();
        tier1.add(new CrystalTraderConfig.TradeEntry(new ItemStack(Items.DIAMOND), 1, 3, 3, 5, 10));
        tier1.add(new CrystalTraderConfig.TradeEntry(new ItemStack(Items.EMERALD), 4, 8, 2, 4, 10));
        tier1.add(new CrystalTraderConfig.TradeEntry(new ItemStack(Items.IRON_INGOT), 8, 16, 1, 3, 10));
        this.tierPools.put(1, tier1);
        List<CrystalTraderConfig.TradeEntry> tier2 = new ArrayList();
        tier2.add(new CrystalTraderConfig.TradeEntry(new ItemStack(Items.GOLD_INGOT), 4, 12, 5, 8, 10));
        tier2.add(new CrystalTraderConfig.TradeEntry(new ItemStack(Items.NETHERITE_SCRAP), 1, 2, 8, 12, 5));
        this.tierPools.put(2, tier2);
        this.pools = new LinkedHashMap();
        this.roomPoolsByType = new LinkedHashMap();
        this.roomPoolsByType.put("CHALLENGE", List.of(new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/crystal_caves", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/digsite", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/dragon", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/factory", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/laboratory", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/village", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/wildwest", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/challenge/x-mark", 10, 0)));
        this.roomPoolsByType.put("OMEGA", List.of(new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/digsite", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/vendor", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/painting", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/mine", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/blacksmith", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/pixel", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/temple", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/mush_room", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/library", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/cove", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/trove", 10, 0), new CrystalTraderConfig.WeightedRoomPool("the_vault:vault/rooms/omega/warehouse", 10, 0)));
        this.modifierPools = new LinkedHashMap();

    }

    public int rollCoinCost(CrystalTraderConfig.TradeEntry entry, Random random) {
        return entry.rollCoinCost(random, this.globalCoinCost);
    }

    public int getGlobalCoinCost() {
        return this.globalCoinCost;
    }

    public List<CrystalTraderConfig.TradeEntry> getAvailableEntries(int greedTier) {
        int effectiveTier = Math.max(1, greedTier);
        List<CrystalTraderConfig.TradeEntry> combined = new ArrayList();

        for(Map.Entry<Integer, List<CrystalTraderConfig.TradeEntry>> entry : this.tierPools.entrySet()) {
            if ((Integer)entry.getKey() <= effectiveTier) {
                combined.addAll((Collection)entry.getValue());
            }
        }

        return combined;
    }

    public List<CrystalTraderConfig.PoolEntry> getPoolEntries(String poolId, int greedTier) {
        List<CrystalTraderConfig.PoolEntry> pool = (List)this.pools.get(poolId);
        if (pool == null) {
            return Collections.emptyList();
        } else {
            List<CrystalTraderConfig.PoolEntry> filtered = new ArrayList();

            for(CrystalTraderConfig.PoolEntry entry : pool) {
                if (entry.minGreedTier <= greedTier && (entry.maxGreedTier == -1 || greedTier <= entry.maxGreedTier)) {
                    filtered.add(entry);
                }
            }

            return filtered;
        }
    }

    public Map<String, List<CrystalTraderConfig.PoolEntry>> getPools() {
        return this.pools;
    }

    public List<CrystalTraderConfig.WeightedRoomPool> getRoomPoolsForType(String roomType) {
        List<CrystalTraderConfig.WeightedRoomPool> pools = (List)this.roomPoolsByType.get(roomType.toUpperCase());
        return pools != null ? pools : Collections.emptyList();
    }

    public CrystalTraderConfig.WeightedRoomPool rollWeightedRoomPool(String roomType, Random random) {
        List<CrystalTraderConfig.WeightedRoomPool> entries = this.getRoomPoolsForType(roomType);
        if (entries.isEmpty()) {
            return null;
        } else {
            int totalWeight = 0;

            for(CrystalTraderConfig.WeightedRoomPool entry : entries) {
                totalWeight += entry.getWeight();
            }

            if (totalWeight <= 0) {
                return null;
            } else {
                int roll = random.nextInt(totalWeight);

                for(CrystalTraderConfig.WeightedRoomPool entry : entries) {
                    roll -= entry.getWeight();
                    if (roll < 0) {
                        return entry;
                    }
                }

                return (CrystalTraderConfig.WeightedRoomPool)entries.get(entries.size() - 1);
            }
        }
    }

    public List<String> rollModifiersFromPool(String poolName, int min, int max, Random random) {
        return this.rollModifiersFromPool(poolName, min, max, random, false);
    }

    public List<String> rollModifiersFromPool(String poolName, int min, int max, Random random, boolean allowDuplicates) {
        List<CrystalTraderConfig.WeightedModifier> pool = (List)this.modifierPools.get(poolName);
        if (pool != null && !pool.isEmpty()) {
            int count = min == max ? min : min + random.nextInt(Math.max(1, max - min + 1));
            if (!allowDuplicates) {
                count = Math.min(count, pool.size());
            }

            List<CrystalTraderConfig.WeightedModifier> available = new ArrayList(pool);
            List<String> result = new ArrayList();
            int totalWeight = 0;

            for(CrystalTraderConfig.WeightedModifier entry : available) {
                totalWeight += entry.getWeight();
            }

            for(int i = 0; i < count && !available.isEmpty() && totalWeight > 0; ++i) {
                int roll = random.nextInt(totalWeight);
                CrystalTraderConfig.WeightedModifier chosen = null;

                for(CrystalTraderConfig.WeightedModifier entry : available) {
                    roll -= entry.getWeight();
                    if (roll < 0) {
                        chosen = entry;
                        break;
                    }
                }

                if (chosen == null) {
                    chosen = (CrystalTraderConfig.WeightedModifier)available.get(available.size() - 1);
                }

                result.add(chosen.getModifier());
                if (!allowDuplicates) {
                    available.remove(chosen);
                    totalWeight -= chosen.getWeight();
                }
            }

            return result;
        } else {
            return Collections.emptyList();
        }
    }

    protected boolean isValid() {
        return this.tierPools != null && !this.tierPools.isEmpty();
    }

    public void addTradeEntry(int tier, CrystalTraderConfig.TradeEntry entry) {
        ((List)this.tierPools.computeIfAbsent(tier, (k) -> new ArrayList())).add(entry);
    }

    public static class TradeEntry {
        @Expose
        private String type = "item";
        @Expose
        private int weight;
        @Expose
        private ItemStack itemStack;
        @Expose
        private int minAmount;
        @Expose
        private int maxAmount;
        @Expose
        private int minCoinCost;
        @Expose
        private int maxCoinCost;
        @Expose
        private String poolId;

        public TradeEntry() {
        }

        public TradeEntry(ItemStack itemStack, int minAmount, int maxAmount, int minCoinCost, int maxCoinCost, int weight) {
            this.type = "item";
            this.itemStack = itemStack;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.minCoinCost = minCoinCost;
            this.maxCoinCost = maxCoinCost;
            this.weight = weight;
        }

        public String getType() {
            return this.type != null ? this.type : "item";
        }

        public int getWeight() {
            return this.weight;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }

        public int getMinAmount() {
            return this.minAmount;
        }

        public int getMaxAmount() {
            return this.maxAmount;
        }

        public int rollCoinCost(Random random, int fallback) {
            if (this.minCoinCost <= 0 && this.maxCoinCost <= 0) {
                return fallback;
            } else if (this.minCoinCost == this.maxCoinCost) {
                return this.minCoinCost;
            } else {
                int range = Math.max(1, this.maxCoinCost - this.minCoinCost + 1);
                return this.minCoinCost + random.nextInt(range);
            }
        }

        public String getPoolId() {
            return this.poolId;
        }
    }

    public static class PoolEntry {
        @Expose
        private String type = "item";
        @Expose
        private int weight = 1;
        @Expose
        private int minGreedTier = 0;
        @Expose
        private int maxGreedTier = -1;
        @Expose
        private int minCoinCost = 0;
        @Expose
        private int maxCoinCost = 0;
        @Expose
        private ItemStack itemStack;
        @Expose
        private CrystalTraderConfig.CatalystBuilder catalyst;
        @Expose
        private CrystalTraderConfig.InscriptionBuilder inscription;
        @Expose
        private CrystalTraderConfig.CrystalBuilder crystal;

        public PoolEntry() {
        }

        public String getType() {
            return this.type != null ? this.type : "item";
        }

        public int getWeight() {
            return this.weight;
        }

        public int getMinGreedTier() {
            return this.minGreedTier;
        }

        public int getMaxGreedTier() {
            return this.maxGreedTier;
        }

        public int rollCoinCost(Random random, int fallback) {
            if (this.minCoinCost <= 0 && this.maxCoinCost <= 0) {
                return fallback;
            } else if (this.minCoinCost == this.maxCoinCost) {
                return this.minCoinCost;
            } else {
                int range = Math.max(1, this.maxCoinCost - this.minCoinCost + 1);
                return this.minCoinCost + random.nextInt(range);
            }
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }

        public CrystalTraderConfig.CatalystBuilder getCatalyst() {
            return this.catalyst;
        }

        public CrystalTraderConfig.InscriptionBuilder getInscription() {
            return this.inscription;
        }

        public CrystalTraderConfig.CrystalBuilder getCrystal() {
            return this.crystal;
        }
    }

    public static class CatalystBuilder {
        @Expose
        private IntRoll size = IntRoll.ofUniform(0, 1);
        @Expose
        private String modifierPool;
        @Expose
        private int minModifiers = 0;
        @Expose
        private int maxModifiers = 0;
        @Expose
        private boolean isSuper = true;
        @Expose
        private boolean isGreedy = true;

        public CatalystBuilder() {
        }

        public int rollSize(RandomSource random) {
            return this.size.get(random);
        }
        public String getModifierPool() {
            return this.modifierPool;
        }
        public int getMinModifiers() {
            return this.minModifiers;
        }
        public int getMaxModifiers() {
            return this.maxModifiers;
        }
        public boolean isSuper() {
            return this.isSuper;
        }
        public boolean isGreedy() {
            return this.isGreedy;
        }
    }

    public static class InscriptionBuilder {
        @Expose
        private List<CrystalTraderConfig.RoomEntry> rooms = new ArrayList();
        @Expose
        private int minTime = 0;
        @Expose
        private int maxTime = 0;
        @Expose
        private String modifierPool;
        @Expose
        private int minModifiers = 0;
        @Expose
        private int maxModifiers = 0;
        @Expose
        private boolean isSuper = true;
        @Expose
        private Integer color;

        public InscriptionBuilder() {
        }

        public List<CrystalTraderConfig.RoomEntry> getRooms() {
            return this.rooms != null ? this.rooms : Collections.emptyList();
        }

        public int getMinTime() {
            return this.minTime;
        }

        public int getMaxTime() {
            return this.maxTime;
        }

        public String getModifierPool() {
            return this.modifierPool;
        }

        public int getMinModifiers() {
            return this.minModifiers;
        }

        public int getMaxModifiers() {
            return this.maxModifiers;
        }

        public boolean isSuper() {
            return this.isSuper;
        }

        public Integer getColor() {
            return this.color;
        }
    }

    public static class CrystalBuilder {
        @Expose
        private String modifierPool;
        @Expose
        private int minModifiers = 0;
        @Expose
        private int maxModifiers = 0;
        @Expose
        private String modifierPoolTwo;
        @Expose
        private int minModifiersTwo = 0;
        @Expose
        private int maxModifiersTwo = 0;
        @Expose
        private String modifierPoolThree;
        @Expose
        private int minModifiersThree = 0;
        @Expose
        private int maxModifiersThree = 0;

        public CrystalBuilder() {
        }
        public String getModifierPool() {
            return this.modifierPool;
        }
        public int getMinModifiers() {
            return this.minModifiers;
        }
        public int getMaxModifiers() {
            return this.maxModifiers;
        }
        public String getModifierPoolTwo() {
            return this.modifierPoolTwo;
        }
        public int getMinModifiersTwo() {
            return this.minModifiersTwo;
        }
        public int getMaxModifiersTwo() {
            return this.maxModifiersTwo;
        }
        public String getModifierPoolThree() {
            return this.modifierPoolThree;
        }
        public int getMinModifiersThree() {
            return this.minModifiersThree;
        }
        public int getMaxModifiersThree() {
            return this.maxModifiersThree;
        }
    }

    public static class RoomEntry {
        @Expose
        private String pool;
        @Expose
        private String roomType;
        @Expose
        private int count = 1;
        @Expose
        private int color = 16777215;

        public RoomEntry() {
        }

        public String getPool() {
            return this.pool;
        }

        public String getRoomType() {
            return this.roomType;
        }

        public int getCount() {
            return this.count;
        }

        public int getColor() {
            return this.color;
        }
    }

    public static class WeightedRoomPool {
        @Expose
        private String pool;
        @Expose
        private int weight = 1;
        @Expose
        private int model = 0;

        public WeightedRoomPool() {
        }

        public WeightedRoomPool(String pool, int weight, int model) {
            this.pool = pool;
            this.weight = weight;
            this.model = model;
        }

        public String getPool() {
            return this.pool;
        }

        public int getWeight() {
            return this.weight;
        }

        public int getModel() {
            return this.model;
        }
    }

    public static class WeightedModifier {
        @Expose
        private String modifier;
        @Expose
        private int weight = 1;

        public WeightedModifier() {
        }

        public WeightedModifier(String modifier, int weight) {
            this.modifier = modifier;
            this.weight = weight;
        }

        public String getModifier() {
            return this.modifier;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}

