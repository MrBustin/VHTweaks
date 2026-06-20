package io.github.a1qs.vaultadditions.mixins.additions_events;

import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.events.VaultAdditionsEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.stat.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AwardCrateObjective.class, remap = false)
public class MixinAwardCrateObjective {

    @Inject(method = "awardCrate", at = @At("RETURN"))
    private void addBorderShardReward(Vault vault, Listener listener, ChunkRandom random, CallbackInfo ci) {
        StatCollector stats = vault.get(Vault.STATS).get(listener.get(Listener.ID));
        EventData data = EventData.getServer();
        VaultAdditionsEvent event = data.getActiveEvent();
        if (data.conditionsCompleted() && event.is(VaultAdditionsEvent.ADD_VAULT_COMPLETION_ITEM) && random.nextFloat() < event.getChance()) {
            stats.get(StatCollector.REWARD).add(event.getItemStack());
        }
    }
}
