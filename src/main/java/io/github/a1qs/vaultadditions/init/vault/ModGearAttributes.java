package io.github.a1qs.vaultadditions.init.vault;

import com.mojang.datafixers.util.Pair;
import io.github.a1qs.vaultadditions.VaultAdditions;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModGearAttributeGenerators;
import iskallia.vault.init.ModGearAttributeReaders;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModGearAttributes {
    public static final VaultGearAttribute<Boolean> BREACHING = attr("breaching",
            VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(),
            ModGearAttributeReaders.booleanReader("Breaching", 10031431),
            VaultGearAttributeComparator.booleanComparator()
    );

    public static final VaultGearAttribute<Float> SIZE_SCALE = attr("size_scale",
            VaultGearAttributeType.floatType(),
            ModGearAttributeGenerators.floatRange(),
            ModGearAttributeReaders.percentageReader("Size Scale", 0xb891c4),
            VaultGearAttributeComparator.floatComparator()
    );

    public static final VaultGearAttribute<Float> KINETIC_DAMAGE_REDUCTION_PERCENT = attr("kinetic_damage_reduction_percent",
            VaultGearAttributeType.floatType(),
            ModGearAttributeGenerators.floatRange(),
            ModGearAttributeReaders.percentageReader("Kinetic Damage Reduction", 0xb891c4),
            VaultGearAttributeComparator.floatComparator()
    );

    public static final VaultGearAttribute<Float> FALL_DAMAGE_REDUCTION_PERCENT = attr("fall_damage_reduction_percent",
            VaultGearAttributeType.floatType(),
            ModGearAttributeGenerators.floatRange(),
            ModGearAttributeReaders.percentageReader("Fall Damage Reduction", 0x00C7C7),
            VaultGearAttributeComparator.floatComparator()
    );

    public static final VaultGearAttribute<Float> AIR_MOBILITY_SPEED = attr("air_mobility_speed",
            VaultGearAttributeType.floatType(),
            ModGearAttributeGenerators.floatRange(),
            ModGearAttributeReaders.percentageReader("Air Mobility Speed", 0x00C764),
            VaultGearAttributeComparator.floatComparator()
    );

    public static final VaultGearAttribute<Float> AIR_MOBILITY_CONTROL = attr("air_mobility_control",
            VaultGearAttributeType.floatType(),
            ModGearAttributeGenerators.floatRange(),
            ModGearAttributeReaders.percentageReader("Air Mobility Control", 0x00C764),
            VaultGearAttributeComparator.floatComparator()
    );

    // Legacy pair attribute retained so older transmog configs can deserialize and migrate cleanly.
    public static final VaultGearAttribute<Pair<Float, Float>> AIR_MOBILITY = attr("air_mobility",
            VaultGearAttributeType.pairType(VaultGearAttributeType.floatType(), VaultGearAttributeType.floatType()),
            ModGearAttributeGenerators.pairGenerator(ModGearAttributeGenerators.floatRange(), ModGearAttributeGenerators.floatRange()),
            ModGearAttributeReaders.pairReader("Air Mobility", 0x00C764,
                    (speed, airMovement) -> new TextComponent(String.format(Locale.ROOT, "+%.0f%% speed, +%.0f%% air", speed * 100.0F, airMovement * 100.0F))),
            null
    );


    @SubscribeEvent
    public static void init(RegistryEvent.Register<VaultGearAttribute<?>> event) {
        IForgeRegistry<VaultGearAttribute<?>> registry = event.getRegistry();

        registry.register(BREACHING);
        registry.register(SIZE_SCALE);
        registry.register(KINETIC_DAMAGE_REDUCTION_PERCENT);
        registry.register(FALL_DAMAGE_REDUCTION_PERCENT);
        registry.register(AIR_MOBILITY_SPEED);
        registry.register(AIR_MOBILITY_CONTROL);
        registry.register(AIR_MOBILITY);
    }

    private static <T> VaultGearAttribute<T> attr(String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader, @Nullable VaultGearAttributeComparator<T> comparator) {
        return new VaultGearAttribute<>(VaultMod.id(name), type, generator, reader, comparator);
    }

}
