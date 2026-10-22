package xyz.iwolfking.woldsvaults.init;

import iskallia.vault.gear.modification.GearModification;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.iwolfking.woldsvaults.api.gear.modification.BlazingGearModification;
import xyz.iwolfking.woldsvaults.api.gear.modification.EmpoweredFundamentalGearModification;
import xyz.iwolfking.woldsvaults.api.gear.modification.unusual.UnusualGearModification;

public class ModGearModifications {
    public static final UnusualGearModification UNUSUAL_GEAR_MODIFICATION = new UnusualGearModification();
    public static final BlazingGearModification BLAZING_GEAR_MODIFICATION = new BlazingGearModification();
    public static final EmpoweredFundamentalGearModification EMPOWERED_FUNDAMENTAL_GEAR_MODIFICATION = new EmpoweredFundamentalGearModification();

    public static void init(RegistryEvent.Register<GearModification> event){
        IForgeRegistry<GearModification> registry = event.getRegistry();
        registry.register(BLAZING_GEAR_MODIFICATION);
        registry.register(UNUSUAL_GEAR_MODIFICATION);
        registry.register(EMPOWERED_FUNDAMENTAL_GEAR_MODIFICATION);
    }
}
