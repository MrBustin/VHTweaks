package io.github.a1qs.vaultadditions.client;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.client.menu.LeaderboardMenu;
import io.github.a1qs.vaultadditions.network.LeaderboardDataMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;

public class ClientMessageHandler {
    public static void handle(LeaderboardDataMessage msg) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(
                    new LeaderboardMenu(
                            new TranslatableComponent("screen." + VaultAdditions.MOD_ID + ".leaderboard"),
                            msg.leaderboard,
                            msg.nextScheduledEvent,
                            msg.serverTimeZone,
                            msg.optionalEvent,
                            msg.optionalEventDuration,
                            msg.totalContributions
                    )
            );
        });
    }
}
