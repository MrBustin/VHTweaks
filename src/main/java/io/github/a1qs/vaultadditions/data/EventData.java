package io.github.a1qs.vaultadditions.data;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.config.vault.entry.EventEntry;
import io.github.a1qs.vaultadditions.events.VaultAdditionsEvent;
import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.EventSyncMessage;
import io.github.a1qs.vaultadditions.util.EntryHelper;
import io.github.a1qs.vaultadditions.util.TimeUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventData extends SavedData {
    private static final Random rand = new Random();
    private static final String DATA_NAME = "vaultadditions_EventData";
    private final List<String> scheduledEvents = new ArrayList<>();
    private long eventDuration;
    private boolean isActive;
    private VaultAdditionsEvent activeEvent;

    public void startEvent(EventEntry event) {
        VaultAdditions.LOGGER.info("Started Event: {}", event.getEventId());

        VaultAdditionsEvent activeEventInstance = new VaultAdditionsEvent(EntryHelper.findIndexOf(Configs.EVENT_CONFIG.getWeightedList(), event), 0, 0);
        if(event.isCrystalSubmission()) {
            activeEventInstance.setRequiredCrystals(rand.nextInt(
                    event.getMinCrystalsSubmitted(),
                    event.getMaxCrystalsSubmitted()+1
                    )
            );
        }
        this.eventDuration = event.getEventDuration();
        this.isActive = true;
        this.activeEvent = activeEventInstance;

        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(this.activeEvent.getEventStartMessage(), ChatType.SYSTEM, Util.NIL_UUID);
        ModNetwork.broadcastToAllPlayers(new EventSyncMessage(this.globeExpanderRequired(), this.isEventActive()));
        setDirty();
    }


    public void stopEvent() {
        VaultAdditions.LOGGER.info("Event Ended: {}", this.activeEvent.getEventId());

        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(this.activeEvent.getEventEndMessage(), ChatType.SYSTEM, Util.NIL_UUID);
        this.activeEvent = null;
        this.isActive = false;
        ModNetwork.broadcastToAllPlayers(new EventSyncMessage(this.globeExpanderRequired(), this.isEventActive()));
        setDirty();
    }


    @SubscribeEvent
    public static void onEventDataTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        EventData eventData = EventData.get(ServerLifecycleHooks.getCurrentServer());

        if(eventData.isActive) {
            eventData.eventDuration--;
            if (eventData.eventDuration <= 0) {
                eventData.stopEvent();
            }
            eventData.setDirty();
            return;
        }

        List<String> eventsToRemove = new ArrayList<>();

        for (String scheduledEvent : eventData.scheduledEvents) {
            long timestamp = TimeUtil.convertToEpochMillis(scheduledEvent);
            if (timestamp == -1) {
                VaultAdditions.LOGGER.error("Invalid Timestamp: {}", scheduledEvent);
                eventsToRemove.add(scheduledEvent);
                continue;
            }

            if (eventData.isTimePassed(timestamp)) {
                EventEntry e = Configs.EVENT_CONFIG.getWeightedList().getRandom(new Random());
                eventData.startEvent(e);
                eventsToRemove.add(scheduledEvent);
            }
        }

        eventData.scheduledEvents.removeAll(eventsToRemove);
        eventData.setDirty();
    }

    public void addEventDate(String date) {
        scheduledEvents.add(date);
        setDirty();
    }

    private boolean isTimePassed(long timestamp) {
        return System.currentTimeMillis() >= timestamp;
    }

    public boolean isEventActive() {
        return isActive;
    }

    public boolean conditionsCompleted() {
        return isActive && (this.getActiveEvent().getCrystalsSubmitted() >= this.getActiveEvent().getRequiredCrystals());
    }

    public long getEventDuration() {
        return eventDuration;
    }

    public VaultAdditionsEvent getActiveEvent() {
        return activeEvent;
    }

    public List<String> getScheduledEvents() {
        return scheduledEvents;
    }

    public String getNextScheduledEvent() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        return getScheduledEvents().stream()
                .filter(event -> {
                    try {
                        // Parse the event string into a LocalDateTime
                        LocalDateTime eventDateTime = LocalDateTime.parse(event, TimeUtil.TIME_FORMAT);
                        return eventDateTime.isAfter(now.toLocalDateTime());
                    } catch (Exception e) {
                        // If parsing fails, treat the event as invalid
                        return false;
                    }
                })
                .min(Comparator.comparing(event -> {
                    LocalDateTime eventDateTime = LocalDateTime.parse(event, TimeUtil.TIME_FORMAT);
                    return eventDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                }))
                .orElse("No upcoming events!");
    }

    public boolean globeExpanderRequired() {
        if(!isEventActive()) return false;

        return this.getActiveEvent().isCrystalSubmissionEvent() || this.getActiveEvent().getEventId().equals(VaultAdditionsEvent.BORDER_EXPANSION_ENABLED);
    }


    // Load-Save methods
    public static EventData load(CompoundTag nbt) {
        EventData data = new EventData();
        ListTag scheduledEventTag = nbt.getList("ScheduledEvents", Tag.TAG_STRING);
        for (int i = 0; i < scheduledEventTag.size(); i++) {
            String time = scheduledEventTag.getString(i);
            if (!time.isEmpty()) {
                data.scheduledEvents.add(time);
            }
        }

        data.isActive = nbt.getBoolean("IsActive");
        data.eventDuration = nbt.getLong("EventDuration");
        if(data.isActive) data.activeEvent = VaultAdditionsEvent.deserialize(nbt.getCompound("ActiveEvent"));
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag scheduledEventTag = new ListTag();
        for (String time : scheduledEvents) {
            scheduledEventTag.add(StringTag.valueOf(time));
        }
        nbt.put("ScheduledEvents", scheduledEventTag);

        nbt.putBoolean("IsActive", isActive);
        nbt.putLong("EventDuration", eventDuration);
        if(isActive) nbt.put("ActiveEvent", VaultAdditionsEvent.serialize(this.activeEvent));

        return nbt;
    }

    // Data-getters
    public static EventData getServer() {
        return get(ServerLifecycleHooks.getCurrentServer());
    }

    public static EventData get(ServerLevel level) {
        return get(level.getServer());
    }

    public static EventData get(MinecraftServer srv) {
        return srv.overworld().getDataStorage().computeIfAbsent(EventData::load, EventData::new, DATA_NAME);
    }
}

