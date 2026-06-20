package io.github.a1qs.vaultadditions.events;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.config.Configs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class VaultAdditionsEvent {
    public static final ResourceLocation BORDER_EXPANSION_ENABLED = new ResourceLocation(VaultAdditions.MOD_ID, "event_re_enable_border_expansion");
    public static final ResourceLocation ADD_PORTAL_MODIFIERS = new ResourceLocation(VaultAdditions.MOD_ID, "event_add_portal_modifiers");
    public static final ResourceLocation ADD_VAULT_COMPLETION_ITEM = new ResourceLocation(VaultAdditions.MOD_ID, "event_vault_completion_item");

    private final int configIndex;
    private int requiredCrystals;
    private int crystalsSubmitted;

    public VaultAdditionsEvent(int configIndex, int requiredCrystals, int crystalsSubmitted) {
        this.configIndex = configIndex;
        this.requiredCrystals = requiredCrystals;
        this.crystalsSubmitted = crystalsSubmitted;
    }

    public static CompoundTag serialize(VaultAdditionsEvent event) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ConfigIndex", event.getConfigIndex());
        tag.putInt("RequiredCrystals", event.getRequiredCrystals());
        tag.putInt("SubmittedCrystals", event.getCrystalsSubmitted());
        return tag;
    }

    public static VaultAdditionsEvent deserialize(CompoundTag tag) {
        return new VaultAdditionsEvent(
                tag.getInt("ConfigIndex"),
                tag.getInt("RequiredCrystals"),
                tag.getInt("SubmittedCrystals")
        );
    }

    public boolean is(ResourceLocation id) {
        return getEventId().equals(id);
    }

    public int getRequiredCrystals() {
        return requiredCrystals;
    }

    public int getCrystalsSubmitted() {
        return crystalsSubmitted;
    }

    public void setRequiredCrystals(int requiredCrystals) {
        this.requiredCrystals = requiredCrystals;
    }

    public void addCrystalsSubmitted(int crystalsSubmitted) {
        this.crystalsSubmitted = Math.min(this.crystalsSubmitted + crystalsSubmitted, getRequiredCrystals());
    }

    public boolean isModifierActive() {
        return this.crystalsSubmitted >= requiredCrystals;
    }

    public int getConfigIndex() {
        return configIndex;
    }

    // Event data getters

    public ResourceLocation getEventId() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex()).value.getEventId();
    }

    public Component getEventStartMessage() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex())
                .value.getParsedMessage(
                        Configs.EVENT_CONFIG.getWeightedList().get(
                                this.getConfigIndex()
                        ).value.getEventStartMessage());
    }

    public Component getEventEndMessage() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex())
                .value.getParsedMessage(
                        Configs.EVENT_CONFIG.getWeightedList().get(
                                this.getConfigIndex()
                        ).value.getEventEndMessage());
    }

    public Component getEventLoginMessage() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex())
                .value.getParsedMessage(
                        Configs.EVENT_CONFIG.getWeightedList().get(
                                this.getConfigIndex()
                        ).value.getEventLoginMessage());
    }

    public Component getEventDisplayMessage() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex())
                .value.getParsedMessage(
                        Configs.EVENT_CONFIG.getWeightedList().get(
                                this.getConfigIndex()
                        ).value.getEventDisplayMessage());
    }

    public Component getEventEnabledMessage() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex())
                .value.getParsedMessage(
                        Configs.EVENT_CONFIG.getWeightedList().get(
                                this.getConfigIndex()
                        ).value.getEventEnabledMessage());
    }

    public boolean isCrystalSubmissionEvent() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex()).value.isCrystalSubmission();
    }

    public Map<ResourceLocation, Integer> getAdditionalModifiers() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex()).value.getAdditionalModifiers();
    }

    public long getEventDuration() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex()).value.getEventDuration();
    }

    public ItemStack getItemStack() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex()).value.getItemToAdd();
    }

    public float getChance() {
        return Configs.EVENT_CONFIG.getWeightedList().get(this.getConfigIndex()).value.getChance();
    }

}
