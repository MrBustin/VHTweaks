package xyz.iwolfking.woldsvaults.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.network.message.BrewingAltarParticleMessage;
import xyz.iwolfking.woldsvaults.network.packets.StopFlightMessage;

public class PacketHandler {
    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID, "woldsvaults"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void init() {
        channel.registerMessage(id++, StopFlightMessage.class, StopFlightMessage::encode, StopFlightMessage::decode, StopFlightMessage::handle);
        channel.registerMessage(id++, BrewingAltarParticleMessage.class, BrewingAltarParticleMessage::encode, BrewingAltarParticleMessage::decode, BrewingAltarParticleMessage::handle);
    }

    public static void sendStopFlightToPlayer(ServerPlayer player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), new StopFlightMessage(player.getUUID()));
    }
}
