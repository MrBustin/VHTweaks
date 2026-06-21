package xyz.iwolfking.woldsvaults.client.overworld;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.client.overworld.gui.OverworldVaultPortalScreen;
import xyz.iwolfking.woldsvaults.network.overworld.ClientboundOverworldVaultGuiDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

public class OverworldVaultPortalClientHandler {
    public static void handle(ClientboundOverworldVaultGuiDataPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            if (!packet.dataAvailable) {
                WoldsVaults.LOGGER.error("OverworldVault GUI data unavailable: {}", packet.errorMessage);
                if (minecraft.player != null) {
                    minecraft.player.displayClientMessage(new TextComponent(packet.errorMessage), true);
                }
            }

            if (minecraft.screen instanceof OverworldVaultPortalScreen screen) {
                screen.applyData(packet);
            }
        });
    }
}
