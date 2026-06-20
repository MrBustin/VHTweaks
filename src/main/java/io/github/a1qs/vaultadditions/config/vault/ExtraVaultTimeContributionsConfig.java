package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;

public class ExtraVaultTimeContributionsConfig extends Config {
    @Expose public int MAX_EXTRA_PLAYER_TICKS;
    @Expose public int MAX_EXTRA_SERVER_TICKS;
    @Expose public int CONTRIBUTIONS_UNTIL_TIME_INCREASE_PLAYER;
    @Expose public int CONTRIBUTIONS_UNTIL_TIME_INCREASE_SERVER;
    @Expose public int TICKS_PER_EFFECTIVE_CONTRIBUTION_PLAYER;
    @Expose public int TICKS_PER_EFFECTIVE_CONTRIBUTION_SERVER;

    @Override
    public String getName() {
        return "vaultadditions_extra_vault_time_contributions";
    }

    @Override
    protected void reset() {
        MAX_EXTRA_PLAYER_TICKS = 3000;
        MAX_EXTRA_SERVER_TICKS = 3000;
        CONTRIBUTIONS_UNTIL_TIME_INCREASE_PLAYER = 250;
        CONTRIBUTIONS_UNTIL_TIME_INCREASE_SERVER = 2500;
        TICKS_PER_EFFECTIVE_CONTRIBUTION_PLAYER = 200;
        TICKS_PER_EFFECTIVE_CONTRIBUTION_SERVER = 200;
    }

    public int getServerCappedTicks(int contributions) {
        int ticks = (contributions / CONTRIBUTIONS_UNTIL_TIME_INCREASE_SERVER) * TICKS_PER_EFFECTIVE_CONTRIBUTION_SERVER;
        return Math.min(ticks, MAX_EXTRA_SERVER_TICKS);
    }

    public int getPlayerCappedTicks(int contributions) {
        int ticks = (contributions / CONTRIBUTIONS_UNTIL_TIME_INCREASE_PLAYER) * TICKS_PER_EFFECTIVE_CONTRIBUTION_PLAYER;
        return Math.min(ticks, MAX_EXTRA_PLAYER_TICKS);
    }

    public int getServerCappedSeconds(int contributions) {
        return getServerCappedTicks(contributions) / 20;
    }

    public int getPlayerCappedSeconds(int contributions) {
        return getPlayerCappedTicks(contributions) / 20;
    }
}
