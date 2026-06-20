package xyz.iwolfking.woldsvaults.init;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectTrialAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModGearAttributeGenerators;
import iskallia.vault.init.ModGearAttributeReaders;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.iwolfking.woldsvaults.effect.mobeffects.HemorrhagedEffect;
import xyz.iwolfking.woldsvaults.gear.attribute.custom.PoisonTrailReader;
import xyz.iwolfking.woldsvaults.util.UniqueEffectGearAttribute;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = "woldsvaults", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModGearAttributes {
    public static final VaultGearAttribute<Boolean> ROTATING_TOOL = attr("rotating", VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.booleanReader("Stylish", 15378160), VaultGearAttributeComparator.booleanComparator());
    public static final VaultGearAttribute<Integer> TRIDENT_LOYALTY = attr("trident_loyalty",
            VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Loyalty", 3114911), VaultGearAttributeComparator.intComparator());
    public static final VaultGearAttribute<Integer> TRIDENT_RIPTIDE = attr("trident_riptide",
            VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Riptide", 9514005), VaultGearAttributeComparator.intComparator());
    public static final VaultGearAttribute<Float> TRIDENT_WINDUP = attr("trident_wind_up_percent",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Windup Time Reduction", 12925717), VaultGearAttributeComparator.floatComparator());
    public static final VaultGearAttribute<Boolean> TRIDENT_CHANNELING = attr("trident_channeling",
            VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.booleanReader("Channeling", 12925823), VaultGearAttributeComparator.booleanComparator());

    public static final VaultGearAttribute<Float> CHANNELING_CHANCE = attr("trident_channeling_chance",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Channeling Chance", 12925893), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Boolean> DISCHARGE = attr("discharge",
            VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.booleanReader("Discharge", 16777037), VaultGearAttributeComparator.booleanComparator());

    public static final VaultGearAttribute<Float> COIN_DOUBLE_CHANCE = attr("coin_double_chance",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Piggy Procc", 14922686), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Boolean> TREASURE_AFFINITY = attr("treasure_affinity",
            VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.booleanReader("Treasure Affinity", 16749824), VaultGearAttributeComparator.booleanComparator());

    public static final VaultGearAttribute<Integer> PIERCING = attr("piercing",
            VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Piercing", 8847359), VaultGearAttributeComparator.intComparator());

    public static final VaultGearAttribute<Float> RETURNING_DAMAGE = attr("returning_damage",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Returning Damage", 8833629), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> HEXING_CHANCE = attr("hexing_chance",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Hexing Chance", 11468966), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> CHAINING_DAMAGE = attr("chaining_damage",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Reduced Chaining Falloff", 6119096), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> EXECUTION_DAMAGE = attr("execution_damage",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Execution Damage", 10302464), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> THORNS_ADAPTATION = attr("thorns_scaling_damage",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Thorns Adaptation", 3134464), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> AP_SCALING_DAMAGE = attr("ap_scaling_damage",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Ability Power Scaling", 11422101), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<EffectGearAttribute> UNIQUE_EFFECT = attr("unique_effect",
            EffectGearAttribute.type(), EffectGearAttribute.generator(), UniqueEffectGearAttribute.reader());

    public static final VaultGearAttribute<Boolean> HEMORRHAGING = attr("hemorrhaging",
            VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.booleanReader("Hemorrhaging", HemorrhagedEffect.COLOR), VaultGearAttributeComparator.booleanComparator());

    public static final VaultGearAttribute<Boolean> MAGNET_ENDERGIZED = attr("endergized",
            VaultGearAttributeType.booleanType(), ModGearAttributeGenerators.booleanFlag(), ModGearAttributeReaders.booleanReader("Endergized", 46276), VaultGearAttributeComparator.booleanComparator());


    public static final VaultGearAttribute<Float> REAVING_DAMAGE = attr("reaving_damage",
            VaultGearAttributeType.floatType(), (ConfigurableAttributeGenerator<Float, ?>)ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Bonus Reaving Damage", 12417954), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> STUNNING_BLOCK = attr("stunning_block",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Stunning Block Chance", 6970061), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<EffectTrialAttribute> POISON_TRAIL = attr("poison_trail",
            EffectTrialAttribute.type(), EffectTrialAttribute.generator(), new PoisonTrailReader(), EffectTrialAttribute.comparator());


    public static final VaultGearAttribute<Float> ECHOING_CHANCE = attr("echoing_chance",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Echoing Chance", 6886199), VaultGearAttributeComparator.floatComparator());
    public static final VaultGearAttribute<Float> ECHOING_DAMAGE = attr("echoing_damage",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Increased Echoing Damage", 6886199), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> ECHOING_DAMAGE_CAP = attr("echoing_damage_cap",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Increased Echoing Damage Cap", 6886199), VaultGearAttributeComparator.floatComparator());


    public static final VaultGearAttribute<Float> MANA_COST_REDUCTION = attr("mana_cost_reduction",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Mana Cost Reduction", 20220), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> MANA_COST_REDUCTION_CAP = attr("mana_cost_reduction_cap",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Mana Cost Reduction Cap", 20220), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Float> SWIFT_SNEAK = attr("swift_sneak",
            VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.percentageReader("Swift Sneak", 8356289), VaultGearAttributeComparator.floatComparator());

    public static final VaultGearAttribute<Integer> RICOCHET = attr("ricochet",
            VaultGearAttributeType.intType(), ModGearAttributeGenerators.intRange(), ModGearAttributeReaders.addedIntReader("Ricochet", 5930400), VaultGearAttributeComparator.intComparator());

    @SubscribeEvent
    public static void init(RegistryEvent.Register<VaultGearAttribute<?>> event) {
        IForgeRegistry<VaultGearAttribute<?>> registry = event.getRegistry();
                      registry.register(TRIDENT_LOYALTY);
                      registry.register(TRIDENT_RIPTIDE);
                      registry.register(TRIDENT_WINDUP);
                      registry.register(TRIDENT_CHANNELING);
                      registry.register(CHANNELING_CHANCE);
                      registry.register(REAVING_DAMAGE);
                      registry.register(TREASURE_AFFINITY);
                      registry.register(ROTATING_TOOL);
                      registry.register(PIERCING);
                      registry.register(RETURNING_DAMAGE);
                      registry.register(HEXING_CHANCE);
                      registry.register(CHAINING_DAMAGE);
                      registry.register(EXECUTION_DAMAGE);
                      registry.register(THORNS_ADAPTATION);
                      registry.register(AP_SCALING_DAMAGE);
                      registry.register(UNIQUE_EFFECT);
                      registry.register(MAGNET_ENDERGIZED);
                      registry.register(DISCHARGE);
                      registry.register(COIN_DOUBLE_CHANCE);
                      registry.register(HEMORRHAGING);
                      registry.register(STUNNING_BLOCK);
                      registry.register(POISON_TRAIL);
                      registry.register(ECHOING_CHANCE);
                      registry.register(ECHOING_DAMAGE);
                      registry.register(MANA_COST_REDUCTION);
                      registry.register(MANA_COST_REDUCTION_CAP);
                      registry.register(ECHOING_DAMAGE_CAP);
                      registry.register(SWIFT_SNEAK);
                      registry.register(RICOCHET);

    }

   public static void registerVanillaAssociations() {

   }




   private static <T> VaultGearAttribute<T> attr(String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader) {
     return attr(name, type, generator, reader, null);
   }


   private static <T> VaultGearAttribute<T> attr(String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader, @Nullable VaultGearAttributeComparator<T> comparator) {
     return new VaultGearAttribute<>(VaultMod.id(name), type, generator, reader, comparator);
   }
}
