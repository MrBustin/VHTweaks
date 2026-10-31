package xyz.iwolfking.woldsvaults.util;

import net.minecraft.world.entity.player.Player;

/** Carries the server-side player into the station's no-argument applyJewels method. */
public final class ToolerPrestigeApplicationContext {
    private static final ThreadLocal<Player> PLAYER = new ThreadLocal<>();

    private ToolerPrestigeApplicationContext() {
    }

    public static Player getPlayer() {
        return PLAYER.get();
    }

    public static void setPlayer(Player player) {
        PLAYER.set(player);
    }

    public static void clearPlayer() {
        PLAYER.remove();
    }
}
