package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeSerializer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.GearDataVersion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.MutableComponent;

public class AttributeTransmogEffect<T> extends TransmogEffect {
    public static final AttributeTransmogEffect<?> TYPE = new AttributeTransmogEffect<>(null);
    private final VaultGearAttributeInstance<T> instance;

    public AttributeTransmogEffect(VaultGearAttributeInstance<T> instance) {
        this.instance = instance;
    }

    public VaultGearAttributeInstance<T> getVaultGearAttributeInstance() {
        return instance;
    }

    @Override
    public MutableComponent getTooltip() {
        return this.getVaultGearAttributeInstance().getAttribute().getReader().getDisplay(this.getVaultGearAttributeInstance(), VaultGearModifier.AffixType.PREFIX);
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = withType();
        CompoundTag instance = new CompoundTag();
        instance.put("instance", VaultGearAttributeSerializer.serializeTag(this.instance));
        json.addProperty("instance", instance.toString());
        return json;
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        try {
            CompoundTag instance = TagParser.parseTag(object.get("instance").getAsString());
            return cast(VaultGearAttributeSerializer.deserializeTag(instance.getCompound("instance"), GearDataVersion.V0_7));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> AttributeTransmogEffect<T> cast(VaultGearAttributeInstance<T> instance) {
        return new AttributeTransmogEffect<>(instance);
    }
}
