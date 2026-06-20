package xyz.iwolfking.woldsvaults.api.inventory;

import iskallia.vault.container.base.SimpleSidedContainer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class SimpleOversizedSidedContainer extends SimpleSidedContainer {
    private final Map<Direction, Set<Integer>> cachedSidedSlots = new HashMap();

    public SimpleOversizedSidedContainer(int size) {
        super(size);
        this.cacheSlots();
    }



    private void cacheSlots() {
        IntStream.range(0, this.getContainerSize()).forEach((slot) -> {
            this.getAccessibleSlots(slot).forEach((dir) -> {
                ((Set)this.cachedSidedSlots.computeIfAbsent(dir, (side) -> {
                    return new HashSet();
                })).add(slot);
            });
        });
    }

    public abstract List<Direction> getAccessibleSlots(int var1);

    public int[] getSlotsForFace(Direction side) {
        return ((Stream)Optional.ofNullable((Set)this.cachedSidedSlots.get(side)).map(Collection::stream).orElse(Stream.empty())).mapToInt(x-> (int) x).toArray();
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return ((Set)this.cachedSidedSlots.getOrDefault(side, Collections.emptySet())).contains(slot);
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return ((Set)this.cachedSidedSlots.getOrDefault(side, Collections.emptySet())).contains(slot);
    }

    @Override
    public int getMaxStackSize() {
        return 2147483582;
    }
}
