package xyz.iwolfking.woldsvaults.prestige;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.prestige.core.PrestigePower;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class GearSealerPrestigePower extends PrestigePower {
    private float sealChance;

    public GearSealerPrestigePower() {
    }

    public GearSealerPrestigePower(float sealChance){
        this.sealChance = sealChance;
    }

    public float getSealChance(){
        return sealChance;
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.FLOAT.writeNbt(this.sealChance).ifPresent((tag -> nbt.put("sealChance", tag)));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt){
        super.readNbt(nbt);
        this.sealChance = (float) Adapters.FLOAT.readNbt(nbt.get("sealChance")).orElse(0.0f);
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.FLOAT.writeJson(this.sealChance).ifPresent((e) -> json.add("sealChance", e));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.sealChance = Adapters.FLOAT.readJson(json.get("sealChance")).orElse(0.0f);
    }

    public void writeBits(BitBuffer buffer){
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.sealChance, buffer);
    }

    public void readBits(BitBuffer buffer){
        super.readBits(buffer);
        this.sealChance = (float) Adapters.FLOAT.readBits(buffer).orElse(0.0f);
    }
}
