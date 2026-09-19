package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.config.customisation.CustomisationDiscovery;
import iskallia.vault.config.customisation.CustomisationDiscoveryConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.extension.CapeDiscoveryConfigExtension;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = CustomisationDiscoveryConfig.class, remap = false)
public abstract class MixinCustomisationDiscoveryConfig implements CapeDiscoveryConfigExtension {

    @Unique
    @Expose
    @SerializedName("capes")
    private Map<String, CustomisationDiscovery> woldsvaults$capes;

    @Override
    @Nullable
    public Component woldsvaults$getCapeRequirementText(String capeId) {
        CustomisationDiscovery discovery = this.woldsvaults$getCapeDiscovery(capeId);
        return discovery == null ? null : discovery.getRequirementText("cape");
    }

    @Override
    @Nullable
    public CustomisationDiscovery woldsvaults$getCapeDiscovery(String capeId) {
        return this.woldsvaults$capes == null ? null : this.woldsvaults$capes.get(capeId);
    }

    @Override
    public List<String> woldsvaults$getDiscoverableCapes(ServerPlayer player) {
        if (this.woldsvaults$capes == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, CustomisationDiscovery> entry : this.woldsvaults$capes.entrySet()) {
            if (entry.getValue().test(player)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void woldsvaults$resetCapeDiscoveries(CallbackInfo ci) {
        this.woldsvaults$capes = new LinkedHashMap<>();
    }
}
