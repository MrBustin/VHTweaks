package io.github.a1qs.vaultadditions.mixins;

import iskallia.vault.client.gui.screen.summary.element.CombatStatsContainerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;

@Mixin(value = CombatStatsContainerElement.class, remap = false)
public class MixinCombatStatsContainerElement {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    public <K, V> V init(Map<K, V> instance, K o) {
        return instance.getOrDefault(o, (V) List.of());
    }
}
