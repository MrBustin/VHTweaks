package xyz.iwolfking.woldsvaults.client.overworld;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class OverworldVaultKeybinds {
    public static KeyMapping openOverworldVaultGui;

    public static void register() {
        openOverworldVaultGui = new KeyMapping(
                "key." + WoldsVaults.MOD_ID + ".open_overworld_vault_gui",
                -1,
                "key.category." + WoldsVaults.MOD_ID
        );
        ClientRegistry.registerKeyBinding(openOverworldVaultGui);
    }
}
