package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.item.InfusedCatalystItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(value = InfusedCatalystItem.class, remap = false)
public class MixinCatalystItem {

    @Inject(method = "loadModels", at = @At("TAIL"))
    public void loadModels(Consumer<ModelResourceLocation> consumer, CallbackInfo ci) {
        Map<Integer, String> CUSTOM_CATALYST_MODELS = new HashMap<>();
        CUSTOM_CATALYST_MODELS.put(95, "95"); // Power Catalyst
        CUSTOM_CATALYST_MODELS.put(96, "96"); // Item Quantity Small Catalyst
        CUSTOM_CATALYST_MODELS.put(97, "97"); // Item Quantity Large Catalyst
        CUSTOM_CATALYST_MODELS.put(98, "98"); // Item Rarity Small Catalyst
        CUSTOM_CATALYST_MODELS.put(99, "99"); // Item Rarity Large Catalyst

        for(Integer i : CUSTOM_CATALYST_MODELS.keySet()) {
            consumer.accept(new ModelResourceLocation("the_vault:catalyst/%d#inventory".formatted(i)));
        }
    }
}
