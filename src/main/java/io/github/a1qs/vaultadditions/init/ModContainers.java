package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.container.LootStatueContainer;
import io.github.a1qs.vaultadditions.container.PlayerTraderContainer;
import io.github.a1qs.vaultadditions.container.RenameContainer;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.core.net.ArrayBitBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModContainers {
    public static MenuType<NBTElementContainer<PowerTree>> POWERS_TAB_CONTAINER;
    public static MenuType<LootStatueContainer> LOOT_STATUE_CONTAINER;
    public static MenuType<RenameContainer> RENAMING_CONTAINER;
    public static MenuType<PlayerTraderContainer> PLAYER_TRADER_CONTAINER;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<MenuType<?>> event) {
        POWERS_TAB_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
            PowerTree expertiseTree = new PowerTree();
            expertiseTree.readBits(ArrayBitBuffer.backing(buffer.readLongArray(), 0));
            return new NBTElementContainer<>(() -> POWERS_TAB_CONTAINER, windowId, inventory.player, expertiseTree);
        });

        LOOT_STATUE_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
            CompoundTag nbt = buffer.readNbt();
            return new LootStatueContainer(windowId, nbt == null ? new CompoundTag() : nbt);
        });

        RENAMING_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
            CompoundTag nbt = buffer.readNbt();
            return new RenameContainer(windowId, nbt == null ? new CompoundTag() : nbt);
        });

        PLAYER_TRADER_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
            Level world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            return new PlayerTraderContainer(windowId, world, pos, inventory);
        });


        event.getRegistry().register(POWERS_TAB_CONTAINER.setRegistryName("power_tab"));
        event.getRegistry().register(LOOT_STATUE_CONTAINER.setRegistryName("loot_statue_container"));
        event.getRegistry().register(RENAMING_CONTAINER.setRegistryName("renaming_container"));
        event.getRegistry().register(PLAYER_TRADER_CONTAINER.setRegistryName("player_trader_container"));
    }
}
