package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.container.PlayerCustomisationStationContainer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import xyz.iwolfking.woldsvaults.api.extension.PlayerCustomisationStationContainerExtension;
import xyz.iwolfking.woldsvaults.world.data.DiscoveredCapesData;

import javax.annotation.Nullable;

@Mixin(value = PlayerCustomisationStationContainer.class, remap = false)
public abstract class MixinPlayerCustomisationStationContainer
        implements PlayerCustomisationStationContainerExtension {

    @Override
    public void woldsvaults$applyCapeSelection(ServerPlayer player, @Nullable String capeId, boolean enabled) {
        DiscoveredCapesData capesData = DiscoveredCapesData.get(player.getLevel());

        if (capeId == null || capeId.isEmpty()) {
            capesData.setSelectedCape(player.getUUID(), null);
            capesData.setCapeEnabled(player.getUUID(), false);
            return;
        }

        if (!capesData.hasDiscoveredCape(player.getUUID(), capeId)) {
            return;
        }

        capesData.setSelectedCape(player.getUUID(), capeId);
        capesData.setCapeEnabled(player.getUUID(), enabled);
    }
}
