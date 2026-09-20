package xyz.iwolfking.woldsvaults.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.util.MiscUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.iwolfking.woldsvaults.configs.PlayerCapesConfig;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.network.message.CapeAssignmentsMessage;
import xyz.iwolfking.woldsvaults.network.message.DiscoveredCapesMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DiscoveredCapesData extends SavedData {

    protected static final String DATA_NAME = VaultMod.MOD_ID + "_DiscoveredCapes";

    private final Map<UUID, Set<String>> discoveredCapes = new HashMap<>();
    private final Map<UUID, String> selectedCapes = new HashMap<>();
    private final Map<UUID, Boolean> capeEnabled = new HashMap<>();

    private DiscoveredCapesData() {}

    private DiscoveredCapesData(CompoundTag tag) {
        this.load(tag);
    }

    public boolean discoverCape(UUID playerUUID, String capeId) {
        Set<String> playerSet = discoveredCapes.computeIfAbsent(playerUUID, id -> new HashSet<>());
        if (!playerSet.contains(capeId)) {
            playerSet.add(capeId);
            this.setDirty();
            return true;
        }
        return false;
    }

    public void discoverCapeAndBroadcast(ServerPlayer player, String capeId) {
        boolean discovered = this.discoverCape(player.getUUID(), capeId);
        if (discovered) {
            this.broadcastCapeDiscovery(capeId, player);
        }
    }

    private void broadcastCapeDiscovery(String capeId, ServerPlayer player) {
        PlayerCapesConfig config = ModConfigs.PLAYER_CAPES;
        if (config == null) return;
        PlayerCapesConfig.Cape cape = config.get(capeId);
        if (cape == null) return;

        MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
        MutableComponent playerName = player.getName().copy();
        playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00_983198)));

        MutableComponent capeName = new TextComponent(cape.getDisplayName());
        capeName.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));

        MiscUtils.broadcast(msgContainer.append(playerName).append(" has discovered the ").append(capeName).append(" cape."));
    }

    public boolean revokeCape(UUID playerUUID, String capeId) {
        boolean changed = false;
        Set<String> playerSet = discoveredCapes.get(playerUUID);
        if (playerSet != null && playerSet.remove(capeId)) {
            changed = true;
        }
        if (capeId.equals(selectedCapes.get(playerUUID))) {
            selectedCapes.remove(playerUUID);
            capeEnabled.put(playerUUID, false);
            changed = true;
        }
        if (changed) this.setDirty();
        return changed;
    }

    public boolean hasDiscoveredCape(UUID playerUUID, String capeId) {
        return discoveredCapes.getOrDefault(playerUUID, Collections.emptySet()).contains(capeId);
    }

    public Set<String> getDiscoveredCapes(UUID playerUUID) {
        return new HashSet<>(discoveredCapes.getOrDefault(playerUUID, Collections.emptySet()));
    }

    @Nullable
    public String getSelectedCape(UUID playerUUID) {
        return selectedCapes.get(playerUUID);
    }

    public void setSelectedCape(UUID playerUUID, @Nullable String capeId) {
        if (capeId == null) {
            selectedCapes.remove(playerUUID);
        } else {
            selectedCapes.put(playerUUID, capeId);
        }
        this.setDirty();
    }

    public boolean isCapeEnabled(UUID playerUUID) {
        return capeEnabled.getOrDefault(playerUUID, true);
    }

    public void setCapeEnabled(UUID playerUUID, boolean enabled) {
        capeEnabled.put(playerUUID, enabled);
        this.setDirty();
    }

    public void reset(UUID playerUUID) {
        boolean changed = false;
        if (this.discoveredCapes.remove(playerUUID) != null) changed = true;
        if (this.selectedCapes.remove(playerUUID) != null) changed = true;
        if (this.capeEnabled.remove(playerUUID) != null) changed = true;
        if (changed) this.setDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (dirty) {
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            if (srv != null) {
                srv.getPlayerList().getPlayers().forEach(this::syncTo);
                ModNetwork.broadcastToAllPlayers(buildAssignmentsMessage(srv));
            }
        }
    }

    public void syncTo(ServerPlayer player) {
        UUID uuid = player.getUUID();
        Set<String> discovered = getDiscoveredCapes(uuid);
        String selected = getSelectedCape(uuid);
        boolean enabled = isCapeEnabled(uuid);
        ModNetwork.sendToClient(new DiscoveredCapesMessage(discovered, selected, enabled), player);
    }

    public void syncAssignmentsTo(ServerPlayer player) {
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        if (srv == null) return;
        ModNetwork.sendToClient(buildAssignmentsMessage(srv), player);
    }

    public void syncAssignmentsToAll() {
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        if (srv == null) return;
        ModNetwork.broadcastToAllPlayers(buildAssignmentsMessage(srv));
    }

    private CapeAssignmentsMessage buildAssignmentsMessage(MinecraftServer server) {
        Map<UUID, CapeAssignmentsMessage.Assignment> assignments = new HashMap<>();
        server.getPlayerList().getPlayers().forEach(player -> {
            UUID uuid = player.getUUID();
            String selected = getSelectedCape(uuid);
            boolean enabled = isCapeEnabled(uuid);
            assignments.put(uuid, new CapeAssignmentsMessage.Assignment(selected, enabled));
        });
        return new CapeAssignmentsMessage(assignments);
    }

    public void load(CompoundTag compound) {
        discoveredCapes.clear();
        selectedCapes.clear();
        capeEnabled.clear();

        CompoundTag capesTag = compound.getCompound("Capes");
        for (String uuidKey : capesTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidKey);
            Set<String> ids = new HashSet<>();
            ListTag list = capesTag.getList(uuidKey, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ids.add(list.getString(i));
            }
            discoveredCapes.put(uuid, ids);
        }

        CompoundTag selectedTag = compound.getCompound("Selected");
        for (String uuidKey : selectedTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidKey);
            selectedCapes.put(uuid, selectedTag.getString(uuidKey));
        }

        CompoundTag enabledTag = compound.getCompound("Enabled");
        for (String uuidKey : enabledTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidKey);
            capeEnabled.put(uuid, enabledTag.getBoolean(uuidKey));
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        CompoundTag capesTag = new CompoundTag();
        discoveredCapes.forEach((uuid, ids) -> {
            ListTag list = new ListTag();
            ids.forEach(id -> list.add(StringTag.valueOf(id)));
            capesTag.put(uuid.toString(), list);
        });
        compound.put("Capes", capesTag);

        CompoundTag selectedTag = new CompoundTag();
        selectedCapes.forEach((uuid, id) -> selectedTag.putString(uuid.toString(), id));
        compound.put("Selected", selectedTag);

        CompoundTag enabledTag = new CompoundTag();
        capeEnabled.forEach((uuid, enabled) -> enabledTag.putBoolean(uuid.toString(), enabled));
        compound.put("Enabled", enabledTag);

        return compound;
    }

    public static DiscoveredCapesData get(ServerLevel level) {
        return get(level.getServer());
    }

    public static DiscoveredCapesData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(DiscoveredCapesData::new, DiscoveredCapesData::new, DATA_NAME);
    }
}
