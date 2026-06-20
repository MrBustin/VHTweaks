package io.github.a1qs.vaultadditions.vault.menu;


import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.KnownPowerMessage;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.SkillTree;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

public class PowerTree extends SkillTree {
    public void sync(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent(player ->
                ModNetwork.CHANNEL.sendTo(new KnownPowerMessage(this), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
    }
}