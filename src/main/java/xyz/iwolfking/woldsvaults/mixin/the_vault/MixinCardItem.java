package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.item.CardItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.vhapi.api.util.ResourceLocUtils;
import xyz.iwolfking.woldsvaults.WoldsVaults;

import java.util.Collection;
import java.util.function.Consumer;

@Mixin(value = CardItem.class, remap = false)
public class MixinCardItem {

    @Inject(method = "loadModels", at = @At("TAIL"))
    public void loadModels(Consumer<ModelResourceLocation> consumer, CallbackInfo ci) {
        Collection<ResourceLocation> iconModels = Minecraft.getInstance().getResourceManager()
                .listResources("models/item/card/icon", s -> s.endsWith(".json"));

        for (ResourceLocation loc : iconModels) {
            if (loc.getNamespace().equals("the_vault")) {
                String path = loc.getPath();
                String iconName = path.substring("models/item/".length(), path.length() - ".json".length());

                ModelResourceLocation modelLoc = new ModelResourceLocation("the_vault:" + iconName + "#inventory");

                consumer.accept(modelLoc);
            }
        }
    }
}
