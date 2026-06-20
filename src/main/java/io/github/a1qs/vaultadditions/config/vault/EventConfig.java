package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import io.github.a1qs.vaultadditions.config.vault.entry.EventEntry;
import io.github.a1qs.vaultadditions.events.VaultAdditionsEvent;
import iskallia.vault.config.Config;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;


public class EventConfig extends Config {
    @Expose
    private final WeightedList<EventEntry> eventList = new WeightedList<>();

    @Override
    public String getName() {
        return "vaultadditions_events";
    }

    @Override
    protected void reset() {
        this.eventList.clear();

        // Add entries to the event list
        eventList.add(new EventEntry(
                VaultAdditionsEvent.BORDER_EXPANSION_ENABLED,
                createComplexStartMessage(),
                createComplexEndMessage(),
                createComplexLoginMessage(),
                createComplexDisplayMessage(),
                createComplexEventEnabledMessage(),
                2000L,
                false), 2);

        eventList.add(new EventEntry(
                        VaultAdditionsEvent.ADD_PORTAL_MODIFIERS,
                        createComplexStartMessage(),
                        createComplexEndMessage(),
                        createComplexLoginMessage(),
                        createComplexDisplayMessage(),
                        createComplexEventEnabledMessage(),
                        2000L,
                        true,
                        Map.of(new ResourceLocation("the_vault:energizing"), 3, new ResourceLocation("the_vault:item_rarity"), 3)),
                3);

        eventList.add(new EventEntry(
                        VaultAdditionsEvent.ADD_VAULT_COMPLETION_ITEM,
                        createComplexStartMessage(),
                        createComplexEndMessage(),
                        createComplexLoginMessage(),
                        createComplexDisplayMessage(),
                        createComplexEventEnabledMessage(),
                        2000L,
                        true,
                        new ItemStack(ModItems.ABYSSAL_ICHOR, 5),
                        0.5F),
                3);
    }

    public WeightedList<EventEntry> getWeightedList() {
        return eventList;
    }




    private JsonElement createComplexStartMessage() {
        JsonArray jsonArray = new JsonArray();

        // Adding styled text components
        JsonObject part1 = new JsonObject();
        part1.addProperty("text", "The Event {eventId}");
        part1.addProperty("color", "yellow");

        JsonObject part2 = new JsonObject();
        part2.addProperty("text", "is now active");
        part2.addProperty("color", "green");
        part2.addProperty("bold", true);

        // Add parts to the array
        jsonArray.add(part1);
        jsonArray.add(part2);

        return jsonArray;
    }

    private JsonElement createComplexEndMessage() {
        JsonArray jsonArray = new JsonArray();

        // Adding styled text components
        JsonObject part1 = new JsonObject();
        part1.addProperty("text", "The event {eventId} has ended.");
        part1.addProperty("color", "yellow");
        part1.addProperty("bold", true);

        JsonObject part2 = new JsonObject();
        part2.addProperty("text", " {submittedCrystals} / {requiredCrystals} Crystals were submitted");
        part2.addProperty("color", "gold");

        // Add parts to the array
        jsonArray.add(part1);
        jsonArray.add(part2);

        return jsonArray;
    }

    private JsonElement createComplexLoginMessage() {
        JsonArray jsonArray = new JsonArray();

        // Adding styled text components
        JsonObject part1 = new JsonObject();
        part1.addProperty("text", "You logged in with an active event!\n");
        part1.addProperty("color", "green");

        JsonObject part2 = new JsonObject();
        part2.addProperty("text", "Time Remaining: {eventDurationDays}d, {eventDurationHours}h, {eventDurationMinutes}m, {eventDurationSeconds}s");
        part2.addProperty("color", "red");

        // Add parts to the array
        jsonArray.add(part1);
        jsonArray.add(part2);

        return jsonArray;
    }

    private JsonElement createComplexDisplayMessage() {
        JsonArray jsonArray = new JsonArray();

        // Adding styled text components
        JsonObject part1 = new JsonObject();
        part1.addProperty("text", "This is an example display message for the Event Block!");
        part1.addProperty("color", "light_purple");

        // Add parts to the array
        jsonArray.add(part1);

        return jsonArray;
    }

    private JsonElement createComplexEventEnabledMessage() {
        JsonArray jsonArray = new JsonArray();

        // Adding styled text components
        JsonObject part1 = new JsonObject();
        part1.addProperty("text", "The Event is now enabled!\n");
        part1.addProperty("color", "light_purple");


        JsonObject part2 = new JsonObject();
        part2.addProperty("text", "{submittedCrystals} / {requiredCrystals} were submitted and the goal was reached!");
        part2.addProperty("color", "red");


        // Add parts to the array
        jsonArray.add(part1);
        jsonArray.add(part2);

        return jsonArray;
    }
}
