package io.github.a1qs.vaultadditions.config.vault.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.events.VaultAdditionsEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventEntry {
    @Expose
    public final ResourceLocation eventId;

    @Expose
    public final JsonElement eventStartMessage;

    @Expose
    public final JsonElement eventEndMessage;

    @Expose
    public final JsonElement eventLoginMessage;

    @Expose
    public final JsonElement eventDisplayMessage;

    @Expose
    public final JsonElement eventEnabledMessage;

    @Expose
    public final long eventDuration;

    @Expose
    public final boolean crystalSubmission;

    @Expose // isCrystalSubmission
    public Integer minCrystalsSubmitted;

    @Expose // isCrystalSubmission
    public Integer maxCrystalsSubmitted;

    @Expose // "event_add_portal_modifiers"
    public Map<ResourceLocation, Integer> additionalModifiers;

    @Expose // "event_vault_completion_item"
    public ItemStack itemToAdd;

    @Expose // "event_vault_completion_item"
    public Float chance;

    public EventEntry(ResourceLocation eventId, JsonElement eventStartMessage, JsonElement eventEndMessage, JsonElement eventLoginMessage, JsonElement eventDisplayMessage, JsonElement eventEnabledMessage, long eventDuration, boolean crystalSubmission) {
        this.eventId = eventId;
        this.eventStartMessage = eventStartMessage;
        this.eventEndMessage = eventEndMessage;
        this.eventLoginMessage = eventLoginMessage;
        this.eventDisplayMessage = eventDisplayMessage;
        this.eventEnabledMessage = eventEnabledMessage;
        this.eventDuration = eventDuration;
        this.crystalSubmission = crystalSubmission;
        if(crystalSubmission) minCrystalsSubmitted = 50;
        if(crystalSubmission) maxCrystalsSubmitted = 250;
    }

    public EventEntry(ResourceLocation eventId, JsonElement eventStartMessage, JsonElement eventEndMessage, JsonElement eventLoginMessage, JsonElement eventDisplayMessage, JsonElement eventEnabledMessage, long eventDuration, boolean crystalSubmission, Map<ResourceLocation, Integer> resourceLocationMap) {
        this.eventId = eventId;
        this.eventStartMessage = eventStartMessage;
        this.eventEndMessage = eventEndMessage;
        this.eventLoginMessage = eventLoginMessage;
        this.eventDisplayMessage = eventDisplayMessage;
        this.eventEnabledMessage = eventEnabledMessage;
        this.eventDuration = eventDuration;
        this.crystalSubmission = crystalSubmission;
        if(crystalSubmission) minCrystalsSubmitted = 50;
        if(crystalSubmission) maxCrystalsSubmitted = 250;

        this.additionalModifiers = resourceLocationMap;
    }

    public EventEntry(ResourceLocation eventId, JsonElement eventStartMessage, JsonElement eventEndMessage, JsonElement eventLoginMessage, JsonElement eventDisplayMessage, JsonElement eventEnabledMessage, long eventDuration, boolean crystalSubmission, ItemStack itemToAdd, float chance) {
        this.eventId = eventId;
        this.eventStartMessage = eventStartMessage;
        this.eventEndMessage = eventEndMessage;
        this.eventLoginMessage = eventLoginMessage;
        this.eventDisplayMessage = eventDisplayMessage;
        this.eventEnabledMessage = eventEnabledMessage;
        this.eventDuration = eventDuration;
        this.crystalSubmission = crystalSubmission;
        if(crystalSubmission) minCrystalsSubmitted = 50;
        if(crystalSubmission) maxCrystalsSubmitted = 250;
        this.itemToAdd = itemToAdd;
        this.chance = chance;
    }

    /**
     * Parses the `eventMessage` into a `Component`, replacing placeholders with the given values.
     *
     * @return A parsed `Component` ready for rendering.
     */
    public Component getParsedMessage(JsonElement message) {
        EventData d = EventData.getServer(); // bandaid fix LMAO
        boolean eventActive = d.isEventActive();
        Map<String, String> placeholders = new HashMap<>();

        // static event data
        placeholders.put("totalEventDurationTicks", String.valueOf(this.eventDuration));
        placeholders.put("totalEventDurationSeconds", String.valueOf((this.eventDuration / 20) % 60));
        placeholders.put("totalEventDurationMinutes", String.valueOf((this.eventDuration / (20 * 60)) % 60));
        placeholders.put("totalEventDurationHours", String.valueOf((this.eventDuration / (20 * 60 * 60)) % 24));
        placeholders.put("totalEventDurationDays", String.valueOf(this.eventDuration / (20 * 60 * 60 * 24)));
        placeholders.put("eventId", this.eventId.toString());
        placeholders.put("crystalSubmission", String.valueOf(this.crystalSubmission));
        placeholders.put("minCrystalsSubmitted", String.valueOf(this.minCrystalsSubmitted));
        placeholders.put("maxCrystalsSubmitted", String.valueOf(this.maxCrystalsSubmitted));


        if(d.isEventActive()){
            if(d.getActiveEvent().getEventId().equals(VaultAdditionsEvent.ADD_VAULT_COMPLETION_ITEM)) {
                placeholders.put("item", this.itemToAdd.getItem().getRegistryName().toString());
                placeholders.put("itemCount", String.valueOf(this.itemToAdd.getCount()));
            }
        }
        // active event based data
        placeholders.put("eventDurationTicks", String.valueOf(eventActive ? d.getEventDuration() : null));
        placeholders.put("eventDurationSeconds", String.valueOf(eventActive ? (d.getEventDuration() / 20) % 60 : null));
        placeholders.put("eventDurationMinutes", String.valueOf(eventActive ? (d.getEventDuration() / 20 * 60) % 60 : null));
        placeholders.put("eventDurationHours", String.valueOf(eventActive ? (d.getEventDuration() / (20 * 60 * 60)) % 24 : null));
        placeholders.put("eventDurationDays", String.valueOf(eventActive ? d.getEventDuration() / (20 * 60 * 60 * 24) : null));
        placeholders.put("requiredCrystals", String.valueOf(eventActive ? d.getActiveEvent().getRequiredCrystals() : null));
        placeholders.put("submittedCrystals", String.valueOf(eventActive ? d.getActiveEvent().getCrystalsSubmitted() : null));

        if (message.isJsonArray()) {
            JsonArray messageArray = message.getAsJsonArray();
            JsonArray substitutedArray = new JsonArray();

            for (JsonElement element : messageArray) {
                if (element.isJsonObject()) {
                    JsonObject substitutedElement = substitutePlaceholders(element.getAsJsonObject(), placeholders);
                    substitutedArray.add(substitutedElement);
                } else {
                    substitutedArray.add(element);
                }
            }

            return Component.Serializer.fromJson(substitutedArray.toString());
        } else {
            throw new IllegalStateException("eventMessage must be a JSON array!");
        }
    }

    /**
     * Replaces placeholders in a JSON object with actual values.
     *
     * @param json The JSON object containing the text component data.
     * @param placeholders A map of placeholder keys to their replacement values.
     * @return A JSON object with placeholders substituted.
     */
    private static JsonObject substitutePlaceholders(JsonObject json, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (json.has("text")) {
                String text = json.get("text").getAsString();
                json.addProperty("text", text.replace(placeholder, entry.getValue()));
            }
        }
        return json;
    }



    /**
     *  Getter/Setters
     */

    public ResourceLocation getEventId() {
        return eventId;
    }

    public JsonElement getEventStartMessage() {
        return eventStartMessage;
    }

    public JsonElement getEventEndMessage() {
        return eventEndMessage;
    }

    public JsonElement getEventLoginMessage() {
        return eventLoginMessage;
    }

    public JsonElement getEventDisplayMessage() {
        return eventDisplayMessage;
    }

    public JsonElement getEventEnabledMessage() {
        return eventEnabledMessage;
    }

    public long getEventDuration() {
        return eventDuration;
    }

    public boolean isCrystalSubmission() {
        return crystalSubmission;
    }

    public Map<ResourceLocation, Integer> getAdditionalModifiers() {
        return additionalModifiers;
    }

    public int getMinCrystalsSubmitted() {
        return minCrystalsSubmitted;
    }

    public int getMaxCrystalsSubmitted() {
        return maxCrystalsSubmitted;
    }

    public ItemStack getItemToAdd() {
        return itemToAdd;
    }

    public Float getChance() {
        return chance;
    }
}
