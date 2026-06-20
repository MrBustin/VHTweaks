package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.client.menu.LootStatueMenu;
import io.github.a1qs.vaultadditions.client.menu.PlayerTraderMenu;
import io.github.a1qs.vaultadditions.client.menu.RenameMenu;
import io.github.a1qs.vaultadditions.vault.menu.PowerElementContainerScreen;
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
