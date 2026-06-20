package xyz.iwolfking.woldsvaults.models;

import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.model.item.PlainItemModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;

public class Rangs {
    public static final DynamicModelRegistry<PlainItemModel> REGISTRY = new DynamicModelRegistry<>();
    //scrappy
    public static final PlainItemModel STONE;
    public static final PlainItemModel WOODEN_RANG;

    //common
    public static final PlainItemModel DIAMOND;
    public static final PlainItemModel CRIMSON;
    public static final PlainItemModel CHROMATIC_IRON;
    public static final PlainItemModel COPPER;
    public static final PlainItemModel JACK_OF_CLUBS;

    //rare
    public static final PlainItemModel GOLD;
    public static final PlainItemModel CHROMATIC_STEEL;
    public static final PlainItemModel KING_OF_DIAMONDS;

    //epic
    public static final PlainItemModel AMETHYST;
    public static final PlainItemModel CHROMATIC_GOLD;
    public static final PlainItemModel QUEEN_OF_HEARTS;

    //omega
    public static final PlainItemModel EMERALD;
    public static final PlainItemModel GETA;
    public static final PlainItemModel OURORANGOS;
    public static final PlainItemModel BLACK_CHROMATIC;
    public static final PlainItemModel MIDNIGHT;
    public static final PlainItemModel PRISMATIC;
    public static final PlainItemModel ACE_OF_SPADES;

    //unique
    public static final PlainItemModel TRIRANG;
    public static final PlainItemModel CEILING_FAN;
    public static final PlainItemModel BANANARANG;
    public static final PlainItemModel RED_JOKER;
    public static final PlainItemModel BLACK_JOKER;

    public Rangs() {
    }

    static {
        //scrappy
        STONE = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/stone"), "Stone Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        WOODEN_RANG = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/wooden"), "Wooden Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());

        //common
        DIAMOND = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/diamond"), "Diamond Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        CRIMSON = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/crimson"), "Crimson Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        CHROMATIC_IRON = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/chroma"), "Chromatic Iron Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        COPPER = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/copper"), "Copper Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        JACK_OF_CLUBS = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/jack_of_clubs"), "Jack of Clubs")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());

        //rare
        GOLD = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/gold"), "Golden Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        CHROMATIC_GOLD = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/gold_chroma"), "Chromatic Gold Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        KING_OF_DIAMONDS = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/king_of_diamonds"), "King of Diamonds")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());

        //epic
        AMETHYST = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/amethyst"), "Amethyst Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        CHROMATIC_STEEL = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/steel_chroma"), "Chromatic Steel Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        QUEEN_OF_HEARTS = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/queen_of_hearts"), "Queen of Hearts")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());

        //omega
        EMERALD = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/emerald"), "Emerald Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        GETA = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/geta"), "Geta Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        OURORANGOS = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/ourorangos"), "Ourorangos")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        BLACK_CHROMATIC = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/black_chroma"), "Black Chromatic Steel Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        ACE_OF_SPADES = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/ace_of_spades"), "Ace of Spades")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());

        //unique
        TRIRANG = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/trirang"), "Tri-Rang")).properties(new DynamicModelProperties());
        MIDNIGHT = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/midnight"), "Midnight Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        PRISMATIC = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/prismatic"), "Prismatic Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        CEILING_FAN = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/ceiling_fan"), "Ceiling Fan")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        BANANARANG = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/bananarang"), "Banana Rang")).properties(new DynamicModelProperties().allowTransmogrification().discoverOnRoll());
        RED_JOKER = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/red_joker"), "Joker")).properties(new DynamicModelProperties().allowTransmogrification());
        BLACK_JOKER = REGISTRY.register(new PlainItemModel(VaultMod.id("gear/rang/black_joker"), "Joker")).properties(new DynamicModelProperties().allowTransmogrification());
    }
}
