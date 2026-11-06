package xyz.iwolfking.woldsvaults.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.api.helper.MultiJumpGearAttributeHelper;
import xyz.iwolfking.woldsvaults.events.MultiJumpGearAttributeHandler;

import java.util.function.Supplier;

/** Sent when the local player uses one of their gear-granted extra jumps. */
public class ServerboundMultiJumpMessage {
    public static void encode(ServerboundMultiJumpMessage message, FriendlyByteBuf buffer) {}

    public static ServerboundMultiJumpMessage decode(FriendlyByteBuf buffer) {
        return new ServerboundMultiJumpMessage();
    }

    public static void handle(ServerboundMultiJumpMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.isSpectator() || player.getAbilities().flying) {
                return;
            }

            int maxExtraJumps = MultiJumpGearAttributeHelper.getExtraJumpCount(player);
            if (maxExtraJumps <= 0 ||
                    !MultiJumpGearAttributeHandler.tryUseServerJump(player, maxExtraJumps)) {
                return;
            }

            player.jumpFromGround();
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.x, movement.y + 0.2, movement.z);
            player.hasImpulse = true;
        });
        context.setPacketHandled(true);
    }
}
