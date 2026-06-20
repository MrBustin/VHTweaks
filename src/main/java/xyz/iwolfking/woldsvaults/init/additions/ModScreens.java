package xyz.iwolfking.woldsvaults.init.additions;

import xyz.iwolfking.woldsvaults.client.menu.LootStatueMenu;
import xyz.iwolfking.woldsvaults.client.menu.PlayerTraderMenu;
import xyz.iwolfking.woldsvaults.client.menu.RenameMenu;
import xyz.iwolfking.woldsvaults.client.menu.PowerElementContainerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
    public static void register() {
        MenuScreens.register(ModContainers.POWERS_TAB_CONTAINER, PowerElementContainerScreen::new);
        MenuScreens.register(ModContainers.LOOT_STATUE_CONTAINER, LootStatueMenu::new);
        MenuScreens.register(ModContainers.RENAMING_CONTAINER, RenameMenu::new);
        MenuScreens.register(ModContainers.PLAYER_TRADER_CONTAINER, PlayerTraderMenu::new);
    }
}
