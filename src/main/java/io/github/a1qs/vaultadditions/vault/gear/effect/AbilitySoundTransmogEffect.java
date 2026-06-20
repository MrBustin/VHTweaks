package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.util.ModelUtil;
import io.github.a1qs.vaultadditions.util.SoundChoice;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class AbilitySoundTransmogEffect extends TransmogEffect {
    public static final AbilitySoundTransmogEffect TYPE = new AbilitySoundTransmogEffect(null, null);
    private final String ability;
    private final SoundChoice sound;

    public AbilitySoundTransmogEffect(String ability, SoundChoice sound) {
        this.ability = ability;
        this.sound = sound;
    }

    @Override
    public MutableComponent getTooltip() {
        return null;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = withType();
        json.addProperty("ability", ability);
        JsonObject sound = new JsonObject();
        sound.addProperty("event_id", this.sound.event().getLocation().toString());
        sound.addProperty("volume", this.sound.volume());
        sound.addProperty("pitch", this.sound.pitch());
        json.add("sound", sound);
        return json;
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        String ability = object.get("ability").getAsString();
        JsonObject sound = object.getAsJsonObject("sound");
        SoundChoice soundChoice = new SoundChoice(
                ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.tryParse(sound.get("event_id").getAsString())),
                sound.get("volume").getAsFloat(),
                sound.get("pitch").getAsFloat()
        );
        return new AbilitySoundTransmogEffect(ability, soundChoice);
    }

    public static SoundChoice getSound(ServerPlayer player, String ability, SoundChoice def) {
        return getSound(player, ability, 0, def);
    }

    public static SoundChoice getSound(ServerPlayer player, String ability, int index, SoundChoice def) {
        ArmorModel wornModel = ModelUtil.getWornSet(player);
        List<AbilitySoundTransmogEffect> effects = Configs.TRANSMOG_EFFECTS_CONFIG.getEffects(wornModel, AbilitySoundTransmogEffect.class);
        effects.removeIf(effect -> !effect.ability.equals(ability));
        if (effects.size() > index) {
            return effects.get(index).sound.extend(def);
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            effects = Configs.TRANSMOG_EFFECTS_CONFIG.getEffects(player.getItemBySlot(slot), AbilitySoundTransmogEffect.class);
            effects.removeIf(effect -> !effect.ability.equals(ability));
            if (effects.size() > index) {
                return effects.get(index).sound.extend(def);
            }
        }
        return def;
    }
}
