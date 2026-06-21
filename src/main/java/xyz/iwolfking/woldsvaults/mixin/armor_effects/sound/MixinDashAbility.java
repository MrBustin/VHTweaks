package xyz.iwolfking.woldsvaults.mixin.armor_effects.sound;

import xyz.iwolfking.woldsvaults.init.ModSounds;
import xyz.iwolfking.woldsvaults.util.DateHelper;
import xyz.iwolfking.woldsvaults.util.SoundChoice;
import xyz.iwolfking.woldsvaults.gear.effect.AbilitySoundTransmogEffect;
import iskallia.vault.init.ModAbilities;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DashAbility.class, remap = false)
public class MixinDashAbility extends Ability {

    @Inject(method = "doSound", at = @At("HEAD"), cancellable = true)
    private void injectSoundEvents(SkillContext context, CallbackInfo ci) {
        context.getSource().as(ServerPlayer.class).ifPresent(player -> {
            SoundChoice def = new SoundChoice(null, 0.2F, 1.0F);
            SoundChoice sound = AbilitySoundTransmogEffect.getSound(player, ModAbilities.DASH, def);
            if (sound.event() != null) {
                player.level.playSound(player, player.getX(), player.getY(), player.getZ(), sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
                player.playNotifySound(sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
                ci.cancel();
            }
        });

        if (!DateHelper.isAprilFools()) return;

        context.getSource().as(ServerPlayer.class).ifPresent(player -> {
            player.level.playSound(
                    player,
                    player.getX(), player.getY(), player.getZ(),
                    ModSounds.FART.get(),
                    SoundSource.PLAYERS,
                    0.5F,
                    1.0F
            );

            player.playNotifySound(
                    ModSounds.FART.get(),
                    SoundSource.PLAYERS,
                    0.5F,
                    1.0F
            );
            ci.cancel();
        });
    }
}
