package xyz.iwolfking.woldsvaults.models;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import org.stringtemplate.v4.ST;

public class Magestaffs {
    public static final DynamicModelRegistry<HandHeldModel> REGISTRY = new DynamicModelRegistry();
    //scrappy
    public static final HandHeldModel MAGESTAFF_0;
    public static final HandHeldModel MAGESTAFF_COPPER;

    //common
    public static final HandHeldModel MAGESTAFF_LIGHT_BLUE;
    public static final HandHeldModel MAGESTAFF_LAPIS;
    public static final HandHeldModel MAGESTAFF_REDSTONE;

    //rare
    public static final HandHeldModel MAGESTAFF_GOLDEN;
    public static final HandHeldModel MAGESTAFF_EMERALD;

    //epic
    public static final HandHeldModel MAGESTAFF_DIAMOND;
    public static final HandHeldModel MAGESTAFF_PURPLE;

    //omega
    public static final HandHeldModel MAGESTAFF_GROWING;
    public static final HandHeldModel MAGESTAFF_TERROR;
    public static final HandHeldModel MAGESTAFF_LIGHTSABER;
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


    public Magestaffs() {
    }

    static {
        //scrappy
        MAGESTAFF_0 = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_0"), "Scrappy Magestaff")).properties(STANDARD_PROPERTIES);
        MAGESTAFF_COPPER = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_copper"), "Copper Magestaff")).properties(STANDARD_PROPERTIES);

        //common
        MAGESTAFF_LIGHT_BLUE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_light_blue"), "Arcane Magestaff")).properties(STANDARD_PROPERTIES);
        MAGESTAFF_LAPIS = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_lapis"), "Lapis Magestaff")).properties(STANDARD_PROPERTIES);
        MAGESTAFF_REDSTONE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_redstone"), "Redstone Magestaff")).properties(STANDARD_PROPERTIES);

        //rare
        MAGESTAFF_GOLDEN = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_golden"), "Golden Magestaff")).properties(STANDARD_PROPERTIES);
        MAGESTAFF_EMERALD = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_emerald"), "Emerald Magestaff")).properties(STANDARD_PROPERTIES);
        MAGESTAFF_GROWING = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_growing"), "Growing Magestaff")).properties(STANDARD_PROPERTIES);
        //SAND_STAFF = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/sand_staff"), "Staff of Shurima")).properties(STANDARD_PROPERTIES);
        //VORTEX = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/vortex"), "Nomad's Staff")).properties(STANDARD_PROPERTIES);


        //epic
        MAGESTAFF_DIAMOND = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_diamond"), "Diamond Magestaff")).properties(STANDARD_PROPERTIES);
        MAGESTAFF_PURPLE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_purple"), "Purple Magestaff")).properties(STANDARD_PROPERTIES);
        FERRYMAN_PIPE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/ferryman"), "Ferryman's Pipe")).properties(STANDARD_PROPERTIES);
        //SKYWARD_SPINE = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/skyward_spine"), "Angel's Staff")).properties(STANDARD_PROPERTIES);
        MAGESTAFF_TERROR = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/magestaff_terror"), "Terror Magestaff")).properties(STANDARD_PROPERTIES);

        //omega
        MAGESTAFF_LIGHTSABER = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/lightsaber"), "Cosmonite's Battlesaber")).properties(STANDARD_PROPERTIES);
        QUACKHAMMER = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/quackhammer"), "Quackhammer")).properties(STANDARD_PROPERTIES);
        CHOPSTICK = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/chopstick_magestaff"), "Chopstick")).properties(STANDARD_PROPERTIES);
        MURAKUMOGIRI = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/murakumogiri"), "Murakumo-Giri Staff")).properties(STANDARD_PROPERTIES);
        SUN_WUKONG = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/sun_wukong_staff"), "Sun Wukong Staff")).properties(STANDARD_PROPERTIES);

        //unique
        BAMBOO = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/bamboo_fightstick"), "Bamboo FightStick")).properties(new DynamicModelProperties());
        NINJA_BATON = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/ninja_baton"), "Ninja Baton")).properties(new DynamicModelProperties());

        //ascension forge
        SCARLET_TWINS = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/scarlet_twins"), "Scarlet Twins")).properties(STANDARD_PROPERTIES);

        //unused
        //MIDNIGHT = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/midnight"), "Midnight Magestaff"));
        //PRISMATIC = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/magestaff/prismatic"), "Prismatic Magestaff"));

    }
}
