package xyz.iwolfking.woldsvaults.modifiers.vault;


import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import xyz.iwolfking.woldsvaults.mixin.accessors.VaultDifficultyAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DifficultyLockModifier extends VaultModifier<DifficultyLockModifier.Properties> {

    private final Map<UUID, VaultDifficulty> RETURN_DIFFICULTY_MAP = new HashMap<>();

    public DifficultyLockModifier(ResourceLocation id, Properties properties, Display display) {
        super(id, properties, display);
    }

    @Override
    public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
            CommonEvents.PLAYER_TICK.register(context.getUUID(), EventPriority.HIGHEST, (event) -> {
                if(event.side.isServer()) {
                    if((event.player.tickCount % 20) != 0) {
                        return;
                    }

                    if(!(event.player.getLevel().dimension().equals(world.dimension()))) {
                        return;
                    }

                    if(vault.get(Vault.CLOCK).has(TickClock.PAUSED)) {
                        return;
                    }

                    if(!event.player.getUUID().equals(vault.get(Vault.OWNER))) {
                        return;
                    }
                    applyDifficulty(world, vault.get(Vault.OWNER));
                }
            });
    }

    @Override
    public void onListenerAdd(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
        vault.ifPresent(Vault.OWNER, owner -> {
            if(listener.getPlayer().isPresent() && listener.getPlayer().get().getUUID().equals(owner)) {
                applyDifficulty(world, owner);
            }
        });
    }

    @Override
    public void onListenerRemove(VirtualWorld world, Vault vault, ModifierContext context, Listener listener) {
            vault.ifPresent(Vault.OWNER, owner -> {
                if(listener.getPlayer().isPresent()) {
                    if(listener.getPlayer().get().getUUID().equals(owner)) {
                        if(RETURN_DIFFICULTY_MAP.containsKey(owner)) {
                            WorldSettings.get(world).setPlayerDifficulty(owner, RETURN_DIFFICULTY_MAP.remove(owner));
                            return;
                        }

                        WorldSettings.get(world).setPlayerDifficulty(owner, VaultDifficulty.NORMAL);
                    }
                }
            });
    }

    private void applyDifficulty(VirtualWorld world, UUID owner) {
        VaultDifficulty currentDifficulty = WorldSettings.get(world).getPlayerDifficulty(owner);
        if(currentDifficulty.equals(properties.getDifficulty())) {
            return;
        }

        if(!properties().shouldLockHigher() && isDifficultyHigher(currentDifficulty)) {
            return;
        }

        RETURN_DIFFICULTY_MAP.putIfAbsent(owner, currentDifficulty);
        WorldSettings.get(world).setPlayerDifficulty(owner, properties.getDifficulty());
    }

    private boolean isDifficultyHigher(VaultDifficulty currentDifficulty) {
        return ((VaultDifficultyAccessor)(Object)currentDifficulty).getDisplayOrder() >= ((VaultDifficultyAccessor)(Object)properties.getDifficulty()).getDisplayOrder();
    }

    public static class Properties {
        @Expose
        private final String difficulty;

        @Expose
        private final boolean lockHigher;

        public Properties(String difficulty, boolean lockHigher) {
            this.difficulty = difficulty;
            this.lockHigher = lockHigher;
        }



        public VaultDifficulty getDifficulty() {
            return VaultDifficulty.valueOf(this.difficulty);
        }

        public boolean shouldLockHigher() {
            return lockHigher;
        }

    }
}
