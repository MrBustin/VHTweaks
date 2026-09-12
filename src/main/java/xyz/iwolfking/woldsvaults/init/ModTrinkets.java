package xyz.iwolfking.woldsvaults.init;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.effects.AttributeTrinket;
import iskallia.vault.gear.trinket.effects.PotionEffectTrinket;
import iskallia.vault.gear.trinket.effects.VanillaAttributeTrinket;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.iwolfking.woldsvaults.api.trinket.MultiAttributeTrinket;
import xyz.iwolfking.woldsvaults.effect.trinket.EffectOnHitTakenEffect;

import java.util.List;


public class ModTrinkets {
    public static final AttributeTrinket<Float> MINERS_HARDHAT;
    public static final VanillaAttributeTrinket RUNNING_SHOES;

    public static final AttributeTrinket<Float> CHROMATIC_DIFFUSER;
    public static final MultiAttributeTrinket<Float> VIBRATING_STONE;
    public static final EffectOnHitTakenEffect IMMORTAL_SEAL;
    public static final AttributeTrinket<Float> LUCKY_COINS;

    public static void init(RegistryEvent.Register<TrinketEffect<?>> event) {
        IForgeRegistry<TrinketEffect<?>> registry = event.getRegistry();
        registry.register(MINERS_HARDHAT);
        registry.register(RUNNING_SHOES);
        registry.register(CHROMATIC_DIFFUSER);
        registry.register(VIBRATING_STONE);
        registry.register(IMMORTAL_SEAL);
        registry.register(LUCKY_COINS);
    }

    static {
        MINERS_HARDHAT = new AttributeTrinket<>(VaultMod.id("miners_hardhat"), ModGearAttributes.COPIOUSLY, 0.5F);
        RUNNING_SHOES = new VanillaAttributeTrinket(VaultMod.id("running_shoes"), Attributes.MOVEMENT_SPEED, 0.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        CHROMATIC_DIFFUSER =  new AttributeTrinket<>(VaultMod.id("chromatic_diffuser"), xyz.iwolfking.woldsvaults.init.ModGearAttributes.INCREASED_EFFECT_CLOUD_CHANCE, 0.1F);
        VIBRATING_STONE =  new MultiAttributeTrinket<>(VaultMod.id("vibrating_stone"), List.of(xyz.iwolfking.woldsvaults.init.ModGearAttributes.ECHOING_CHANCE, xyz.iwolfking.woldsvaults.init.ModGearAttributes.ECHOING_DAMAGE), List.of(0.1F, 0.25F));
        IMMORTAL_SEAL =  new EffectOnHitTakenEffect(VaultMod.id("immortal_seal"), 0.05F, ModEffects.IMMORTALITY, 1, 60);
        LUCKY_COINS = new AttributeTrinket<>(VaultMod.id("lucky_coins"), xyz.iwolfking.woldsvaults.init.ModGearAttributes.COIN_DOUBLE_CHANCE, 0.2f);
    }

}
