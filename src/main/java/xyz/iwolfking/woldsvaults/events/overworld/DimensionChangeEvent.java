package xyz.iwolfking.woldsvaults.events.overworld;

import xyz.iwolfking.woldsvaults.configs.overworld.VaultConfigRegistry;
import xyz.iwolfking.woldsvaults.util.overworld.MiscUtil;
import xyz.iwolfking.woldsvaults.util.overworld.OverworldVaultCrystalUtil;
import xyz.iwolfking.woldsvaults.world.overworld.ActiveCrystalSavedData;
import xyz.iwolfking.woldsvaults.world.overworld.PortalData;
import xyz.iwolfking.woldsvaults.world.overworld.PortalSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class DimensionChangeEvent {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() == null) return;
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        if (!VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.UPDATE_VAULT_COMPASS) return;

        if (event.getPlayer() instanceof ServerPlayer player) {
            PortalData data = PortalSavedData.getServer().getFirstActivePortalData();
            if (data == null) {
                ActiveCrystalSavedData activeCrystalData = ActiveCrystalSavedData.getServer();
                if (activeCrystalData.hasActiveCrystal()) {
                    if (activeCrystalData.getDimension().equals(event.getTo())) {
                        BlockPos pedestalPos = OverworldVaultCrystalUtil.getActiveCrystalPedestalPos(player.getServer(), activeCrystalData).orElse(activeCrystalData.getPedestalPos());
                        MiscUtil.sendCompassInfoToPlayer(player, activeCrystalData.getDimension(), pedestalPos);
                    } else {
                        MiscUtil.clearCompassInfoForPlayer(player);
                    }
                    return;
                }

                MiscUtil.clearCompassInfoForPlayer(player);
                return;
            }
            if (data.getDimension().equals(event.getTo())) {
                MiscUtil.sendCompassInfoToPlayer(player, data.getDimension(), data.getPortalFrameCenterPos());
            } else {
                MiscUtil.clearCompassInfoForPlayer(player);
            }
        }
    }
}
