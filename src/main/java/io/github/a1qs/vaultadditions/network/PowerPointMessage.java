package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.util.MiscUtil;
import iskallia.vault.client.gui.screen.player.legacy.ILegacySkillTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PowerPointMessage {

    public final int unspentPowerPoints;

    public PowerPointMessage(int unspentPowerPoints) {
        this.unspentPowerPoints = unspentPowerPoints;
    }

    public static void encode(PowerPointMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.unspentPowerPoints);
    }

    public static PowerPointMessage decode(FriendlyByteBuf buffer) {
        int unspentPowerPoints = buffer.readInt();
        return new PowerPointMessage(unspentPowerPoints);
    }

    public static void handle(PowerPointMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            MiscUtil.unspentPowerPoints = message.unspentPowerPoints;
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof ILegacySkillTreeScreen skillTreeScreen) {
                skillTreeScreen.update();
            }

        });
        context.setPacketHandled(true);
    }
}

