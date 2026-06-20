package io.github.a1qs.vaultadditions.events;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.data.PlayerAdditionalVaultStatData;
import io.github.a1qs.vaultadditions.data.PlayerPowersData;
import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.EventSyncMessage;
import io.github.a1qs.vaultadditions.util.LuckPermsHelper;
import io.github.a1qs.vaultadditions.util.TimeUtil;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OnPlayerLogInEvent {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ServerLevel level = player.getLevel();
        EventData data = EventData.get(level);

        PlayerPowersData.get(level).getPowers(player).sync(SkillContext.of(player));
        PlayerAdditionalVaultStatData.get(level).getVaultStats(player).sync(level.getServer());

        if (!TimeUtil.pastDate() && ServerConfigs.LIMIT_TIME_FOR_EXPANSION.get()) {
            player.sendMessage(TimeUtil.untilDateMessage().withStyle(ChatFormatting.LIGHT_PURPLE), Util.NIL_UUID);
        }
        if (data.isEventActive()) {
            player.sendMessage(data.getActiveEvent().getEventLoginMessage(), Util.NIL_UUID);
        }

        // Send a network message to inform clients about event stuff
        ModNetwork.sendToClient(new EventSyncMessage(data.globeExpanderRequired(), data.isEventActive()), player);
    }

    @SubscribeEvent
    public static void transmogUnlocks(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        ServerLevel level = player.getLevel();

        LinkedHashSet<DynamicModel<?>> models = new LinkedHashSet<>();
        List<DynamicModel<?>> manualUnlocks = Configs.TRANSMOG_UNLOCKS.getUnlocks(player);
        if (manualUnlocks != null) {
            models.addAll(manualUnlocks);
        }
        models.addAll(getLuckPermsUnlocks(player));

        if (models.isEmpty()) {
            return;
        }

        DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get(level);
        Set<ResourceLocation> discoveredModels = discoveredModelsData.getDiscoveredModels(player.getUUID());
        for (DynamicModel<?> model : models) {
            ResourceLocation id = model.getId();
            if (model instanceof ArmorModel armor) {
                for (ArmorPieceModel piece : armor.getPieces().values()) {
                    if (!discoveredModels.contains(piece.getId())) {
                        discoveredModelsData.discoverAllArmorPieceAndBroadcast(player, armor);
                        break;
                    }
                }
            } else {
                if (!discoveredModels.contains(id)) {
                    ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(id).ifPresent(pair -> {
                        discoveredModelsData.discoverModelAndBroadcast(pair.getSecond(), id, player);
                    });
                }
            }
        }
    }

    private static List<DynamicModel<?>> getLuckPermsUnlocks(ServerPlayer player) {
        try {
            return Configs.TRANSMOG_ROLE_UNLOCKS.getUnlocks(LuckPermsHelper.getRoleNames(player));
        } catch (IllegalStateException | NoClassDefFoundError ignored) {
            return List.of();
        }
    }
}
