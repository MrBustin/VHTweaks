package xyz.iwolfking.woldsvaults.init;

import iskallia.vault.effect.ThresholdEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.RegistryEvent;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.abilities.ColossusAbility;
import xyz.iwolfking.woldsvaults.abilities.LegacyManaShieldAbility;
import xyz.iwolfking.woldsvaults.abilities.ShieldWallAbility;
import xyz.iwolfking.woldsvaults.abilities.SneakyGetawayAbility;
import xyz.iwolfking.woldsvaults.api.pehkui.CustomScaleTypes;
import xyz.iwolfking.woldsvaults.effect.mobeffects.*;

import java.awt.*;

public class ModEffects {
    public static final MobEffect GROWING = new GrowingPotionEffect(MobEffectCategory.NEUTRAL, 0xc90000, CustomScaleTypes.SIZE, ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID, "growing"));
    public static final MobEffect SHRINKING = new ShrinkingPotionEffect(MobEffectCategory.NEUTRAL, 0xcca468, CustomScaleTypes.SIZE, ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID, "shrinking"));
    public static final MobEffect REAVING = new ReavingPotionEffect(MobEffectCategory.BENEFICIAL, 0xcca468, ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID, "reaving"));
    public static final MobEffect COLOSSUS = new ColossusAbility.ColossusEffect(MobEffectCategory.BENEFICIAL,0xc90000, CustomScaleTypes.SIZE_NO_MOVEMENT, ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID,"colossus"));
    public static final MobEffect SNEAKY_GETAWAY = new SneakyGetawayAbility.SneakyGetawayEffect(MobEffectCategory.BENEFICIAL,0xcca468,CustomScaleTypes.SIZE_NO_MOVEMENT, ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID,"sneaky_getaway"));
    public static final MobEffect SAFER_SPACE = new SaferSpacePotionEffect(MobEffectCategory.BENEFICIAL,0xae6bd1, ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID,"safer_space"));
    public static final MobEffect LEVITATEII = new LevitateIIPotionEffect(MobEffectCategory.BENEFICIAL,0xceffff, ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID,"levitateii"));
    public static final MobEffect HEMORRHAGED = new HemorrhagedEffect(MobEffectCategory.HARMFUL, HemorrhagedEffect.COLOR, WoldsVaults.id(HemorrhagedEffect.ID));
    public static final MobEffect ECHOING = new EchoingPotionEffect(MobEffectCategory.HARMFUL, 0x691997, WoldsVaults.id("echoing"));
    public static final MobEffect SHIELD_WALL = new ShieldWallAbility.ShieldWallEffect(MobEffectCategory.BENEFICIAL, Color.GRAY.getRGB(), WoldsVaults.id("shield_wall"));
    public static final ToggleAbilityEffect LEGACY_MANA_SHIELD = new LegacyManaShieldAbility.LegacyManaShieldEffect(Color.CYAN.getRGB(), WoldsVaults.id("legacy_mana_shield"));
    public static final ThresholdEffect MOMENTUM_ENGINE = new ThresholdEffect(0xceffff, WoldsVaults.id("momentum_engine"));


    public static void register(RegistryEvent.Register<MobEffect> event) {
        event.getRegistry().registerAll(SHRINKING, GROWING, REAVING, COLOSSUS, SNEAKY_GETAWAY, SAFER_SPACE, LEVITATEII, HEMORRHAGED, ECHOING, SHIELD_WALL, LEGACY_MANA_SHIELD, MOMENTUM_ENGINE);
    }
}
