package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.vault.skill.ability.LegacyManaShieldAbility;
import io.github.a1qs.vaultadditions.vault.skill.ability.ShieldWallAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.RegistryEvent;

import java.awt.*;

public class ModEffects {
    public static final MobEffect SHIELD_WALL;
    public static final ToggleAbilityEffect LEGACY_MANA_SHIELD;

    public static void registerEffects(RegistryEvent.Register<MobEffect> event) {
        event.getRegistry().register(SHIELD_WALL);
        event.getRegistry().register(LEGACY_MANA_SHIELD);
    }

    static {
        SHIELD_WALL = new ShieldWallAbility.ShieldWallEffect(MobEffectCategory.BENEFICIAL, Color.GRAY.getRGB(), VaultAdditions.id("shield_wall"));
        LEGACY_MANA_SHIELD = new LegacyManaShieldAbility.LegacyManaShieldEffect(Color.CYAN.getRGB(), VaultAdditions.id("legacy_mana_shield"));
    }
}
