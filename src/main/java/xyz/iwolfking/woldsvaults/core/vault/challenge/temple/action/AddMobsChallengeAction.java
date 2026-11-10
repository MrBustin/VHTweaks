package xyz.iwolfking.woldsvaults.core.vault.challenge.temple.action;

import iskallia.vault.core.vault.challenge.action.ChallengeAction;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import iskallia.vault.core.vault.challenge.memory.MemoryRoomChallengeManager;
import iskallia.vault.core.vault.challenge.raid.RaidChallengeManager;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleChallengeManager;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.roll.IntRoll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class AddMobsChallengeAction extends ChallengeAction<AddMobsChallengeAction.Config> {

    int count;

    public AddMobsChallengeAction() {
        super(new Config());

    }

    public AddMobsChallengeAction(Config config) {
        super(config);
    }

    @Override
    public boolean onPopulate(RandomSource random) {
        if(!super.onPopulate(random)) {
            this.count = this.getConfig().count.get(random);
            return false;
        }

        return true;
    }

    @Override
    public boolean onMerge(ChallengeAction<?> action) {
        if(action instanceof AddMobsChallengeAction other) {
            if(this.getConfig().entity.isSubsetOf(other.getConfig().entity)
                    && other.getConfig().entity.isSubsetOf(this.getConfig().entity)) {
                this.count += other.count;
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivate(ServerLevel world, ChallengeManager manager, RandomSource random) {
        super.onActivate(world, manager, random);

        if(manager instanceof RaidChallengeManager raid) {
            raid.getSpawner().addSpawn(this.getConfig().entity, this.count, this.getConfig().spawner);
        } else if (manager instanceof MemoryRoomChallengeManager memory) {
            memory.getSpawner().addSpawn(this.getConfig().entity, this.count, this.getConfig().spawner);
        } else if (manager instanceof TempleChallengeManager temple) {
            temple.getSpawner().addSpawn(this.getConfig().entity, this.count, this.getConfig().spawner);
        }
    }

    @Override
    public Component getText() {
        ResourceLocation id = this.getConfig().entity.getId();
        Component name = this.getConfig().name == null ? null : new TextComponent(this.getConfig().name);

        if(name == null) {
            EntityType<?> type = id == null ? null : EntityType.byString(id.toString()).orElse(null);
            name = type == null ? new TextComponent("Unknown") : type.getDescription();
        }

        return new TextComponent("+" + this.count + " ").append(name).append(this.count == 1 ? " Spawn" : " Spawns")
                .setStyle(Style.EMPTY.withColor(this.getConfig().textColor));
    }

    public static class Config extends ChallengeAction.Config {
        private String name;
        private PartialEntity entity;
        private String spawner;
        private IntRoll count;

        @Override
        public void writeBits(BitBuffer buffer) {
            super.writeBits(buffer);
            Adapters.UTF_8.asNullable().writeBits(this.name, buffer);
            Adapters.PARTIAL_ENTITY.writeBits(this.entity, buffer);
            Adapters.UTF_8.writeBits(this.spawner, buffer);
            Adapters.INT_ROLL.writeBits(this.count, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            super.readBits(buffer);
            this.name = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
            this.entity = Adapters.PARTIAL_ENTITY.readBits(buffer).orElseThrow();
            this.spawner = Adapters.UTF_8.readBits(buffer).orElseThrow();
            this.count = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
        }

        @Override
        public Optional<CompoundTag> writeNbt() {
            return super.writeNbt().map(nbt -> {
                Adapters.UTF_8.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
                Adapters.PARTIAL_ENTITY.writeNbt(this.entity).ifPresent(tag -> nbt.put("entity", tag));
                Adapters.UTF_8.writeNbt(this.spawner).ifPresent(tag -> nbt.put("spawner", tag));
                Adapters.INT_ROLL.writeNbt(this.count).ifPresent(tag -> nbt.put("count", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(CompoundTag nbt) {
            super.readNbt(nbt);
            this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
            this.entity = Adapters.PARTIAL_ENTITY.readNbt(nbt.get("entity")).orElseThrow();
            this.spawner = Adapters.UTF_8.readNbt(nbt.get("spawner")).orElseThrow();
            this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElse(IntRoll.ofConstant(1));
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return super.writeJson().map(json -> {
                Adapters.UTF_8.writeJson(this.name).ifPresent(tag -> json.add("name", tag));
                Adapters.PARTIAL_ENTITY.writeJson(this.entity).ifPresent(tag -> json.add("entity", tag));
                Adapters.UTF_8.writeJson(this.spawner).ifPresent(tag -> json.add("spawner", tag));
                Adapters.INT_ROLL.writeJson(this.count).ifPresent(tag -> json.add("count", tag));
                return json;
            });
        }

        @Override
        public void readJson(JsonObject json) {
            super.readJson(json);
            this.name = Adapters.UTF_8.readJson(json.get("name")).orElse(null);
            this.entity = Adapters.PARTIAL_ENTITY.readJson(json.get("entity")).orElseThrow();
            this.spawner = Adapters.UTF_8.readJson(json.get("spawner")).orElseThrow();
            this.count = Adapters.INT_ROLL.readJson(json.get("count")).orElse(IntRoll.ofConstant(1));
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        if(!this.isPopulated()) return;
        Adapters.INT_SEGMENTED_3.writeBits(this.count, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        if(!this.isPopulated()) return;
        this.count = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            if(!this.isPopulated()) return nbt;
            Adapters.INT.writeNbt(this.count).ifPresent(tag -> nbt.put("count", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        if(!this.isPopulated()) return;
        this.count = Adapters.INT.readNbt(nbt.get("count")).orElseThrow();
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            if(!this.isPopulated()) return json;
            Adapters.INT.writeJson(this.count).ifPresent(tag -> json.add("count", tag));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        if(!this.isPopulated()) return;
        this.count = Adapters.INT.readJson(json.get("count")).orElseThrow();
    }

}
