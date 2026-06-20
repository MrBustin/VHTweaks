package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.core.card.CardEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Arrays;

@Mixin(CardEntry.Color.class)
public abstract class CardEntryColorEnumMixin {

    @Shadow
    @Final
    @Mutable
    private static CardEntry.Color[] $VALUES;

    // Calls the real enum constructor:
    // <init>(String enumName, int ordinal, String name, int color)
    @Invoker("<init>")
    private static CardEntry.Color invokeInit(String enumName, int ordinal, String name, int color) {
        throw new AssertionError();
    }

    // Create your new constant(s)
    private static final CardEntry.Color PURPLE =
            add("RED_BLUE", "Red-Blue", 0xA020F0);

    private static CardEntry.Color add(String enumConstantName, String displayName, int color) {
        CardEntry.Color value = invokeInit(enumConstantName, $VALUES.length, displayName, color);

        CardEntry.Color[] newValues = Arrays.copyOf($VALUES, $VALUES.length + 1);
        newValues[newValues.length - 1] = value;
        $VALUES = newValues;

        return value;
    }
}