package xyz.iwolfking.woldsvaults.abilities;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class CritHitChanceTalent extends LearnableSkill {
    private float percentCritHitChance;

    public CritHitChanceTalent(int unlockLevel, int learnPointCost, int regretPointCost, float percentCritHitChance){
        super (unlockLevel, learnPointCost,regretPointCost);
        this.percentCritHitChance = percentCritHitChance;
    }

    public CritHitChanceTalent(){
    }


    public float getPercentCritHitChance(){
        return this.percentCritHitChance;
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.percentCritHitChance, buffer);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.percentCritHitChance = (Float)Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.FLOAT.writeNbt(this.percentCritHitChance).ifPresent((tag) -> nbt.put("percentCritHitChance", tag));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.percentCritHitChance = (Float)Adapters.FLOAT.readNbt(nbt.get("percentCritHitChance")).orElseThrow();
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.FLOAT.writeJson(this.percentCritHitChance).ifPresent((element) -> json.add("percentCritHitChance", element));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.percentCritHitChance = (Float)Adapters.FLOAT.readJson(json.get("percentCritHitChance")).orElseThrow();
    }
}
