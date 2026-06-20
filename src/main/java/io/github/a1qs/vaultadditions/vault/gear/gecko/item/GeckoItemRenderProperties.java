package io.github.a1qs.vaultadditions.vault.gear.gecko.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;

public class GeckoItemRenderProperties implements IItemRenderProperties {
    private final IItemRenderProperties defaultProperties;
    BlockEntityWithoutLevelRenderer renderer = null;

    public GeckoItemRenderProperties(IItemRenderProperties defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        if (renderer == null) {
            renderer = new VaultGeckoItemRenderer<>(defaultProperties);
        }
        return renderer;
    }
}