package xyz.iwolfking.woldsvaults.core.vault.challenge.temple.action;

import iskallia.vault.core.vault.challenge.action.ChallengeAction;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import iskallia.vault.core.vault.challenge.elite.EliteChallengeManager;
import iskallia.vault.core.vault.challenge.memory.MemoryRoomChallengeManager;
import iskallia.vault.core.vault.challenge.raid.RaidChallengeManager;
import iskallia.vault.core.vault.challenge.xmark.XMarkChallengeManager;
import iskallia.vault.core.vault.modifier.modifier.NoLootModifier;
import iskallia.vault.core.world.data.item.PartialStack;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.entity.entity.FloatingItemEntity;
import iskallia.vault.util.LootInitialization;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class FloatingItemRewardChallengeAction extends ChallengeAction<FloatingItemRewardChallengeAction.Config> {

    public FloatingItemRewardChallengeAction() {
        super(new Config());

    }

    public FloatingItemRewardChallengeAction(Config config) {
        super(config);
    }

    @Override
    public boolean onMerge(ChallengeAction<?> action) {
        if(action instanceof FloatingItemRewardChallengeAction other) {
            PartialStack a = PartialStack.of(this.getConfig().item);
            PartialStack b = PartialStack.of(other.getConfig().item);

            if(a.isSubsetOf(b) && b.isSubsetOf(a)) {
                this.getConfig().item.grow(other.getConfig().item.getCount());
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivate(ServerLevel world, ChallengeManager manager, RandomSource random) {
        Vault vault = ServerVaults.get(world).orElse(null);
        if (vault == null) return;
        if (vault.get(Vault.MODIFIERS).getModifiers().stream().anyMatch(modifier -> modifier instanceof NoLootModifier)) {
            return;
        }
        super.onActivate(world, manager, random);
        BlockCuboid zone;

        if(manager instanceof RaidChallengeManager raid) {
            zone = raid.getZone().offset(manager.pos);
            zone = BlockCuboid.of(zone.getMinX(), zone.getMinY(), zone.getMinZ(),
                    zone.getMaxX(), zone.getMaxY() - 30, zone.getMaxZ());
        } else if(manager instanceof XMarkChallengeManager xmark) {
            zone = xmark.getZone().offset(manager.pos);
            zone = BlockCuboid.of(zone.getMinX(), zone.getMinY(), zone.getMinZ(),
                    zone.getMaxX(), zone.getMaxY() - 35, zone.getMaxZ());
        } else if(manager instanceof EliteChallengeManager elite) {
            zone = elite.getZone();
            zone = BlockCuboid.of(zone.getMinX() + 10, zone.getMinY() + 10, zone.getMinZ() + 10,
                    zone.getMaxX() - 10, zone.getMaxY() - 10, zone.getMaxZ() - 10);
        } else if(manager instanceof MemoryRoomChallengeManager memory) {
            zone = memory.getZone().offset(manager.pos);
            zone = BlockCuboid.of(zone.getMinX(), zone.getMinY(), zone.getMinZ(),
                    zone.getMaxX(), zone.getMaxY() - 25, zone.getMaxZ());
        } else {
            zone = manager.getZone().offset(manager.pos);
        }

        if (manager instanceof xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleChallengeManager) {
            int maxY = Math.min(zone.getMaxY(), manager.getPos().getY());
            if (zone.getMinY() > maxY || zone.getMinX() > zone.getMaxX() || zone.getMinZ() > zone.getMaxZ()) return;
            zone = BlockCuboid.of(zone.getMinX(), zone.getMinY(), zone.getMinZ(), zone.getMaxX(), maxY, zone.getMaxZ());
        }

        for(int j = 0; j < this.getConfig().item.getCount(); j++) {
            for (int i = 0; i < 200; i++) {
                int x = random.nextInt(zone.getMaxX() - zone.getMinX() + 1) + zone.getMinX();
                int y = random.nextInt(zone.getMaxY() - zone.getMinY() + 1) + zone.getMinY();
                int z = random.nextInt(zone.getMaxZ() - zone.getMinZ() + 1) + zone.getMinZ();
                BlockPos pos = new BlockPos(x, y, z);

                if (world.getBlockState(pos).isAir() &&
                        world.getBlockState(pos.above()).isAir() &&
                        world.getBlockState(pos.below()).isAir()) {
                    ItemStack stack = this.getConfig().item.copy();
                    stack = LootInitialization.initializeVaultLoot(stack, vault, pos);
                    stack.setCount(1);
                    FloatingItemEntity entity = new FloatingItemEntity(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
                    entity.setColor(this.getConfig().color1, this.getConfig().color2);
                    world.addFreshEntity(entity);
                    break;
                }
            }
        }
    }

    @Override
    public Component getText() {
        return new TextComponent("+" + this.getConfig().item.getCount())
                .append(new TextComponent(" "))
                .append(this.getConfig().name == null ? this.getConfig().item.getHoverName()
                        : new TextComponent(this.getConfig().name))
                .setStyle(Style.EMPTY.withColor(this.getConfig().textColor));
    }

    public static class Config extends ChallengeAction.Config {
        public String name;
        public ItemStack item;
        public int color1;
        public int color2;

        @Override
        public void writeBits(BitBuffer buffer) {
            super.writeBits(buffer);
            Adapters.UTF_8.asNullable().writeBits(this.name, buffer);
            Adapters.ITEM_STACK.writeBits(this.item, buffer);
            Adapters.INT.writeBits(this.color1, buffer);
            Adapters.INT.writeBits(this.color2, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            super.readBits(buffer);
            this.name = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
            this.item = Adapters.ITEM_STACK.readBits(buffer).orElseThrow();
            this.color1 = Adapters.INT.readBits(buffer).orElseThrow();
            this.color2 = Adapters.INT.readBits(buffer).orElseThrow();
        }

        @Override
        public Optional<CompoundTag> writeNbt() {
            return super.writeNbt().map(nbt -> {
                Adapters.UTF_8.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
                Adapters.ITEM_STACK.writeNbt(this.item).ifPresent(tag -> nbt.put("item", tag));
                Adapters.INT.writeNbt(this.color1).ifPresent(tag -> nbt.put("color1", tag));
                Adapters.INT.writeNbt(this.color2).ifPresent(tag -> nbt.put("color2", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(CompoundTag nbt) {
            super.readNbt(nbt);
            this.name = Adapters.UTF_8.asNullable().readNbt(nbt.get("name")).orElse(null);
            this.item = Adapters.ITEM_STACK.readNbt(nbt.get("item")).orElseThrow();
            this.color1 = Adapters.INT.readNbt(nbt.get("color1")).orElse(0xFFFFFFFF);
            this.color2 = Adapters.INT.readNbt(nbt.get("color2")).orElse(0xFFFFFFFF);
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return super.writeJson().map(json -> {
                Adapters.UTF_8.writeJson(this.name).ifPresent(tag -> json.add("name", tag));
                Adapters.ITEM_STACK.writeJson(this.item).ifPresent(tag -> json.add("item", tag));
                Adapters.INT.writeJson(this.color1).ifPresent(tag -> json.add("color1", tag));
                Adapters.INT.writeJson(this.color2).ifPresent(tag -> json.add("color2", tag));
                return json;
            });
        }

        @Override
        public void readJson(JsonObject json) {
            super.readJson(json);
            this.name = Adapters.UTF_8.asNullable().readJson(json.get("name")).orElse(null);
            this.item = Adapters.ITEM_STACK.readJson(json.get("item")).orElseThrow();
            this.color1 = Adapters.INT.readJson(json.get("color1")).orElse(0xFFFFFFFF);
            this.color2 = Adapters.INT.readJson(json.get("color2")).orElse(0xFFFFFFFF);
        }
    }

}
