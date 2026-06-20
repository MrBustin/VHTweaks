package xyz.iwolfking.woldsvaults.init;


import net.minecraftforge.event.RegistryEvent;
import xyz.iwolfking.vhapi.api.registry.gear.CustomVaultGearRegistryEntry;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.gear.CustomVaultGearModelRollRaritiesConfig;
import xyz.iwolfking.woldsvaults.models.Battlestaffs;
import xyz.iwolfking.woldsvaults.models.LootSacks;
import xyz.iwolfking.woldsvaults.models.Plushies;
import xyz.iwolfking.woldsvaults.models.Tridents;
import xyz.iwolfking.woldsvaults.models.Rangs;


public class ModCustomVaultGearEntries {

    public static final CustomVaultGearRegistryEntry BATTLESTAFF = new CustomVaultGearRegistryEntry("battlestaff", "Battlestaff", ModItems.BATTLESTAFF, Battlestaffs.REGISTRY, CustomVaultGearModelRollRaritiesConfig.BATTLESTAFF_MODEL_ROLLS);
    public static final CustomVaultGearRegistryEntry TRIDENT = new CustomVaultGearRegistryEntry("trident", "Trident", ModItems.TRIDENT, Tridents.REGISTRY, CustomVaultGearModelRollRaritiesConfig.TRIDENT_MODEL_ROLLS);
    public static final CustomVaultGearRegistryEntry PLUSHIE = new CustomVaultGearRegistryEntry("plushie", "Plushie", ModItems.PLUSHIE, Plushies.REGISTRY, CustomVaultGearModelRollRaritiesConfig.PLUSHIE_MODEL_ROLLS);
    public static final CustomVaultGearRegistryEntry LOOT_SACK = new CustomVaultGearRegistryEntry("loot_sack", "Loot Sack", ModItems.LOOT_SACK, LootSacks.REGISTRY, CustomVaultGearModelRollRaritiesConfig.LOOT_SACKS_MODEL_ROLLS);
    public static final CustomVaultGearRegistryEntry RANG = new CustomVaultGearRegistryEntry("rang", "Vaultarang", ModItems.RANG, Rangs.REGISTRY, CustomVaultGearModelRollRaritiesConfig.RANG_MODEL_ROLLS);

    public static void registerGearEntries(RegistryEvent.Register<CustomVaultGearRegistryEntry> event) {
        event.getRegistry().register(TRIDENT);

        event.getRegistry().register(BATTLESTAFF);

        event.getRegistry().register(LOOT_SACK);

        event.getRegistry().register(PLUSHIE);

        event.getRegistry().register(RANG);

    }
}
