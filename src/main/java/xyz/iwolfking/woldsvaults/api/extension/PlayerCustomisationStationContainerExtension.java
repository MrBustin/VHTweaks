package xyz.iwolfking.woldsvaults.api.extension;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface PlayerCustomisationStationContainerExtension {
    void woldsvaults$applyCapeSelection(ServerPlayer player, @Nullable String capeId, boolean enabled);
}
