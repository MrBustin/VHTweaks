package io.github.a1qs.vaultadditions.mixins.vault_gecko_compat;

import io.github.a1qs.vaultadditions.vault.gear.gecko.item.GeckoItemRenderProperties;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.item.gear.VaultArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Item.class)
public class MixinItem {
    @Shadow(remap = false) private Object renderProperties;

    @ModifyArg(method = "initClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;initializeClient(Ljava/util/function/Consumer;)V"), remap = false)
    public Consumer<IItemRenderProperties> wrapCustomItemRenderer(Consumer<IItemRenderProperties> consumer) {
        if (applies()) {
            return properties -> {
                if (properties == this) {
                    throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
                } else if (!(properties instanceof GeckoItemRenderProperties)) {
                    this.renderProperties = new GeckoItemRenderProperties(properties);
                } else {
                    this.renderProperties = properties;
                }
            };
        }
        return consumer;
    }

    @Inject(method = "initializeClient", at = @At("HEAD"), remap = false)
    public void addDefaultGeckoRenderer(Consumer<IItemRenderProperties> consumer, CallbackInfo ci) {
        if (applies()) {
            consumer.accept(new GeckoItemRenderProperties(null));
        }
    }

    @Unique
    private boolean applies() {
        return this instanceof VaultGearItem && !VaultArmorItem.class.isInstance(this);
    }
}
