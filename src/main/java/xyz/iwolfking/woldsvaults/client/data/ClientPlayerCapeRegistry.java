package xyz.iwolfking.woldsvaults.client.data;

import com.mojang.blaze3d.platform.NativeImage;
import iskallia.vault.VaultMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import xyz.iwolfking.woldsvaults.configs.PlayerCapesConfig;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.network.message.CapeAssignmentsMessage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ClientPlayerCapeRegistry {

    private static final Map<UUID, CapeAssignmentsMessage.Assignment> assignments = new HashMap<>();
    private static final Map<ResourceLocation, CapeTextureInfo> textureCache = new HashMap<>();
    private static final Set<ResourceLocation> missingTexturesLogged = new HashSet<>();

    public static void receiveAssignments(Map<UUID, CapeAssignmentsMessage.Assignment> newAssignments) {
        assignments.clear();
        assignments.putAll(newAssignments);
    }

    @Nullable
    public static ResourceLocation getCapeTexture(UUID playerUUID) {
        CapeAssignmentsMessage.Assignment assignment = assignments.get(playerUUID);
        if (assignment == null || !assignment.enabled() || assignment.capeId() == null) return null;

        PlayerCapesConfig config = ModConfigs.PLAYER_CAPES;
        if (config == null) return null;

        PlayerCapesConfig.Cape cape = config.get(assignment.capeId());
        if (cape == null) return null;

        ResourceLocation texture = cape.getTextureLocation();
        if (texture == null) return null;

        CapeTextureInfo info = resolveTexture(texture);
        return info != null ? texture : null;
    }

    @Nullable
    public static CapeTextureInfo getCapeTextureInfo(UUID playerUUID) {
        CapeAssignmentsMessage.Assignment assignment = assignments.get(playerUUID);
        if (assignment == null || !assignment.enabled() || assignment.capeId() == null) return null;

        PlayerCapesConfig config = ModConfigs.PLAYER_CAPES;
        if (config == null) return null;

        PlayerCapesConfig.Cape cape = config.get(assignment.capeId());
        if (cape == null) return null;

        ResourceLocation texture = cape.getTextureLocation();
        if (texture == null) return null;

        return resolveTexture(texture);
    }

    @Nullable
    private static CapeTextureInfo resolveTexture(ResourceLocation texture) {
        CapeTextureInfo cached = textureCache.get(texture);
        if (cached != null) return cached;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return null;

        try {
            Resource resource = mc.getResourceManager().getResource(texture);
            try (InputStream stream = resource.getInputStream();
                 NativeImage image = NativeImage.read(stream)) {
                CapeTextureInfo info = new CapeTextureInfo(texture, image.getWidth(), image.getHeight());
                textureCache.put(texture, info);
                return info;
            }
        } catch (IOException e) {
            if (missingTexturesLogged.add(texture)) {
                VaultMod.LOGGER.warn("Cape texture {} not found or unreadable; skipping render.", texture);
            }
            return null;
        }
    }

    public static void clear() {
        assignments.clear();
        textureCache.clear();
        missingTexturesLogged.clear();
    }

    public record CapeTextureInfo(ResourceLocation location, int width, int height) {}
}
