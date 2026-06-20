package xyz.iwolfking.woldsvaults.configs;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.LevelEntryList;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.vhapi.api.lib.core.readers.CustomVaultConfigReader;
import xyz.iwolfking.vhapi.api.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;

public class HauntedBraziersConfig extends Config {
    @Expose
    private LevelEntryList<Entry> levels;

    public HauntedBraziersConfig() {
    }

    public String getName() {
        return "haunted_braziers";
    }

    public ResourceLocation getStackModifierPool(int level) {
        return ((Entry)this.levels.getForLevel(level).orElseThrow()).stackModifierPool;
    }

    public ResourceLocation getOverStackModifierPool(int level) {
        return ((Entry)this.levels.getForLevel(level).orElseThrow()).overStackModifierPool;
    }

    public ResourceLocation getOverStackLootTable(int level) {
        return ((Entry)this.levels.getForLevel(level).orElseThrow()).overStackLootTable;
    }

    protected void reset() {
        try (InputStream stream = this.getClass().getResourceAsStream("/default_configs/haunted_braziers.json")) {
            CustomVaultConfigReader<HauntedBraziersConfig> reader = new CustomVaultConfigReader<>();
            if(stream == null) {
                throw new IOException();
            }
            this.levels = reader.readCustomConfig("haunted_braziers", JsonUtils.parseJsonContentFromStream(stream), HauntedBraziersConfig.class).levels;
        } catch (IOException e) {
            System.out.println("Failed to read default Haunted Brazier config...");
            this.levels = new LevelEntryList<>();
            throw new RuntimeException(e);
        }

    }

    private static class Entry implements LevelEntryList.ILevelEntry {
        @Expose
        private final int level;
        @Expose
        private ResourceLocation stackModifierPool;
        @Expose
        private ResourceLocation overStackModifierPool;
        @Expose
        private ResourceLocation overStackLootTable;

        public Entry(int level, ResourceLocation stackModifierPool, ResourceLocation overStackModifierPool, ResourceLocation overStackLootTable) {
            this.level = level;
            this.stackModifierPool = stackModifierPool;
            this.overStackModifierPool = overStackModifierPool;
            this.overStackLootTable = overStackLootTable;
        }

        public int getLevel() {
            return this.level;
        }
    }
}
