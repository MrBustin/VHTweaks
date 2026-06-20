package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.LevelEntryMap;

public class RaidPlaqueConfig extends Config {
    @Expose
    private LevelEntryMap<Integer> scoreToTier = new LevelEntryMap<>();

    public int getTier(int score) {
        return this.scoreToTier.getForLevel(score).orElse(1);
    }

    public LevelEntryMap<Integer> getScoreToTier() {
        return this.scoreToTier;
    }

    @Override
    protected void reset() {
        for (int i = 0; i < 8; ++i) {
            this.scoreToTier.put(Math.max(1, i * 10), i);
        }
    }

    @Override
    public String getName() {
        return "vaultadditions_raid_plaque_tiers";
    }
}
