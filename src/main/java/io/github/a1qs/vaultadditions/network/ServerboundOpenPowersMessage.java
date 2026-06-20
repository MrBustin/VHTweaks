package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.data.PlayerPowersData;
import io.github.a1qs.vaultadditions.init.ModContainers;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.core.net.ArrayBitBuffer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class ServerboundOpenPowersMessage {
    public static final ServerboundOpenPowersMessage INSTANCE = new ServerboundOpenPowersMessage();

    public ServerboundOpenPowersMessage() {
    }

    public static void encode(ServerboundOpenPowersMessage message, FriendlyByteBuf buffer) {
    }

    public static ServerboundOpenPowersMessage decode(FriendlyByteBuf buffer) {
        return INSTANCE;
    }

    public static void handle(ServerboundOpenPowersMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                PlayerPowersData playerPowersData = PlayerPowersData.get(sender.getLevel());
                final PowerTree powerTree = playerPowersData.getPowers(sender);
                NetworkHooks.openGui(sender, new MenuProvider() {
                    @Nonnull
                    public Component getDisplayName() {
                        return new TranslatableComponent("container.vault.power");
                    }

                    @ParametersAreNonnullByDefault
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        return new NBTElementContainer<>(() -> ModContainers.POWERS_TAB_CONTAINER, i, playerInventory.player, powerTree);
                    }
                }, (buffer) -> {
                    ArrayBitBuffer buffer1 = ArrayBitBuffer.empty();
                    powerTree.writeBits(buffer1);
                    buffer.writeLongArray(buffer1.toLongArray());
                });
            }
        });
        context.setPacketHandled(true);
    }
}
