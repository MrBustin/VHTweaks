package xyz.iwolfking.woldsvaults.events.overworld;

import xyz.iwolfking.woldsvaults.configs.overworld.VaultConfigRegistry;
import xyz.iwolfking.woldsvaults.util.overworld.MiscUtil;
import xyz.iwolfking.woldsvaults.util.overworld.OverworldVaultCrystalUtil;
import xyz.iwolfking.woldsvaults.world.overworld.ActiveCrystalSavedData;
import xyz.iwolfking.woldsvaults.world.overworld.PortalData;
import xyz.iwolfking.woldsvaults.world.overworld.PortalSavedData;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class OnPlayerLogin {
    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (!VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.BROADCAST_IN_CHAT) return;

        if (event.getPlayer() instanceof ServerPlayer player) {
            PortalData data = PortalSavedData.getServer().getFirstActivePortalData();
            if (data != null) {
                if (data.getDimension().equals(player.getLevel().dimension()) && VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.UPDATE_VAULT_COMPASS) {
                    MiscUtil.sendCompassInfoToPlayer(player, data.getDimension(), data.getPortalFrameCenterPos());
                }

                player.sendMessage(MiscUtil.getPortalMessage(data.getLoginTranslationComponent(), data.getDimension()), ChatType.SYSTEM, Util.NIL_UUID);
                return;
            }

            ActiveCrystalSavedData activeCrystalData = ActiveCrystalSavedData.getServer();
            if (activeCrystalData.hasActiveCrystal()
                    && activeCrystalData.getDimension().equals(player.getLevel().dimension())
                    && VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.UPDATE_VAULT_COMPASS) {
                BlockPos pedestalPos = OverworldVaultCrystalUtil.getActiveCrystalPedestalPos(player.getServer(), activeCrystalData).orElse(activeCrystalData.getPedestalPos());
                MiscUtil.sendCompassInfoToPlayer(player, activeCrystalData.getDimension(), pedestalPos);
            }
        }
    }
}
