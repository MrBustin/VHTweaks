package xyz.iwolfking.woldsvaults.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import xyz.iwolfking.woldsvaults.blocks.containers.VaultSalvagerContainer;
import xyz.iwolfking.woldsvaults.items.scavenger_pouch.menu.ScavengerPouchContainer;

public class ModContainers {

    public static MenuType<VaultSalvagerContainer> VAULT_SALVAGER_CONTAINER;
    public static MenuType<ScavengerPouchContainer> SCAVENGER_POUCH_CONTAINER;

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

        event.getRegistry().registerAll(
                new MenuType[]{
                        (MenuType) VAULT_SALVAGER_CONTAINER.setRegistryName("vault_salvager_container"),
                        SCAVENGER_POUCH_CONTAINER.setRegistryName("scavenger_pouch_container")
                });
    }
}
