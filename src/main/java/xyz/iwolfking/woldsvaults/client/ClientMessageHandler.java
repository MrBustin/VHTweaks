package xyz.iwolfking.woldsvaults.client;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.client.menu.LeaderboardMenu;
import xyz.iwolfking.woldsvaults.network.LeaderboardDataMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;

public class ClientMessageHandler {
    public static void handle(LeaderboardDataMessage msg) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(
                    new LeaderboardMenu(
                            new TranslatableComponent("screen." + WoldsVaults.MOD_ID + ".leaderboard"),
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
