package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.data.PlayerPowersData;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public class PowerConfig extends Config {
    @Expose
    private PowerTree tree;

    private boolean mergeScheduled = false;

    @Override
    protected void onLoad(@Nullable Config oldConfigInstance) {
        if (this.tree == null) {
            VaultAdditions.LOGGER.warn("[PowerConfig] No tree found, creating a new one.");
            this.tree = new PowerTree();
        }
        mergeScheduled = true;
    }

    public boolean isMergeScheduled() {
        if (mergeScheduled) {
            mergeScheduled = false;
            return true;
        }
        return false;
    }

    public PowerTree getTree() {
        return this.tree;
    }

    public String getName() {
        return "vaultadditions_power";
    }

    @Override
    protected boolean isValid() {
        return this.tree != null;
    }

    @Override
    protected void reset() {}
}

