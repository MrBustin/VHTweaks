package xyz.iwolfking.woldsvaults.abilities;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class CritHitDamageTalent extends LearnableSkill {
    private float percentCritHitDamage;

    public CritHitDamageTalent(int unlockLevel, int learnPointCost, int regretPointCost, float percentCritHitDamage){
        super (unlockLevel, learnPointCost,regretPointCost);
        this.percentCritHitDamage = percentCritHitDamage;
    }

    public CritHitDamageTalent(){
    }


    public float getPercentCritHitDamage(){
        return this.percentCritHitDamage;
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.percentCritHitDamage, buffer);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.percentCritHitDamage = (Float)Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.FLOAT.writeNbt(this.percentCritHitDamage).ifPresent((tag) -> nbt.put("percentCritHitDamage", tag));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.percentCritHitDamage = (Float)Adapters.FLOAT.readNbt(nbt.get("percentCritHitDamage")).orElseThrow();
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.FLOAT.writeJson(this.percentCritHitDamage).ifPresent((element) -> json.add("percentCritHitDamage", element));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.percentCritHitDamage = (Float)Adapters.FLOAT.readJson(json.get("percentCritHitDamage")).orElseThrow();
    }
}
