package io.github.a1qs.vaultadditions.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Integer> POWER_CRYSTAL_INCREASE;
    public static final ForgeConfigSpec.ConfigValue<Integer> NETHER_BORDER_INCREASE;
    public static final ForgeConfigSpec.ConfigValue<Integer> END_BORDER_INCREASE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LIMIT_TIME_FOR_EXPANSION;
    public static final ForgeConfigSpec.ConfigValue<String> STOP_ACCEPTING_GEMSTONES_DATE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GROW_PLAYER_ON_GEMSTONE_SUBMIT;
    public static final ForgeConfigSpec.ConfigValue<Double> GROW_PLAYER_AMOUNT;
    public static final ForgeConfigSpec.ConfigValue<Double> GROW_PLAYER_CAP;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SHOW_POWER_MENU;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LIMIT_WITHER_SPAWN_GLOBAL_SOUND;


    static {
        BUILDER.comment("ServerConfigs for VaultAdditions");

        BUILDER.push("General Settings"); // General Settings Start

        POWER_CRYSTAL_INCREASE = BUILDER.comment("Modify the amount of blocks a Power Crystal increases with its usage")
                .define("POWER_CRYSTAL_INCREASE", 5);

        NETHER_BORDER_INCREASE = BUILDER.comment("The multiplier when increasing the border inside the Nether")
                .define("NETHER_BORDER_INCREASE", 8);

        END_BORDER_INCREASE = BUILDER.comment("The multiplier when increasing the border inside the Nether")
                .define("END_BORDER_INCREASE", 4);

        LIMIT_TIME_FOR_EXPANSION = BUILDER.comment("Whether there should be a time limit on when to prevent Border Expansion")
                .define("LIMIT_TIME_FOR_EXPANSION", false);

        STOP_ACCEPTING_GEMSTONES_DATE = BUILDER.comment("The date when Gemstones will no longer be accepted by the Globe Expander, based off of Local server time. (TT/MM/YYYY, HH:MM:SS)")
                .define("STOP_ACCEPTING_GEMSTONES_DATE", "01/01/2030 20:00:00");

        GROW_PLAYER_ON_GEMSTONE_SUBMIT = BUILDER.comment("Whether players should grow in size whenever Gemstones are submit (Does not grow player hitbox)")
                .define("GROW_PLAYER_ON_GEMSTONE_SUBMIT", true);

        GROW_PLAYER_AMOUNT = BUILDER.comment("The amount to grow the player by whenever they submit a gemstone, per gemstone")
                .define("GROW_PLAYER_AMOUNT", 0.001);

        GROW_PLAYER_CAP = BUILDER.comment("The max amount a player can grow in total")
                .define("GROW_PLAYER_CAP", 0.5);

        SHOW_POWER_MENU = BUILDER.comment("Whether the Power menu should be shown to the player")
                .define("SHOW_POWER_MENU", true);

        LIMIT_WITHER_SPAWN_GLOBAL_SOUND = BUILDER.comment("When enabled, replaces the global Wither spawn sound with a ranged sound heard within 64 blocks")
                .define("LIMIT_WITHER_SPAWN_GLOBAL_SOUND", false);

        BUILDER.pop(); // General Settings End

        SPEC = BUILDER.build();
    }
}
