package xyz.iwolfking.woldsvaults.prestige;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.prestige.core.PrestigePower;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class ToolCapacityPrestigePower extends PrestigePower {
    private int capacityIncrease;

    public ToolCapacityPrestigePower() {
    }

    public ToolCapacityPrestigePower(int capacityIncrease) {
        this.capacityIncrease = capacityIncrease;
    }

    public int getCapacityIncrease() {
        return capacityIncrease;
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.INT.writeNbt(this.capacityIncrease).ifPresent((tag) -> nbt.put("capacityIncrease", tag));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.capacityIncrease = Adapters.INT.readNbt(nbt.get("capacityIncrease")).orElse(0);
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.INT.writeJson(this.capacityIncrease).ifPresent((e) -> json.add("capacityIncrease", e));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.capacityIncrease = Adapters.INT.readJson(json.get("capacityIncrease")).orElse(0);
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.INT.writeBits(this.capacityIncrease, buffer);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.capacityIncrease = Adapters.INT.readBits(buffer).orElse(0);
    }
}
