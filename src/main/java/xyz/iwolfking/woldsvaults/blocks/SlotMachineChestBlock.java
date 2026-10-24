package xyz.iwolfking.woldsvaults.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import xyz.iwolfking.woldsvaults.container.SlotMachineContainer;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public class SlotMachineChestBlock extends Block {
    public SlotMachineChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            List<ItemStack> rewards = SlotMachineContainer.rollRewards(player);
            NetworkHooks.openGui(serverPlayer, new net.minecraft.world.MenuProvider() {
                @Override
                public net.minecraft.network.chat.Component getDisplayName() {
                    return new TextComponent("Slot Machine Chest");
                }

                @Override
                public SlotMachineContainer createMenu(int id, net.minecraft.world.entity.player.Inventory inventory,
                                                       Player menuPlayer) {
                    return SlotMachineContainer.createServer(id, inventory, pos, rewards);
                }
            }, buffer -> {
                buffer.writeBlockPos(pos);
                rewards.forEach(buffer::writeItem);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
