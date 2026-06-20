package io.github.a1qs.vaultadditions.mixins.accessors;

import iskallia.vault.snapshot.AttributeSnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AttributeSnapshot.AttributeValue.class, remap = false)
public interface InvokeAttributeSnapshotAttributeValue {
    @Invoker("<init>")
    static <T, V> AttributeSnapshot.AttributeValue<T, V> invokeConstructor() {
        throw new AssertionError();
    }

    @Invoker(value = "addCachedValue", remap = false)
    void invokeAddCachedValue(Object object);
}
