package xyz.iwolfking.woldsvaults.network.overworld;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class OverworldVaultNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WoldsVaults.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                ClientboundOverworldVaultCompassPacket.class,
                ClientboundOverworldVaultCompassPacket::encode,
                ClientboundOverworldVaultCompassPacket::decode,
                ClientboundOverworldVaultCompassPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ServerboundOverworldVaultGuiRequestPacket.class,
                ServerboundOverworldVaultGuiRequestPacket::encode,
                ServerboundOverworldVaultGuiRequestPacket::decode,
                ServerboundOverworldVaultGuiRequestPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ServerboundOverworldVaultGuiClosePacket.class,
                ServerboundOverworldVaultGuiClosePacket::encode,
                ServerboundOverworldVaultGuiClosePacket::decode,
                ServerboundOverworldVaultGuiClosePacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ClientboundOverworldVaultGuiDataPacket.class,
                ClientboundOverworldVaultGuiDataPacket::encode,
                ClientboundOverworldVaultGuiDataPacket::decode,
                ClientboundOverworldVaultGuiDataPacket::handle
        );
    }
}
