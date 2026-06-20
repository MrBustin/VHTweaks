package xyz.iwolfking.woldsvaults.mixin.angelring;

import dev.denismasterherobrine.angelring.AngelRing;
import dev.denismasterherobrine.angelring.item.vanilla.ItemRing;
import dev.denismasterherobrine.angelring.register.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AngelRing.RegistryEvents.class, remap = false)
public abstract class MixinItemRegistry {

    @Shadow
    private static ResourceLocation location(String name) {
        return null;
    }

    /**
     * @author iwolfking
     * @reason Remove all but Diamond Ring
     */
    @Inject(method = "RegisterItems", at = @At("HEAD"), cancellable = true)
    private static void RegisterItems(RegistryEvent.Register<Item> event, CallbackInfo ci) {
        event.getRegistry().register(ItemRegistry.ItemRing = (new ItemRing()).setRegistryName(location("itemring")));
        ci.cancel();
    }
}
