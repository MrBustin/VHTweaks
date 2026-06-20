package xyz.iwolfking.woldsvaults.modifiers.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class EnchantedElixirEventAmountModifier extends VaultModifier<EnchantedElixirEventAmountModifier.Properties> {
    public EnchantedElixirEventAmountModifier(ResourceLocation id, Properties properties, Display display) {
        super(id, properties, display);
    }

    public static class Properties {
        @Expose
        private final int count;

        public Properties(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }
    }
}
