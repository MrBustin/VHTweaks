package xyz.iwolfking.woldsvaults.init.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.iwolfking.woldsvaults.blocks.containers.VaultSalvagerScreen;
import xyz.iwolfking.woldsvaults.init.ModContainers;
import xyz.iwolfking.woldsvaults.items.scavenger_pouch.menu.ScavengerPouchContainer;
import xyz.iwolfking.woldsvaults.items.scavenger_pouch.screen.ScavengerPouchScreen;
import xyz.iwolfking.woldsvaults.client.menu.LootStatueMenu;
import xyz.iwolfking.woldsvaults.client.menu.PlayerTraderMenu;
import xyz.iwolfking.woldsvaults.client.menu.RenameMenu;
import xyz.iwolfking.woldsvaults.client.menu.PowerElementContainerScreen;
import xyz.iwolfking.woldsvaults.container.screen.CrystalTraderScreen;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
    public static void register() {
        MenuScreens.register(ModContainers.VAULT_SALVAGER_CONTAINER, VaultSalvagerScreen::new);
        MenuScreens.register(ModContainers.SCAVENGER_POUCH_CONTAINER, ScavengerPouchScreen::new);
        MenuScreens.register(ModContainers.POWERS_TAB_CONTAINER, PowerElementContainerScreen::new);
        MenuScreens.register(ModContainers.LOOT_STATUE_CONTAINER, LootStatueMenu::new);
        MenuScreens.register(ModContainers.RENAMING_CONTAINER, RenameMenu::new);
        MenuScreens.register(ModContainers.PLAYER_TRADER_CONTAINER, PlayerTraderMenu::new);
        MenuScreens.register(ModContainers.CRYSTAL_TRADER_CONTAINER, CrystalTraderScreen.Shop::new);
    }
}
