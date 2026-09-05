package xyz.iwolfking.woldsvaults.talent;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class VoltaicDistanceTalent extends LearnableSkill {
    private float shockingDistanceIncrease;

    public VoltaicDistanceTalent(int unlockLevel, int learnPointCost, int regretPointCost, float shockingDistanceIncrease) {
        super(unlockLevel,learnPointCost,regretPointCost);
        this.shockingDistanceIncrease = shockingDistanceIncrease;
    }

    public VoltaicDistanceTalent() {
    }

    public float getShockingDistanceIncrease() {
        return this.shockingDistanceIncrease;
    }

    public static float getShockingDistanceIncrease(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return 0.0F;
        } else {
            TalentTree talents = PlayerTalentsData.get(serverPlayer.getLevel()).getTalents(serverPlayer);
            float distanceIncrease = 0.0F;

            for(VoltaicDistanceTalent talent : talents.getAll(VoltaicDistanceTalent.class, Skill::isUnlocked)) {
                distanceIncrease += talent.getShockingDistanceIncrease();
            }

            return distanceIncrease;
        }
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.shockingDistanceIncrease, buffer);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.shockingDistanceIncrease = (Float)Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.FLOAT.writeNbt(this.shockingDistanceIncrease).ifPresent((tag) -> nbt.put("shockingDistanceIncrease", tag));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.shockingDistanceIncrease = (Float)Adapters.FLOAT.readNbt(nbt.get("shockingDistanceIncrease")).orElseThrow();
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.FLOAT.writeJson(this.shockingDistanceIncrease).ifPresent((element) -> json.add("shockingDistanceIncrease", element));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.shockingDistanceIncrease = (Float)Adapters.FLOAT.readJson(json.get("shockingDistanceIncrease")).orElseThrow();
    }
}
