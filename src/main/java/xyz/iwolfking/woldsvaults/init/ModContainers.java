package xyz.iwolfking.woldsvaults.init;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.core.net.ArrayBitBuffer;
import xyz.iwolfking.woldsvaults.blocks.containers.LootStatueContainer;
import xyz.iwolfking.woldsvaults.blocks.containers.PlayerTraderContainer;
import xyz.iwolfking.woldsvaults.blocks.containers.RenameContainer;
import xyz.iwolfking.woldsvaults.blocks.containers.VaultSalvagerContainer;
import xyz.iwolfking.woldsvaults.client.menu.PowerTree;
import xyz.iwolfking.woldsvaults.items.scavenger_pouch.menu.ScavengerPouchContainer;

public class ModContainers {

    public static MenuType<VaultSalvagerContainer> VAULT_SALVAGER_CONTAINER;
    public static MenuType<ScavengerPouchContainer> SCAVENGER_POUCH_CONTAINER;
    public static MenuType<NBTElementContainer<PowerTree>> POWERS_TAB_CONTAINER;
    public static MenuType<LootStatueContainer> LOOT_STATUE_CONTAINER;
    public static MenuType<RenameContainer> RENAMING_CONTAINER;
    public static MenuType<PlayerTraderContainer> PLAYER_TRADER_CONTAINER;

    public static void register(RegistryEvent.Register<MenuType<?>> event) {
        VAULT_SALVAGER_CONTAINER = IForgeMenuType.create((windowId, inventory, buffer) -> {
            Level world = inventory.player.getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            return new VaultSalvagerContainer(windowId, world, pos, inventory);
        });

        SCAVENGER_POUCH_CONTAINER = IForgeMenuType.create((windowId, inventory, data) -> {
            int slot = data.readInt();
            return new ScavengerPouchContainer(windowId, inventory, slot);
        });

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

        event.getRegistry().registerAll(
                new MenuType[]{
                        (MenuType) VAULT_SALVAGER_CONTAINER.setRegistryName("vault_salvager_container"),
                        SCAVENGER_POUCH_CONTAINER.setRegistryName("scavenger_pouch_container"),
                        POWERS_TAB_CONTAINER.setRegistryName("power_tab"),
                        LOOT_STATUE_CONTAINER.setRegistryName("loot_statue_container"),
                        RENAMING_CONTAINER.setRegistryName("renaming_container"),
                        PLAYER_TRADER_CONTAINER.setRegistryName("player_trader_container")
                });
    }
}
