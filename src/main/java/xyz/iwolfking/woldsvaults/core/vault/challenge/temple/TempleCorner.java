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
    public final BlockCuboid base;
    public final BlockCuboid statue;
    private boolean selected;
    private boolean revealed;

    public TempleCorner(BlockPos proxy, BlockCuboid rewards, BlockCuboid base, BlockCuboid statue) {
        this.proxy = proxy;
        this.rewards = rewards;
        this.base = base;
        this.statue = statue;
    }

    public static List<TempleCorner> defaults() {
        List<TempleCorner> corners = new ArrayList<>();
        for (int x : new int[]{1, -1}) {
            for (int z : new int[]{1, -1}) {
                BlockPos proxy = new BlockPos(x * 2, 0, z * 2);
                corners.add(defaultForProxy(proxy));
            }
        }
        return corners;
    }

    private static TempleCorner defaultForProxy(BlockPos proxy) {
        int baseMinX = proxy.getX() > 0 ? 16 : -20;
        int baseMinZ = proxy.getZ() > 0 ? 16 : -20;
        int statueMinX = proxy.getX() > 0 ? 15 : -21;
        int statueMinZ = proxy.getZ() > 0 ? 15 : -21;
        return new TempleCorner(proxy,
                BlockCuboid.of(baseMinX, -6, baseMinZ, baseMinX + 4, -3, baseMinZ + 4),
                BlockCuboid.of(baseMinX, -2, baseMinZ, baseMinX + 4, 1, baseMinZ + 4),
                BlockCuboid.of(statueMinX, 2, statueMinZ, statueMinX + 6, 10, statueMinZ + 6));
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
        BlockCuboid worldBase = this.base.offset(origin);
        BlockCuboid worldStatue = this.statue.offset(origin);
        BlockCuboid worldFoundation = BlockCuboid.of(this.base.getMinX(), this.base.getMinY() - 1, this.base.getMinZ(),
                this.base.getMaxX(), this.base.getMinY() - 1, this.base.getMaxZ()).offset(origin);
        if (!loaded(world, worldBase) || !loaded(world, worldStatue) || !loaded(world, worldFoundation)) return false;
        // Never remove the reward pocket, even if edited statue bounds overlap it.
        IZonedWorld.runWithBypass(world, true, () -> {
            // The footing shares the reward cuboid's top Y layer; clear the footing explicitly.
            removeRegion(world, origin, worldFoundation, false);
            removeRegion(world, origin, worldBase);
            removeRegion(world, origin, worldStatue);
        });
        world.playSound(null, origin, ModSounds.RAID_HATCH_OPEN, SoundSource.MASTER, 1.0F, 0.7F);
        this.revealed = true;
        return true;
    }

    private void removeRegion(ServerLevel world, BlockPos origin, BlockCuboid area) {
        removeRegion(world, origin, area, true);
    }

    private void removeRegion(ServerLevel world, BlockPos origin, BlockCuboid area, boolean protectRewards) {
        for (BlockPos pos : BlockPos.betweenClosed(area.getMinX(), area.getMinY(), area.getMinZ(),
                area.getMaxX(), area.getMaxY(), area.getMaxZ())) {
            removeRevealBlock(world, origin, pos, protectRewards);
        }
    }

    private void removeRevealBlock(ServerLevel world, BlockPos origin, BlockPos pos, boolean protectRewards) {
        if ((protectRewards && this.rewards.contains(pos.subtract(origin))) || pos.equals(origin)) return;
        var block = world.getBlockState(pos).getBlock();
        if (block instanceof VaultChestBlock || block instanceof CoinPileBlock) return;
        world.removeBlock(pos, false);
    }

    private static boolean loaded(ServerLevel world, BlockCuboid area) {
        return world.hasChunksAt(new BlockPos(area.getMinX(), area.getMinY(), area.getMinZ()),
                new BlockPos(area.getMaxX(), area.getMaxY(), area.getMaxZ()));
    }

    public CompoundTag writeNbt() {
        CompoundTag tag = new CompoundTag();
        Adapters.BLOCK_POS.writeNbt(this.proxy).ifPresent(value -> tag.put("proxy", value));
        Adapters.BLOCK_CUBOID.writeNbt(this.rewards).ifPresent(value -> tag.put("rewards", value));
        Adapters.BLOCK_CUBOID.writeNbt(this.base).ifPresent(value -> tag.put("baseRegion", value));
        Adapters.BLOCK_CUBOID.writeNbt(this.statue).ifPresent(value -> tag.put("statueRegion", value));
        tag.putBoolean("selected", this.selected);
        tag.putBoolean("revealed", this.revealed);
        return tag;
    }

    public static TempleCorner readNbt(CompoundTag tag) {
        BlockPos proxy = Adapters.BLOCK_POS.readNbt(tag.get("proxy")).orElseThrow();
        TempleCorner defaults = defaultForProxy(proxy);
        BlockCuboid rewards = Adapters.BLOCK_CUBOID.readNbt(tag.get("rewards")).orElse(defaults.rewards);
        BlockCuboid base = Adapters.BLOCK_CUBOID.readNbt(tag.get("baseRegion")).orElse(defaults.base);
        BlockCuboid statue = Adapters.BLOCK_CUBOID.readNbt(tag.get("statueRegion"))
                .orElseGet(() -> Adapters.BLOCK_CUBOID.readNbt(tag.get("statue")).orElse(defaults.statue));
        TempleCorner corner = new TempleCorner(proxy, rewards, base, statue);
        corner.selected = tag.getBoolean("selected");
        corner.revealed = tag.getBoolean("revealed");
        return corner;
    }
}
