package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.chat.MutableComponent;

public class HideElytraTransmogEffect extends TransmogEffect {
    public static final HideElytraTransmogEffect TYPE = new HideElytraTransmogEffect();
    private HideElytraTransmogEffect() {}

    @Override
    public MutableComponent getTooltip() {
        return null;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(type());
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        return this;
    }
}
