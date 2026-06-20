package io.github.a1qs.vaultadditions.vault.skill.power;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class SkillPointIncreasePower extends LearnableSkill {
    private int pointIncrease;

    @Override
    public void onAdd(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent(serverPlayer -> {
            PlayerVaultStatsData data = PlayerVaultStatsData.get(serverPlayer.getLevel());
            data.addSkillPoints(serverPlayer, pointIncrease);
        });
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        buffer.writeInt(this.pointIncrease);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.pointIncrease = Adapters.INT.readBits(buffer).orElseThrow();
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            nbt.putInt("pointIncrease", this.pointIncrease);
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.pointIncrease = Adapters.INT.readNbt(nbt.get("pointIncrease")).orElseThrow();
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            json.addProperty("pointIncrease", this.pointIncrease);
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.pointIncrease = Adapters.INT.readJson(json.get("pointIncrease")).orElseThrow();
    }
}
