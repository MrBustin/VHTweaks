package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.a1qs.vaultadditions.client.sound.CustomElytraSoundInstance;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ElytraSoundTransmogEffect extends TransmogEffect {
    public static final ElytraSoundTransmogEffect TYPE = new ElytraSoundTransmogEffect(null, 0);
    private final SoundEvent elytraSound;
    private final float elytraVolumeModifier;

    public ElytraSoundTransmogEffect(SoundEvent elytraSound, float elytraVolumeModifier) {
        this.elytraSound = elytraSound;
        this.elytraVolumeModifier = elytraVolumeModifier;
    }

    public CustomElytraSoundInstance createSoundInstance(LocalPlayer player) {
        return new CustomElytraSoundInstance(player, elytraSound, elytraVolumeModifier);
    }

    @Override
    public MutableComponent getTooltip() {
        return null;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = withType();
        json.addProperty("elytraSound", elytraSound.getRegistryName().toString());
        json.addProperty("elytraVolumeModifier", elytraVolumeModifier);
        return json;
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        return new ElytraSoundTransmogEffect(
                ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.tryParse(object.get("elytraSound").getAsString())),
                object.get("elytraVolumeModifier").getAsFloat()
        );
    }
}
