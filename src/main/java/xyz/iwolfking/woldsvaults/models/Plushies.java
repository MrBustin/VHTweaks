package xyz.iwolfking.woldsvaults.models;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;

public class Plushies {
    public static final DynamicModelRegistry<HandHeldModel> REGISTRY = new DynamicModelRegistry();
    //scrappy
    public static final HandHeldModel CAT;
    public static final HandHeldModel COW;
    public static final HandHeldModel BEE;
    public static final HandHeldModel FROG;

    public static final HandHeldModel PHAN;
    public static final HandHeldModel WANDA;
    public static final HandHeldModel SQUIDDLY;
    public static final HandHeldModel SQUIDDY;
    public static final HandHeldModel VINCENT;
    public static final HandHeldModel MOOEY;
    public static final HandHeldModel BABOU;

    //common
    public static final HandHeldModel AXOLOTL;
    public static final HandHeldModel DOLPHIN;
    public static final HandHeldModel PANDA;
    public static final HandHeldModel LLAMA;

    public static final HandHeldModel WALTER_THEO;
    public static final HandHeldModel SHELLY;
    public static final HandHeldModel MOLTEN;
    public static final HandHeldModel SKITTER;
    public static final HandHeldModel GURGLES;
    public static final HandHeldModel EEPY;
    public static final HandHeldModel SNIFFY;
    public static final HandHeldModel CLYDE;
    public static final HandHeldModel ARMANI;

    //rare
    public static final HandHeldModel DWELLER;
    public static final HandHeldModel PARROT;
    public static final HandHeldModel SLIME;
    public static final HandHeldModel TURTLE;
    public static final HandHeldModel ARTIC_FOX;
    public static final HandHeldModel BLAHAJ;

    public static final HandHeldModel KEVIN_BACON;
    public static final HandHeldModel ARIA;
    public static final HandHeldModel VIXIE;
    public static final HandHeldModel BREEZY;
    public static final HandHeldModel EVO;
    public static final HandHeldModel GLIMMER;
    public static final HandHeldModel ECHO;

    //epic
    public static final HandHeldModel ARAEVIN;
    public static final HandHeldModel WARDEN;
    public static final HandHeldModel HRRY;
    public static final HandHeldModel CAIGAN;
    public static final HandHeldModel JACKAL;
    public static final HandHeldModel DRAGON;
    public static final HandHeldModel SPARKLEZ;

    public static final HandHeldModel GANDER;
    public static final HandHeldModel WENDY_SCOTT;
    public static final HandHeldModel IDRIS;
    public static final HandHeldModel ELDRICH;
    public static final HandHeldModel RUMBLE;
    public static final HandHeldModel INGOT;
    public static final HandHeldModel WINNIE;

    //omega
    public static final HandHeldModel SQUISH;
    public static final HandHeldModel JOSEPH;
    public static final HandHeldModel IWK;
    public static final HandHeldModel JROWEZ;
    public static final HandHeldModel ATTACK8;
    public static final HandHeldModel JUSTA;
    public static final HandHeldModel DOGV2;
    public static final HandHeldModel KATABLES;
    public static final HandHeldModel BUSTIN;
    public static final HandHeldModel CAROLINE;
    public static final HandHeldModel CYBER;

    //unique
    public static final HandHeldModel BOGSTER;  //unique
    public static final HandHeldModel BURNIE;
    public static final HandHeldModel BATTY;
    public static final HandHeldModel PENNY_RAE;
    public static final HandHeldModel SAFER_SPACES;


    public Plushies() {
    }

    static {
        //scrappy
        ARTIC_FOX = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/artic_fox"), "Snowflake the Artic Fox")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        AXOLOTL = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/axolotl"), "Aquee the Axolotl")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        BABOU = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/babou"), "Babou the Ocelot")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        BEE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/bee"), "Billy the Bee")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        CAT = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/cat"), "Carl the Cat")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        COW = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/cow"), "Caroline the Cow")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        DOLPHIN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/dolphin"), "Daphne the Dolphin")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        FROG = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/frog"), "Fred the Frog")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        LLAMA = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/llama"), "Larry the Llama")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        MOOEY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/mooey"), "Mooey the Mooshroom")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PANDA = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/panda"), "Petey the Panda")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PHAN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/phan"), "Phan the Phantom")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SQUIDDY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/squiddy"), "Squiddy the Glow Squid")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SQUIDDLY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/squiddly"), "Squiddly the Squid")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));



        //common
        KEVIN_BACON = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/kevin_bacon"), "Kevin Bacon the Killer Bunny")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        VINCENT = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/vincent"), "Vincent the Villager")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        WANDA = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/wanda"), "Wanda the Witch")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        WALTER_THEO = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/walter_theo"), "Walter Theo the Wandering Trader")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        MOLTEN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/molten"), "Molten the Magma Cube")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SHELLY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/shelly"), "Shelly the Shulker")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SKITTER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/skitter"), "Skitter the Silverfish")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        GURGLES = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/gurgles"), "Gurgles the Guardian")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        EEPY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/eepy"), "Eepy the Endermite")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SNIFFY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/sniffy"), "Snort the Sniffer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        CLYDE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/clyde"), "Clyde the Camel")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        ARMANI = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/armani"), "Armani the Armadillo")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SLIME = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/slime"), "Stevey the Slime")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        TURTLE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/turtle"), "Tim the Turtle")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));


        //rare
        DWELLER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/dweller"), "Dave the Dweller")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PARROT = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/parrot"), "Petroclus the Parrot")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        BLAHAJ = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/blahaj"), "Blahaj the Shark")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        ARIA = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/aria"), "Aria the Allay")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        VIXIE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/vixie"), "Vixie the Vex")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        BREEZY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/breezy"), "Breezy the Breeze")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        EVO = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/evo"), "Evo the Evoker")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        GLIMMER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/glimmer"), "Glimmer the Ghast")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        ECHO = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/echo"), "Echo the Enderman")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        ARAEVIN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/araevin"), "Araevin the Heathen")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        CAIGAN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/caigan"), "Caigan the Mouse")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SQUISH = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/dearliest"), "Dearliest the Squishiest")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        JACKAL = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/jackal"), "Jackal the Legend")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));

        //epic
        DRAGON = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/dragon"), "Drew the Dragon")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        HRRY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/hrry"), "CsaBa")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SPARKLEZ = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/sparklez"), "Chaaaland")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        WARDEN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/warden"), "Wally the Warden")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        GANDER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/gander"), "Gander the Goat")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        IDRIS = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/idris"), "Idris the Illusionist")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        ELDRICH = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/eldrich"), "Eldrich Guy the Elder Guardian")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        RUMBLE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/rumble"), "Rumble the Ravager")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        INGOT = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/ingot"), "Ingot the Iron Golem")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        WINNIE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/winnie"), "Winnie the Wither")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        JOSEPH = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/joseph"), "Joseph the Filterer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));


        //omega
        IWK = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/wolf"), "Wolf the Creator")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        JROWEZ = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/jrowez"), "Jawn the Tinkerer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        ATTACK8 = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/attack8"), "Attack8 the Developer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        JUSTA = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/justa"), "JustAHuman the Developer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        DOGV2 = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/dogv2"), "Dog the Developer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        KATABLES = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/katables"), "Katables the Builder")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        BUSTIN = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/bustin"), "Bustin the Developer")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        CAROLINE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/caroline"), "Caroline the Ticketmaster")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        CYBER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/cyber"), "Cyberwolf the Artist")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));

        //unique
        BATTY = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/batty"), "Batty the Bat")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        PENNY_RAE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/penny_rae"), "Penny Rae the Red Panda")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        SAFER_SPACES = REGISTRY.register(new HandHeldModel(VaultMod.id("gear/plushie/safer_spaces"), "Terry the Tardigrade")).properties(new DynamicModelProperties());

        //ascension forge
        BURNIE = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/burnie"), "Burnie the Blaze")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        WENDY_SCOTT = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/wendy_scott"), "Wendy Scott the Wither Skeleton")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));
        BOGSTER = (HandHeldModel)REGISTRY.register((HandHeldModel)(new HandHeldModel(VaultMod.id("gear/plushie/bogster"), "Bogster the Bogged")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll()));

        //unused



    }
}
