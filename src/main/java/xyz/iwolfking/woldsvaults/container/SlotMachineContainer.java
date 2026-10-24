package xyz.iwolfking.woldsvaults.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import xyz.iwolfking.woldsvaults.init.ModContainers;

import java.util.ArrayList;
import java.util.List;

public class SlotMachineContainer extends AbstractContainerMenu {
    public static final int ROLL_TICKS = 115;
    private static final List<ItemStack> REWARD_POOL = List.of(
            new ItemStack(Items.IRON_INGOT, 8),
            new ItemStack(Items.GOLD_INGOT, 6),
            new ItemStack(Items.EMERALD, 4),
            new ItemStack(Items.DIAMOND, 2),
            new ItemStack(Items.EXPERIENCE_BOTTLE, 8),
            new ItemStack(Items.GOLDEN_APPLE, 1)
    );

    private final BlockPos pos;
    private final List<ItemStack> rewards;
    private final long openedAt;
    private boolean claimed;

    private SlotMachineContainer(int id, Inventory inventory, BlockPos pos, List<ItemStack> rewards) {
        super(ModContainers.SLOT_MACHINE_CONTAINER, id);
        this.pos = pos;
        this.rewards = rewards;
        this.openedAt = inventory.player.level.getGameTime();
    }

    public static List<ItemStack> rollRewards(Player player) {
        List<ItemStack> rewards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            rewards.add(REWARD_POOL.get(player.getRandom().nextInt(REWARD_POOL.size())).copy());
        }
        return rewards;
    }

    public static SlotMachineContainer createServer(int id, Inventory inventory, BlockPos pos, List<ItemStack> rewards) {
        return new SlotMachineContainer(id, inventory, pos, new ArrayList<>(rewards));
    }

    public static SlotMachineContainer fromNetwork(int id, Inventory inventory, FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        List<ItemStack> rewards = new ArrayList<>();
        for (int i = 0; i < 3; i++) rewards.add(buffer.readItem());
        return new SlotMachineContainer(id, inventory, pos, rewards);
    }

    public List<ItemStack> getRewards() {
        return this.rewards;
    }

    public static List<ItemStack> getRewardPool() {
        return REWARD_POOL;
    }

    public void claim(ServerPlayer player) {
        if (this.claimed || player.level.getGameTime() - this.openedAt < ROLL_TICKS) return;
        this.claimed = true;
        for (ItemStack reward : this.rewards) {
            ItemStack stack = reward.copy();
            if (!player.getInventory().add(stack) && !stack.isEmpty()) player.drop(stack, false);
        }
        player.containerMenu.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D
                && player.level.getBlockState(this.pos).is(ModBlocks.SLOT_MACHINE_CHEST.get());
    }
}
