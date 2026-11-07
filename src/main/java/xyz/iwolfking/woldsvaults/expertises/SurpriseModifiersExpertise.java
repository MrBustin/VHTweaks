package xyz.iwolfking.woldsvaults.expertises;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.base.LearnableSkill;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class SurpriseModifiersExpertise extends LearnableSkill {
    private int modifierAmount;
    private float chance;

    public SurpriseModifiersExpertise() {
    }

    public SurpriseModifiersExpertise(int unlockLevel, int learnPointCost, int regretCost, int modifierAmount, float chance) {
        super(unlockLevel, learnPointCost, regretCost);
        this.modifierAmount = modifierAmount;
        this.chance = chance;
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.INT.writeBits(this.modifierAmount, buffer);
        Adapters.FLOAT.writeBits(this.chance, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.modifierAmount = Adapters.INT.readBits(buffer).orElseThrow();
        this.chance = Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            Adapters.INT.writeNbt(this.modifierAmount).ifPresent(tag -> nbt.put("modifierAmount", tag));
            Adapters.FLOAT.writeNbt(this.chance).ifPresent(tag -> nbt.put("chance", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.modifierAmount = Adapters.INT.readNbt(nbt.get("modifierAmount")).orElseThrow();
        this.chance = Adapters.FLOAT.readNbt(nbt.get("chance")).orElseThrow();
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.INT.writeJson(this.modifierAmount).ifPresent(element -> json.add("modifierAmount", element));
            Adapters.FLOAT.writeJson(this.chance).ifPresent(element -> json.add("chance", element));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.modifierAmount = Adapters.INT.readJson(json.get("modifierAmount")).orElseThrow();
        this.chance = Adapters.FLOAT.readJson(json.get("chance")).orElseThrow();
    }

    public int getModifierAmount() {
        return this.modifierAmount;
    }

    public float getChance(){
        return this.chance;
    }
}
