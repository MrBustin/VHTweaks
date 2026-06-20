package xyz.iwolfking.woldsvaults.models;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.init.ModDynamicModels;

public class Tridents {
    public static final DynamicModelRegistry<HandHeldModel> REGISTRY = new DynamicModelRegistry();
    //scrappy
    public static final HandHeldModel SCRAPPY_1;
    public static final HandHeldModel SCRAPPY_2;
    public static final HandHeldModel SCRAPPY_3;
    public static final HandHeldModel SCRAPPY_4;

    //common
    public static final HandHeldModel CLASSIC;
    public static final HandHeldModel GUARDIAN;
    public static final HandHeldModel ORANGE;
    public static final HandHeldModel GOLDEN;

    //rare
    public static final HandHeldModel TRIDENT_SEABIRD;
    public static final HandHeldModel AXOLOTL;
    public static final HandHeldModel EMERALD;

    //epic
    public static final HandHeldModel PURPLE;
    public static final HandHeldModel ROYAL;
    public static final HandHeldModel PINK;
    public static final HandHeldModel FORK;

    //omega
    public static final HandHeldModel TRIDENT_PITCHFORK;
    public static final HandHeldModel TRIDENT_CRIMSON;
    public static final HandHeldModel CRYSTAL;
    public static final HandHeldModel PRISMARINE;
    public static final HandHeldModel PHANTOMGUARD;

    //unique
    public static final HandHeldModel CATCH;
    public static final HandHeldModel FJORD_BALLAD;
    //public static final HandHeldModel DRAGON_BANE;
    //public static final HandHeldModel LITHIC;
    //public static final HandHeldModel HOMA;
    public static final HandHeldModel VOID;
    public static final HandHeldModel WHISPER;
    public static final HandHeldModel MARLIN;
    //public static final HandHeldModel SWORDFISH;

    public static final HandHeldModel AQUATIC_RAGE;
    public static final HandHeldModel DEATH_KNIGHT;
    public static final HandHeldModel SCARLET_PIERCER;
    public static final HandHeldModel HOLY_SPEAR;
    public static final HandHeldModel MJOLNIR;
    public static final HandHeldModel DIVINEGUARD;
    public static final HandHeldModel PRISMATIC;


    public Tridents() {
    }

    static {
        //scrappy
        SCRAPPY_4 = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/scrappy_4"), "Scrappy Trident 4")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SCRAPPY_3 = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/scrappy_3"), "Scrappy Trident 3")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SCRAPPY_2 = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/scrappy_2"), "Scrappy Trident 2")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SCRAPPY_1 = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/scrappy_1"), "Scrappy Trident 1")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));

        //common
        CLASSIC = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/vanilla"), "Classic Vanilla Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        GUARDIAN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/guardian"), "Guardian Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        ORANGE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/orange"), "Orange Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        GOLDEN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/golden"), "Golden Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        TRIDENT_PITCHFORK = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/pitchfork"), "Pitchfork Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PRISMARINE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/prismarine"), "Prismarine Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        HOLY_SPEAR = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/holy_spear"), "Piercing Light")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));



        //rare
        TRIDENT_SEABIRD = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/seabird"), "Seabird Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        AXOLOTL = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/axolotl"), "Axolotl Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        EMERALD = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/emerald"), "Emerald Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        TRIDENT_CRIMSON = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/crimson"), "Crimson Lance Trident")).properties((new DynamicModelProperties()).allowTransmogrification().discoverOnRoll()));
        //DRAGON_BANE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/dragon_bane"), "Dragon's Bane")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        //LITHIC = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/lithic"), "Lithic Spear")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        //HOMA = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/homa"), "Homa's Wand")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));


        //epic
        PURPLE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/purple"), "Purple Trident")).properties((new DynamicModelProperties()).allowTransmogrification().discoverOnRoll()));
        ROYAL = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/royal"), "Royal Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PINK = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/pink"), "Pink Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        VOID = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/void"), "Void Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        FORK = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/fork"), "Fork")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));

        //omega
        CRYSTAL = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/crystal"), "Crystal Lance")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PHANTOMGUARD = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/phantomguard"), "Phantomguard Lance")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        AQUATIC_RAGE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/aquatic_rage"), "Tidepiercer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PRISMATIC = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/prismatic"), "Prismatic Lance")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        DIVINEGUARD = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/divineguard"), "Divine Lance")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));


        //unique
        MARLIN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/marlin"), "Marlin Trident")).properties(new DynamicModelProperties()));
        //SWORDFISH = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/swordfish"), "Swordfish Trident")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        MJOLNIR = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/mjolnir"), "Mjolnir")).properties(new DynamicModelProperties()));


        //ascension forge
        CATCH = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/catch"), "Glacial Lance")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        FJORD_BALLAD = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/fjord_ballad"), "Azure Lance")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        DEATH_KNIGHT = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/death_knight_spear"), "Death Knight's Lance")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SCARLET_PIERCER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/scarlet_piercer"), "Scarlet Piercer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));


        //unused
        WHISPER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/trident/whisper"), "Whisper Trident")).properties(new DynamicModelProperties()));

    }
}
