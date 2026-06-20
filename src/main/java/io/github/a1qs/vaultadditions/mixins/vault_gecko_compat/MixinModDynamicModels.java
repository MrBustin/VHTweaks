package io.github.a1qs.vaultadditions.mixins.vault_gecko_compat;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.util.ModelUtil;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModDynamicModels.class, remap = false)
public abstract class MixinModDynamicModels {
    @Redirect(method = "lambda$bakeModels$4", at = @At(value = "INVOKE", target = "Liskallia/vault/init/ModDynamicModels;jsonModelExists(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;)Z"))
    private static boolean geckoItemModelJank(ResourceManager manager, ResourceLocation id, @Local(argsOnly = true) DynamicModel<?> dynamicModel) {
        if (false && ModelUtil.getGeckoModel(dynamicModel) != null) {
            ResourceLocation model = DynamicModel.prependToId("models/item/", dynamicModel.getId());
            ResourceLocation jsonModel = DynamicModel.appendToId(model, ".json");
            VaultAdditions.LOGGER.info("Gecko Model id `{}`, model `{}`, json `{}`, json exists {}, withoutjson exists {}", dynamicModel.getId(), model, jsonModel, manager.hasResource(jsonModel), manager.hasResource(model));

            String string = model.getPath();
            string = string.substring(0, string.lastIndexOf("/"));
            VaultAdditions.LOGGER.info("Looking for resources under : {}", string);
            for (ResourceLocation resource : manager.listResources(string, s -> true)) {
                if (resource.equals(jsonModel)) {
                    VaultAdditions.LOGGER.info("Found resource: {}", resource);
                }
            }
        }
        return jsonModelExists(manager, id);
    }

    @Shadow
    public static boolean jsonModelExists(ResourceManager manager, ResourceLocation id) {
        throw new AssertionError("Mixin failed to inject");
    }
}
