package xyz.iwolfking.woldsvaults.api.extension;

import iskallia.vault.config.customisation.CustomisationDiscovery;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public interface CapeDiscoveryConfigExtension {
    @Nullable
    Component woldsvaults$getCapeRequirementText(String capeId);

    @Nullable
    CustomisationDiscovery woldsvaults$getCapeDiscovery(String capeId);

    List<String> woldsvaults$getDiscoverableCapes(ServerPlayer player);
}
