package xyz.iwolfking.woldsvaults.prestige;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.prestige.core.PrestigePower;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class CrystalSizePrestigePower extends PrestigePower {
    private float sizeAvoidChance;

    public CrystalSizePrestigePower() {
    }

    public CrystalSizePrestigePower(float sizeAvoidChance){
        this.sizeAvoidChance = sizeAvoidChance;
    }

    public float getSizeAvoidChance() {
        return sizeAvoidChance;
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.FLOAT.writeNbt(this.sizeAvoidChance).ifPresent((tag -> nbt.put("sizeAvoidChance", tag)));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt){
        super.readNbt(nbt);
        this.sizeAvoidChance = (float) Adapters.FLOAT.readNbt(nbt.get("sizeAvoidChance")).orElse(0.0f);
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.FLOAT.writeJson(this.sizeAvoidChance).ifPresent((e) -> json.add("sizeAvoidChance", e));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.sizeAvoidChance = Adapters.FLOAT.readJson(json.get("sizeAvoidChance")).orElse(0.0f);
    }

    public void writeBits(BitBuffer buffer){
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.sizeAvoidChance, buffer);
    }

    public void readBits(BitBuffer buffer){
        super.readBits(buffer);
        this.sizeAvoidChance = (float) Adapters.FLOAT.readBits(buffer).orElse(0.0f);
    }
}
