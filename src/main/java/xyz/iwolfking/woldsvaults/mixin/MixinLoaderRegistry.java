package xyz.iwolfking.woldsvaults.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.vhapi.api.LoaderRegistry;
import xyz.iwolfking.vhapi.api.lib.core.processors.IConfigProcessor;
import xyz.iwolfking.vhapi.api.loaders.Processors;
import xyz.iwolfking.vhapi.api.loaders.lib.core.VaultConfigProcessor;
import xyz.iwolfking.woldsvaults.events.SetupEvents;

import java.util.Iterator;

@Mixin(value = LoaderRegistry.class, remap = false)
public abstract class MixinLoaderRegistry {


    @Inject(method = "initProcessors", at = @At("TAIL"))
    private static void initProcessors(CallbackInfo ci) {
        SetupEvents.addManualConfigs();
    }
}
