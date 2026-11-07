package xyz.iwolfking.woldsvaults.util;

import net.minecraft.world.entity.player.Player;

/** Carries the applying player into JewelApplication.plan, excluding client previews. */
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
