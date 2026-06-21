package xyz.iwolfking.woldsvaults.events.overworld;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.util.overworld.PortalUtil;
import xyz.iwolfking.woldsvaults.world.overworld.PortalData;
import xyz.iwolfking.woldsvaults.world.overworld.PortalSavedData;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class VaultLifecycleEventHandler {
    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        CommonEvents.VAULT_END.register(VaultLifecycleEventHandler.class, data -> {
            Object rawVaultId = data.getVault().get(Vault.ID);
            if (!(rawVaultId instanceof UUID vaultId)) {
                return;
            }

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return;
            }

            PortalSavedData portalSavedData = PortalSavedData.get(server);
            PortalData activePortalData = portalSavedData.getFirstActivePortalData();
            if (activePortalData == null || !matchesActivePortal(server, activePortalData, vaultId)) {
                return;
            }

            WoldsVaults.LOGGER.info(
                    "Vault {} ended. Deactivating matching OverworldVault portal at {} in {}.",
                    vaultId,
                    activePortalData.getPortalFrameCenterPos(),
                    activePortalData.getDimension().location()
            );

            PortalUtil.deactivatePortal(
                    server,
                    activePortalData,
                    true,
                    null
            );
            ServerTickEvent.activePortalTickCounter = 0;
            ServerTickEvent.actlRemoveModifierTimer = -1;
        });
    }

    private static boolean matchesActivePortal(MinecraftServer server, PortalData activePortalData, UUID vaultId) {
        UUID activeVaultId = activePortalData.getActiveVaultId();
        if (vaultId.equals(activeVaultId)) {
            return true;
        }

        return PortalUtil.getActivePortalVaultId(server, activePortalData)
                .map(portalVaultId -> {
                    if (vaultId.equals(portalVaultId)) {
                        activePortalData.setActiveVaultId(portalVaultId);
                        PortalSavedData.get(server).setDirty();
                        return true;
                    }

                    return false;
                })
                .orElse(false);
    }
}
