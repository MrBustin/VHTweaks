package xyz.iwolfking.woldsvaults.init;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.effects.AttributeTrinket;
import iskallia.vault.init.ModGearAttributes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;


public class ModTrinkets {
    public static final AttributeTrinket<Float> MINERS_HARDHAT;

    public static void init(RegistryEvent.Register<TrinketEffect<?>> event) {
        IForgeRegistry<TrinketEffect<?>> registry = event.getRegistry();
        registry.register(MINERS_HARDHAT);
    }

    static {
        MINERS_HARDHAT = new AttributeTrinket<>(VaultMod.id("miners_hardhat"), ModGearAttributes.COPIOUSLY, 0.5F);
    }

}
