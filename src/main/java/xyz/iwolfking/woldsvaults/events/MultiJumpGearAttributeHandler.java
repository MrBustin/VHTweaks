package xyz.iwolfking.woldsvaults.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.WoldsVaults;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = WoldsVaults.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MultiJumpGearAttributeHandler {
    private static final Map<ServerPlayer, ServerJumpState> SERVER_JUMP_STATES = new WeakHashMap<>();

    public static boolean tryUseServerJump(ServerPlayer player, int maxExtraJumps) {
        ServerJumpState state = SERVER_JUMP_STATES.get(player);
        if (state != null && state.hasBeenAirborne && player.isOnGround()) {
            state = null;
        }
        if (state == null) {
            state = new ServerJumpState();
            SERVER_JUMP_STATES.put(player, state);
        }

        if (state.usedJumps >= maxExtraJumps) {
            return false;
        }

        state.usedJumps++;
        return true;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient() || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        ServerJumpState state = SERVER_JUMP_STATES.get(player);
        if (player.isSpectator() || player.getAbilities().flying) {
            SERVER_JUMP_STATES.remove(player);
        } else if (state != null && !player.isOnGround()) {
            state.hasBeenAirborne = true;
        } else if (state != null && state.hasBeenAirborne) {
            SERVER_JUMP_STATES.remove(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            SERVER_JUMP_STATES.remove(player);
        }
    }

    private static final class ServerJumpState {
        private int usedJumps;
        private boolean hasBeenAirborne;
    }
}
