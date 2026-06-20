package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.event.ClientEvents;
import net.minecraftforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ClientEvents.class, remap = false)
public class MixinClientEvents {

    /**
     * @author iwolfking
     * @reason Remove supporters button.
     */
    @Overwrite
    public static void onGuiInit(ScreenEvent.InitScreenEvent event) {
    }
}
