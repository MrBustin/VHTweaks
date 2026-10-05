package xyz.iwolfking.woldsvaults.configs;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerCapesConfig extends Config {

    @Expose
    private Map<String, Cape> capes;

    @Override
    public String getName() {
        return "player_capes";
    }

    public Map<String, Cape> getAll() {
        return this.capes != null ? this.capes : Collections.emptyMap();
    }

    @Nullable
    public Cape get(String id) {
        return this.capes != null ? this.capes.get(id) : null;
    }

    @Override
    protected void reset() {
        this.capes = new LinkedHashMap<>();
        this.capes.put("twitch", new Cape(
                "Twitch Cape",
                "the_vault:textures/entity/cape/twitch.png",
                "A cape granted to Twitch streamers."
        ));
    }

    public static class Cape {
        @Expose private final String displayName;
        @Expose private final String texture;
        @Expose private final String description;

        public Cape() {
            this("", "", "");
        }

        public Cape(String displayName, String texture, String description) {
            this.displayName = displayName;
            this.texture = texture;
            this.description = description;
        }

        public String getDisplayName() {
            return this.displayName != null ? this.displayName : "";
        }

        @Nullable
        public ResourceLocation getTextureLocation() {
            if (this.texture == null || this.texture.isEmpty()) return null;
            try {
                return new ResourceLocation(this.texture);
            } catch (net.minecraft.ResourceLocationException e) {
                return null;
            }
        }

        public String getDescription() {
            return this.description != null ? this.description : "";
        }
    }
}
