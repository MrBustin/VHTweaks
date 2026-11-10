package xyz.iwolfking.woldsvaults.core.vault.challenge.temple.action;

import iskallia.vault.core.vault.challenge.action.ChallengeAction;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import iskallia.vault.core.vault.challenge.elite.EliteChallengeManager;
import iskallia.vault.core.vault.challenge.memory.MemoryRoomChallengeManager;
import iskallia.vault.core.vault.challenge.raid.RaidChallengeManager;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleChallengeManager;
import iskallia.vault.core.vault.challenge.xmark.XMarkChallengeManager;
import iskallia.vault.core.vault.modifier.modifier.NoLootModifier;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.core.world.processor.tile.MirrorTileProcessor;
import iskallia.vault.core.world.processor.tile.RotateTileProcessor;
import iskallia.vault.core.world.processor.tile.TranslateTileProcessor;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.core.world.template.DynamicTemplate;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

import static iskallia.vault.core.vault.Vault.SEED;
import static iskallia.vault.core.world.generator.GridGenerator.CELL_X;
import static iskallia.vault.core.world.generator.GridGenerator.CELL_Z;

public class TileRewardChallengeAction extends ChallengeAction<TileRewardChallengeAction.Config> {

    public TileRewardChallengeAction() {
        super(new Config());

    }

    public TileRewardChallengeAction(Config config) {
        super(config);
    }

    @Override
    public boolean onMerge(ChallengeAction<?> action) {
        if(action instanceof TileRewardChallengeAction other) {
            PartialTile a = this.getConfig().tile;
            PartialTile b = other.getConfig().tile;

            if(a.isSubsetOf(b) && b.isSubsetOf(a)) {
                this.getConfig().count += other.getConfig().count;
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivate(ServerLevel world, ChallengeManager manager, RandomSource random) {
        Vault theVault = ServerVaults.get(world).orElse(null);
        if (theVault == null) return;
        if (theVault.get(Vault.MODIFIERS).getModifiers().stream().anyMatch(modifier -> modifier instanceof NoLootModifier)) {
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
        } else if (manager instanceof MemoryRoomChallengeManager memory) {
            zone = memory.getZone().offset(manager.pos);
            zone = BlockCuboid.of(zone.getMinX(), zone.getMinY(), zone.getMinZ(),
                    zone.getMaxX(), zone.getMaxY() - 33, zone.getMaxZ());
        } else if (manager instanceof TempleChallengeManager temple) {
            zone = temple.getZone().offset(manager.pos);
            zone = BlockCuboid.of(zone.getMinX(), zone.getMinY() - 3, zone.getMinZ(),
                    zone.getMaxX(), zone.getMaxY() - 17, zone.getMaxZ()).grow(-5, 0, -5);
        } else {
            zone = manager.getZone().offset(manager.pos);
        }

        if (manager instanceof xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleChallengeManager) {
            int maxY = Math.min(zone.getMaxY(), manager.getPos().getY());
            if (zone.getMinY() > maxY || zone.getMinX() > zone.getMaxX() || zone.getMinZ() > zone.getMaxZ()) return;
            zone = BlockCuboid.of(zone.getMinX(), zone.getMinY(), zone.getMinZ(), zone.getMaxX(), maxY, zone.getMaxZ());
        }

        for(int j = 0; j < this.getConfig().count; j++) {
            for(int i = 0; i < 200; i++) {
                int x = random.nextInt(zone.getMaxX() - zone.getMinX() + 1) + zone.getMinX();
                int y = random.nextInt(zone.getMaxY() - zone.getMinY() + 1) + zone.getMinY();
                int z = random.nextInt(zone.getMaxZ() - zone.getMinZ() + 1) + zone.getMinZ();
                BlockPos pos = new BlockPos(x, y, z);

                if(world.getBlockState(pos).isAir() && world.getBlockState(pos.above()).isAir()
                        && world.getBlockState(pos.below()).isCollisionShapeFullBlock(world, pos)) {
                    DynamicTemplate template = new DynamicTemplate();
                    template.add(this.getConfig().tile.copy().setPos(pos));

                    PlacementSettings settings = new PlacementSettings(new ProcessorContext(null, random)).setFlags(Block.UPDATE_ALL);

                    ServerVaults.get(world).ifPresent(vault -> {
                        if(!(vault.get(Vault.WORLD).get(WorldManager.GENERATOR) instanceof GridGenerator gen)) {
                            return;
                        }

                        RegionPos region = RegionPos.ofBlockPos(pos, gen.get(CELL_X), gen.get(CELL_Z));

                        ChunkRandom chunkRandom = ChunkRandom.any();
                        chunkRandom.setRegionSeed(vault.get(SEED), region.getX(), region.getZ(), 1234567890);

                        JigsawTemplate room = (JigsawTemplate)gen.get(GridGenerator.LAYOUT).getAt(vault, region, chunkRandom, settings);
                        room.getConfigurator().accept(settings);
                        settings.getTileProcessors().removeIf(processor -> processor instanceof TranslateTileProcessor);
                        settings.getTileProcessors().removeIf(processor -> processor instanceof MirrorTileProcessor);
                        settings.getTileProcessors().removeIf(processor -> processor instanceof RotateTileProcessor);
                    });

                    IZonedWorld.runWithBypass(world, true, () -> {
                        template.place(world, settings);
                    });

                    break;
                }
            }
        }
    }

    @Override
    public Component getText() {
        Component name = this.getConfig().tile.getState().getBlock().asWhole()
                .map(block -> new ItemStack(block).getHoverName())
                .orElse(ItemStack.EMPTY.getHoverName());

        return new TextComponent("+" + this.getConfig().count)
                .append(new TextComponent(" "))
                .append(this.getConfig().name == null ? name : new TextComponent(this.getConfig().name))
                .setStyle(Style.EMPTY.withColor(this.getConfig().textColor));
    }

    public static class Config extends ChallengeAction.Config {
        public String name;
        public PartialTile tile;
        public int count;

        @Override
        public void writeBits(BitBuffer buffer) {
            super.writeBits(buffer);
            Adapters.UTF_8.asNullable().writeBits(this.name, buffer);
            Adapters.PARTIAL_TILE.writeBits(this.tile, buffer);
            Adapters.INT_SEGMENTED_3.writeBits(this.count, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            super.readBits(buffer);
            this.name = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
            this.tile = Adapters.PARTIAL_TILE.readBits(buffer).orElseThrow();
            this.count = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
        }

        @Override
        public Optional<CompoundTag> writeNbt() {
            return super.writeNbt().map(nbt -> {
                Adapters.UTF_8.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
                Adapters.PARTIAL_TILE.writeNbt(this.tile).ifPresent(tag -> nbt.put("tile", tag));
                Adapters.INT.writeNbt(this.count).ifPresent(tag -> nbt.put("count", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(CompoundTag nbt) {
            super.readNbt(nbt);
            this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
            this.tile = Adapters.PARTIAL_TILE.readNbt(nbt.get("tile")).orElseThrow();
            this.count = Adapters.INT.readNbt(nbt.get("count")).orElse(1);
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return super.writeJson().map(json -> {
                Adapters.UTF_8.writeJson(this.name).ifPresent(tag -> json.add("name", tag));
                Adapters.PARTIAL_TILE.writeJson(this.tile).ifPresent(tag -> json.add("tile", tag));
                Adapters.INT.writeJson(this.count).ifPresent(tag -> json.add("count", tag));
                return json;
            });
        }

        @Override
        public void readJson(JsonObject json) {
            super.readJson(json);
            this.name = Adapters.UTF_8.readJson(json.get("name")).orElse(null);
            this.tile = Adapters.PARTIAL_TILE.readJson(json.get("tile")).orElseThrow();
            this.count = Adapters.INT.readJson(json.get("count")).orElse(1);
        }
    }

}
