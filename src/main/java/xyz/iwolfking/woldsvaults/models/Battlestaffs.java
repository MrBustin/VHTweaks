package xyz.iwolfking.woldsvaults.models;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import org.stringtemplate.v4.ST;

public class Battlestaffs {
    public static final DynamicModelRegistry<HandHeldModel> REGISTRY = new DynamicModelRegistry();
    //scrappy
    public static final HandHeldModel BATTLESTAFF_0;
    public static final HandHeldModel BATTLESTAFF_COPPER;

    //common
    public static final HandHeldModel BATTLESTAFF_LIGHT_BLUE;
    public static final HandHeldModel BATTLESTAFF_LAPIS;
    public static final HandHeldModel BATTLESTAFF_REDSTONE;

    //rare
    public static final HandHeldModel BATTLESTAFF_GOLDEN;
    public static final HandHeldModel BATTLESTAFF_EMERALD;

    //epic
    public static final HandHeldModel BATTLESTAFF_DIAMOND;
    public static final HandHeldModel BATTLESTAFF_PURPLE;

    //omega
    public static final HandHeldModel BATTLESTAFF_GROWING;
    public static final HandHeldModel BATTLESTAFF_TERROR;
    public static final HandHeldModel BATTLESTAFF_LIGHTSABER;
    public static final HandHeldModel QUACKHAMMER;

    //unique
    public static final HandHeldModel FERRYMAN_PIPE;
    //public static final HandHeldModel VORTEX;
    //public static final HandHeldModel SKYWARD_SPINE;
    //public static final HandHeldModel SAND_STAFF;

    public static final HandHeldModel BAMBOO;
    public static final HandHeldModel CHOPSTICK;
    public static final HandHeldModel MURAKUMOGIRI;
    public static final HandHeldModel NINJA_BATON;
    public static final HandHeldModel SUN_WUKONG;

    public static final HandHeldModel SCARLET_TWINS;
    //public static final HandHeldModel MIDNIGHT;
    //public static final HandHeldModel PRISMATIC;

    public static DynamicModelProperties STANDARD_PROPERTIES = new DynamicModelProperties().allowTransmogrification().discoverOnRoll();


    public Battlestaffs() {
    }

    static {
        //scrappy
        BATTLESTAFF_0 = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_0"), "Scrappy Battlestaff")).properties(STANDARD_PROPERTIES);
        BATTLESTAFF_COPPER = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_copper"), "Copper Battlestaff")).properties(STANDARD_PROPERTIES);

        //common
        BATTLESTAFF_LIGHT_BLUE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_light_blue"), "Arcane Battlestaff")).properties(STANDARD_PROPERTIES);
        BATTLESTAFF_LAPIS = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_lapis"), "Lapis Battlestaff")).properties(STANDARD_PROPERTIES);
        BATTLESTAFF_REDSTONE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_redstone"), "Redstone Battlestaff")).properties(STANDARD_PROPERTIES);

        //rare
        BATTLESTAFF_GOLDEN = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_golden"), "Golden Battlestaff")).properties(STANDARD_PROPERTIES);
        BATTLESTAFF_EMERALD = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_emerald"), "Emerald Battlestaff")).properties(STANDARD_PROPERTIES);
        BATTLESTAFF_GROWING = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_growing"), "Growing Battlestaff")).properties(STANDARD_PROPERTIES);
        //SAND_STAFF = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/sand_staff"), "Staff of Shurima")).properties(STANDARD_PROPERTIES);
        //VORTEX = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/vortex"), "Nomad's Staff")).properties(STANDARD_PROPERTIES);


        //epic
        BATTLESTAFF_DIAMOND = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_diamond"), "Diamond Battlestaff")).properties(STANDARD_PROPERTIES);
        BATTLESTAFF_PURPLE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_purple"), "Purple Battlestaff")).properties(STANDARD_PROPERTIES);
        FERRYMAN_PIPE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/ferryman"), "Ferryman's Pipe")).properties(STANDARD_PROPERTIES);
        //SKYWARD_SPINE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/skyward_spine"), "Angel's Staff")).properties(STANDARD_PROPERTIES);
        BATTLESTAFF_TERROR = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/battlestaff_terror"), "Terror Battlestaff")).properties(STANDARD_PROPERTIES);

        //omega
        BATTLESTAFF_LIGHTSABER = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/lightsaber"), "Cosmonite's Battlesaber")).properties(STANDARD_PROPERTIES);
        QUACKHAMMER = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/quackhammer"), "Quackhammer")).properties(STANDARD_PROPERTIES);
        CHOPSTICK = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/chopstick_battlestaff"), "Chopstick")).properties(STANDARD_PROPERTIES);
        MURAKUMOGIRI = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/murakumogiri"), "Murakumo-Giri Staff")).properties(STANDARD_PROPERTIES);
        SUN_WUKONG = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/sun_wukong_staff"), "Sun Wukong Staff")).properties(STANDARD_PROPERTIES);

        //unique
        BAMBOO = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/bamboo_fightstick"), "Bamboo FightStick")).properties(new DynamicModelProperties());
        NINJA_BATON = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/ninja_baton"), "Ninja Baton")).properties(new DynamicModelProperties());

        //ascension forge
        SCARLET_TWINS = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/scarlet_twins"), "Scarlet Twins")).properties(STANDARD_PROPERTIES);

        //unused
        //MIDNIGHT = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/midnight"), "Midnight Battlestaff"));
        //PRISMATIC = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/battlestaff/prismatic"), "Prismatic Battlestaff"));

    }
}
