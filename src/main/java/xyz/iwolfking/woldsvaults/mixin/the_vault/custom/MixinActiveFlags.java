package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.event.ActiveFlags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value = ActiveFlags.class, remap = false)
public class MixinActiveFlags {
    @Shadow
    @Final
    @Mutable
    @SuppressWarnings("target")
    private static ActiveFlags[] $VALUES;

    @Invoker("<init>")
    public static ActiveFlags woldsvaults$invokeInit(String name, int ordinal) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void woldsvaults$addFireNovaFlag(CallbackInfo ci) {
        int ordinal = $VALUES.length;
        ActiveFlags fireNova = woldsvaults$invokeInit("IS_FIRE_NOVA_ATTACKING", ordinal);
        $VALUES = Arrays.copyOf($VALUES, ordinal + 1);
        $VALUES[ordinal] = fireNova;
    }
}
