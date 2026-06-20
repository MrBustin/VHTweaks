package io.github.a1qs.vaultadditions.vault;

import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.PowerPointMessage;
import iskallia.vault.util.NetcodeUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class PlayerAdditionalVaultStats implements INBTSerializable<CompoundTag> {
    private final UUID uuid;
    private int unspentPowerPoints;
    private int totalSpentPowerPoints;

    public PlayerAdditionalVaultStats(UUID uuid) {
        this.uuid = uuid;
    }

    public int getUnspentPowerPoints() {
        return this.unspentPowerPoints;
    }

    public int getTotalSpentPowerPoints() {
        return this.totalSpentPowerPoints;
    }

    public void spendPowerPoints(MinecraftServer server, int amount) {
        this.unspentPowerPoints -= amount;
        this.totalSpentPowerPoints += amount;
        this.sync(server);
    }

    public PlayerAdditionalVaultStats reset(MinecraftServer server) {
        this.unspentPowerPoints = 0;
        this.sync(server);
        return this;
    }

    public PlayerAdditionalVaultStats resetPower(MinecraftServer server) {
        this.unspentPowerPoints = 0;
        this.totalSpentPowerPoints = 0;
        this.sync(server);
        return this;
    }

    public void setTotalSpentPowerPoints(int totalSpentExpertisePoints) {
        this.totalSpentPowerPoints = totalSpentExpertisePoints;
    }

    public PlayerAdditionalVaultStats addPowerPoints(int amount) {
        this.unspentPowerPoints += amount;
        return this;
    }

    public PlayerAdditionalVaultStats setPowerPoints(int amount) {
        this.unspentPowerPoints = amount;
        this.sync(ServerLifecycleHooks.getCurrentServer());
        return this;
    }

    public PlayerAdditionalVaultStats resetAndReturnPowerPoints() {
        this.unspentPowerPoints = this.unspentPowerPoints + this.totalSpentPowerPoints;
        this.totalSpentPowerPoints = 0;
        return this;
    }

    public PlayerAdditionalVaultStats refundPowerPoints(int amount) {
        this.unspentPowerPoints += amount;
        this.totalSpentPowerPoints -= amount;
        return this;
    }

    public void sync(MinecraftServer server) {
        NetcodeUtils.runIfPresent(server, this.uuid, (player) -> {
            ModNetwork.CHANNEL.sendTo(new PowerPointMessage(this.unspentPowerPoints), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        });
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("unspentPowerPoints", this.unspentPowerPoints);
        nbt.putInt("totalSpentPowerPoints", this.totalSpentPowerPoints);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.unspentPowerPoints = nbt.getInt("unspentPowerPoints");
        this.totalSpentPowerPoints = nbt.getInt("totalSpentPowerPoints");
    }
}
