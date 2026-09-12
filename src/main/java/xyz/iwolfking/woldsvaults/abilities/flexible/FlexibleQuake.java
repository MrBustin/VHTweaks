package xyz.iwolfking.woldsvaults.abilities.flexible;

import iskallia.vault.mana.FullManaPlayer;
import iskallia.vault.skill.ability.effect.EarthquakeAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.source.SkillSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class FlexibleQuake extends EarthquakeAbility {
    public void cast(Player player, Entity target, EarthquakeAbility ability) {
        ability.onAction(SkillContext.of((ServerPlayer) player, SkillSource.of(player).setPos(target.position()).setMana(FullManaPlayer.INSTANCE)));
    }
}
