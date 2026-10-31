package xyz.iwolfking.woldsvaults.prestige;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.prestige.core.PrestigePower;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class JewelSizePrestigePower extends PrestigePower {
    private float chance;

    public JewelSizePrestigePower(int unlockLevel, int learnPointCost, int regretCost, float chance){
        super(unlockLevel, learnPointCost, regretCost);
        this.chance = chance;
    }

    public JewelSizePrestigePower(){
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.chance, buffer);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.chance = (Float)Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.FLOAT.writeNbt(this.chance).ifPresent((tag) -> nbt.put("chance", tag));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.chance = (Float)Adapters.FLOAT.readNbt(nbt.get("chance")).orElseThrow();
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.FLOAT.writeJson(this.chance).ifPresent((element) -> json.add("chance", element));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.chance = (Float)Adapters.FLOAT.readJson(json.get("chance")).orElseThrow();
    }

    public float getChance() {
        return this.chance;
    }

}
