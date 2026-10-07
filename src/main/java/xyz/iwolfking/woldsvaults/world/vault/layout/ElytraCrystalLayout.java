package xyz.iwolfking.woldsvaults.world.vault.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Optional;

public class ElytraCrystalLayout extends CrystalLayout {
    public static final int DEFAULT_ROOM_COUNT = 6;
    private int roomCount = DEFAULT_ROOM_COUNT;

    public ElytraCrystalLayout() {
    }

    public ElytraCrystalLayout(int roomCount) {
        this.roomCount = Math.max(1, roomCount);
    }

    @Override
    public void configure(Vault vault, RandomSource random, String sigil) {
        vault.getOptional(Vault.WORLD)
                .map(world -> world.get(WorldManager.GENERATOR))
                .filter(GridGenerator.class::isInstance)
                .map(GridGenerator.class::cast)
                .ifPresent(generator -> generator.set(GridGenerator.LAYOUT, new ElytraVaultLayout(this.roomCount)));
    }

    @Override
    public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time, int level) {
        tooltip.add(new TextComponent("Layout: ")
                .append(new TextComponent("Elytra Course").withStyle(ChatFormatting.LIGHT_PURPLE)));
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("room_count", this.roomCount);
        return Optional.of(nbt);
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        this.roomCount = Math.max(1, nbt.contains("room_count")
                ? nbt.getInt("room_count") : DEFAULT_ROOM_COUNT);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        JsonObject json = new JsonObject();
        json.addProperty("room_count", this.roomCount);
        return Optional.of(json);
    }

    @Override
    public void readJson(JsonObject json) {
        this.roomCount = Math.max(1, json.has("room_count")
                ? json.get("room_count").getAsInt() : DEFAULT_ROOM_COUNT);
    }
}
