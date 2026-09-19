package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.config.customisation.CustomisationDiscoveryConfig;
import iskallia.vault.config.customisation.CustomisationDiscoveryHelper;
import iskallia.vault.init.ModConfigs;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.extension.CapeDiscoveryConfigExtension;
import xyz.iwolfking.woldsvaults.world.data.DiscoveredCapesData;

@Mixin(value = CustomisationDiscoveryHelper.class, remap = false)
public abstract class MixinCustomisationDiscoveryHelper {

    @Inject(method = "checkDiscoveries", at = @At("TAIL"))
    private static void woldsvaults$checkCapeDiscoveries(ServerPlayer player, CallbackInfo ci) {
        CustomisationDiscoveryConfig config = ModConfigs.CUSTOMISATION_DISCOVERY;
        if (!(config instanceof CapeDiscoveryConfigExtension extension)) {
            return;
        }

        DiscoveredCapesData capesData = DiscoveredCapesData.get(player.getLevel());
        for (String capeId : extension.woldsvaults$getDiscoverableCapes(player)) {
            capesData.discoverCapeAndBroadcast(player, capeId);
        }
    }
}
