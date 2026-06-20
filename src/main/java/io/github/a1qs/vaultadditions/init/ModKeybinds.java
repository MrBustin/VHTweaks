package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class ModKeybinds {
    public static KeyMapping openLeaderboard;
    public static KeyMapping toggleZephyr;


    public static void register() {
        openLeaderboard = registerKeyMapping("open_leaderboard", -1);
        toggleZephyr = registerKeyMapping("toggle_zephyr", -1);
    }


    private static KeyMapping registerKeyMapping(String name, int keyCode) {
        KeyMapping key = new KeyMapping("key." + VaultAdditions.MOD_ID + "." + name, keyCode, "key.category." + VaultAdditions.MOD_ID);
        ClientRegistry.registerKeyBinding(key);
        return key;
    }
}
