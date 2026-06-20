package xyz.iwolfking.woldsvaults.configs;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.objective.elixir.*;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.roll.IntRoll;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.vhapi.api.lib.core.readers.CustomVaultConfigReader;
import xyz.iwolfking.vhapi.api.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantedElixirConfig extends Config {
    @Expose
    private Map<Integer, Integer> elixirToSize;
    @Expose
    private Map<ResourceLocation, EntityPredicate> mobGroups;
    @Expose
    private LevelEntryList<Entry> entries;

    public EnchantedElixirConfig() {
    }

    public String getName() {
        return "enchanted_elixir";
    }

    protected void reset() {


        try (InputStream stream = this.getClass().getResourceAsStream("/default_configs/enchanted_elixir.json")) {
            CustomVaultConfigReader<EnchantedElixirConfig> reader = new CustomVaultConfigReader<>();
            if(stream == null) {
                throw new IOException();
            }
            EnchantedElixirConfig config = reader.readCustomConfig("enchanted_elixir", JsonUtils.parseJsonContentFromStream(stream), EnchantedElixirConfig.class);
            this.elixirToSize = config.elixirToSize;
            this.mobGroups = config.mobGroups;
            this.entries = config.entries;
        } catch (IOException e) {
            System.out.println("Failed to read default Enchanted Elixir config...");
            this.elixirToSize = new LinkedHashMap<>();

            for(int i = 0; i < 11; ++i) {
                this.elixirToSize.put(i, i);
            }

            this.mobGroups = new LinkedHashMap<>();
            this.mobGroups.put(ResourceLocation.withDefaultNamespace("main"), EntityPredicate.of("minecraft:zombie", true).orElseThrow());
            this.entries = new LevelEntryList<>();
            List<ElixirTask.Config<?>> tasks = new ArrayList<>();
            tasks.add(new ChestElixirTask.Config((new WeightedList<IntRoll>()).add(IntRoll.ofUniform(1, 10), 1), VaultChestType.WOODEN));
            tasks.add(new ChestElixirTask.Config((new WeightedList<IntRoll>()).add(IntRoll.ofUniform(1, 10), 1), VaultChestType.LIVING));
            tasks.add(new ChestElixirTask.Config((new WeightedList<IntRoll>()).add(IntRoll.ofUniform(1, 10), 1), VaultChestType.GILDED));
            tasks.add(new ChestElixirTask.Config((new WeightedList<IntRoll>()).add(IntRoll.ofUniform(1, 10), 1), VaultChestType.ORNATE));
            tasks.add(new CoinStacksElixirTask.Config((new WeightedList<IntRoll>()).add(IntRoll.ofUniform(1, 10), 1)));
            tasks.add(new OreElixirTask.Config((new WeightedList<IntRoll>()).add(IntRoll.ofUniform(1, 10), 1)));
            tasks.add(new MobElixirTask.Config((new WeightedList<IntRoll>()).add(IntRoll.ofUniform(1, 10), 1), ResourceLocation.withDefaultNamespace("main")));
            this.entries.add(new Entry(0, (new WeightedList<IntRoll>()).add(IntRoll.ofUniform(80, 100), 1), tasks));
            throw new RuntimeException(e);
        }

    }

    public int generateTarget(int level, RandomSource random) {
        Entry entry = this.entries.getForLevel(level).orElse(null);
        return entry == null ? 0 : entry.getTarget().getRandom(random).map((roll) -> roll.get(random)).orElse(0);
    }

    public List<ElixirTask> generateGoals(int level, RandomSource random) {
        Entry entry = this.entries.getForLevel(level).orElse( null);
        if (entry == null) {
            return new ArrayList<>();
        } else {
            IntBalancingRandom balancingRandom = new IntBalancingRandom(random);
            return entry.getTasks().stream().map((config) -> config.generate(balancingRandom)).collect(Collectors.toList());
        }
    }

    public static class Entry implements LevelEntryList.ILevelEntry {
        @Expose
        private int level;
        @Expose
        private WeightedList<IntRoll> target;
        @Expose
        private List<ElixirTask.Config<?>> tasks;

        public Entry(int level, WeightedList<IntRoll> target, List<ElixirTask.Config<?>> tasks) {
            this.level = level;
            this.target = target;
            this.tasks = tasks;
        }

        public int getLevel() {
            return this.level;
        }

        public WeightedList<IntRoll> getTarget() {
            return this.target;
        }

        public List<ElixirTask.Config<?>> getTasks() {
            return this.tasks;
        }
    }

    private static class IntBalancingRandom implements RandomSource {
        private final RandomSource delegate;
        private double balance = 0.0;

        protected IntBalancingRandom(RandomSource randomSource) {
            this.delegate = randomSource;
        }

        public int nextInt(int bound) {
            int balancedBound = bound + (int)this.balance;
            if (balancedBound <= 0) {
                this.balance += (double)bound / 2.0;
                return 0;
            } else {
                int randValue = this.delegate.nextInt(balancedBound);
                this.balance += (double)bound / 2.0 - (double)randValue;
                return randValue;
            }
        }

        public long nextLong() {
            return this.delegate.nextLong();
        }
    }
}
