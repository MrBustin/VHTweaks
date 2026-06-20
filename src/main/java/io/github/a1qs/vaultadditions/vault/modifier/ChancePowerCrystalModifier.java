package io.github.a1qs.vaultadditions.vault.modifier;

import io.github.a1qs.vaultadditions.events.VaultCommonEvents;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.AbstractChanceModifier;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public class ChancePowerCrystalModifier extends AbstractChanceModifier<AbstractChanceModifier.Properties> {
    public ChancePowerCrystalModifier(ResourceLocation id, AbstractChanceModifier.Properties properties, VaultModifier.Display display) {
        super(id, properties, display);
    }

    public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
        VaultCommonEvents.CHEST_POWER_CRYSTAL_GENERATION_EVENT.register(context.getUUID(), (data) -> {
            if (data.getPlayer().level == world) {
                if (!context.hasTarget() || context.getTarget().equals(data.getPlayer().getUUID())) {
                    data.setProbability(data.getProbability() + (double)(this.properties).getChance(context));
                }
            }
        });
    }
}

