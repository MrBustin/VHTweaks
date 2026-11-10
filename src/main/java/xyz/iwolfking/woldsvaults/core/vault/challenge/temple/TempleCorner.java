package xyz.iwolfking.woldsvaults.core.vault.challenge.temple;

import iskallia.vault.block.CoinPileBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Controller-relative pocket and reveal bounds. Each proxy selects exactly one corner. */
public class TempleCorner {
    public final BlockPos proxy;
    public final BlockCuboid rewards;
    public final BlockCuboid statue;
    private boolean selected;
    private boolean revealed;

    public TempleCorner(BlockPos proxy, BlockCuboid rewards, BlockCuboid statue) {
        this.proxy = proxy;
        this.rewards = rewards;
        this.statue = statue;
    }

    public static List<TempleCorner> defaults() {
        List<TempleCorner> corners = new ArrayList<>();
        for (int x : new int[]{1, -1}) {
            for (int z : new int[]{1, -1}) {
                int minX = x > 0 ? 16 : -20;
                int minZ = z > 0 ? 16 : -20;
                corners.add(new TempleCorner(new BlockPos(x * 2, 0, z * 2),
                        BlockCuboid.of(minX, -6, minZ, minX + 4, -3, minZ + 4),
                        BlockCuboid.of(minX, -2, minZ, minX + 4, 10, minZ + 4)));
            }
        }
        return corners;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void select() {
        this.selected = true;
    }

    public String describe(ServerLevel world, BlockPos origin) {
        BlockCuboid area = this.rewards.offset(origin);
        if (!loaded(world, area)) return "Reveal corner (loading)";
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (BlockPos pos : BlockPos.betweenClosed(area.getMinX(), area.getMinY(), area.getMinZ(),
                area.getMaxX(), area.getMaxY(), area.getMaxZ())) {
            var block = world.getBlockState(pos).getBlock();
            if (block instanceof VaultChestBlock || block instanceof CoinPileBlock) {
                counts.merge(block.getName().getString(), 1, Integer::sum);
            }
        }
        return counts.isEmpty() ? "Reveal corner (no loot detected)" : "Reveal " + counts.entrySet().stream()
                .map(entry -> entry.getValue() + " x " + entry.getKey()).collect(Collectors.joining(", "));
    }

    public boolean reveal(ServerLevel world, BlockPos origin) {
        if (!this.selected || this.revealed) return true;
        BlockCuboid area = this.statue.offset(origin);
        if (!loaded(world, area)) return false;
        // Never remove the reward pocket, even if edited statue bounds overlap it.
        IZonedWorld.runWithBypass(world, true, () -> {
            for (BlockPos pos : BlockPos.betweenClosed(area.getMinX(), area.getMinY(), area.getMinZ(),
                    area.getMaxX(), area.getMaxY(), area.getMaxZ())) {
                if (this.rewards.contains(pos.subtract(origin)) || pos.equals(origin)) continue;
                var block = world.getBlockState(pos).getBlock();
                if (block instanceof VaultChestBlock || block instanceof CoinPileBlock) continue;
                world.removeBlock(pos, false);
            }
        });
        world.playSound(null, origin, ModSounds.RAID_HATCH_OPEN, SoundSource.MASTER, 1.0F, 0.7F);
        this.revealed = true;
        return true;
    }

    private static boolean loaded(ServerLevel world, BlockCuboid area) {
        return world.hasChunksAt(new BlockPos(area.getMinX(), area.getMinY(), area.getMinZ()),
                new BlockPos(area.getMaxX(), area.getMaxY(), area.getMaxZ()));
    }

    public CompoundTag writeNbt() {
        CompoundTag tag = new CompoundTag();
        Adapters.BLOCK_POS.writeNbt(this.proxy).ifPresent(value -> tag.put("proxy", value));
        Adapters.BLOCK_CUBOID.writeNbt(this.rewards).ifPresent(value -> tag.put("rewards", value));
        Adapters.BLOCK_CUBOID.writeNbt(this.statue).ifPresent(value -> tag.put("statue", value));
        tag.putBoolean("selected", this.selected);
        tag.putBoolean("revealed", this.revealed);
        return tag;
    }

    public static TempleCorner readNbt(CompoundTag tag) {
        TempleCorner corner = new TempleCorner(Adapters.BLOCK_POS.readNbt(tag.get("proxy")).orElseThrow(),
                Adapters.BLOCK_CUBOID.readNbt(tag.get("rewards")).orElseThrow(),
                Adapters.BLOCK_CUBOID.readNbt(tag.get("statue")).orElseThrow());
        corner.selected = tag.getBoolean("selected");
        corner.revealed = tag.getBoolean("revealed");
        return corner;
    }
}
