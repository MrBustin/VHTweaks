package xyz.iwolfking.woldsvaults.modifiers.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.api.core.vault_events.lib.EventTag;

import java.util.List;

public class EnchantedElixirPoolModifier extends VaultModifier<EnchantedElixirPoolModifier.Properties> {
    public EnchantedElixirPoolModifier(ResourceLocation id, Properties properties, Display display) {
        super(id, properties, display);
    }

    public static class Properties {
        @Expose
        private final List<EventTag> allowedTags;

        public Properties(List<EventTag> allowedTags) {
            this.allowedTags = allowedTags;
        }

        public List<EventTag> getAllowedTags() {
            return this.allowedTags;
        }
    }
}
