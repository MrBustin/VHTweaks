package io.github.a1qs.vaultadditions.util;

import net.minecraft.sounds.SoundEvent;

public record SoundChoice(SoundEvent event, float volume, float pitch) {
    public SoundChoice(SoundEvent event) {
        this(event, -1, -1);
    }

    public SoundChoice extend(SoundChoice def) {
        if (volume != -1 && pitch != -1) {
            return this;
        }
        return new SoundChoice(event, volume == -1 ? def.volume : volume, pitch == -1 ? def.pitch : pitch);
    }
}
