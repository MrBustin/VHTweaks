package io.github.a1qs.vaultadditions.mixins.armor_effects.sound;

import io.github.a1qs.vaultadditions.util.SoundChoice;
import io.github.a1qs.vaultadditions.vault.gear.effect.AbilitySoundTransmogEffect;
import iskallia.vault.init.ModAbilities;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.SmiteArchonAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import iskallia.vault.skill.base.SkillContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SmiteArchonAbility.class, remap = false)
public class MixinSmiteArchonAbility extends AbstractSmiteAbility {
    @Override
    protected void doToggleSound(SkillContext context) {
        if (!this.isActive()) {
            return;
        }

        context.getSource().as(ServerPlayer.class).ifPresent((player) -> {
            SoundChoice sound = AbilitySoundTransmogEffect.getSound(player, ModAbilities.SMITE_ARCHON, new SoundChoice(ModSounds.SMITE, 0.5F, 1.0F));
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
            player.playNotifySound(sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
        });
    }
}
