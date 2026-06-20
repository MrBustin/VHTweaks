package xyz.iwolfking.woldsvaults.configs.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import xyz.iwolfking.vhapi.config.VHAPIConfig;

import java.util.ArrayList;
import java.util.List;

public class WoldsVaultsConfig {

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Boolean> enableVaultGearRecipes;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableGemReagents;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableGemDropsOverhaul;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Features");
            this.enableVaultGearRecipes = builder.comment("Whether recipes for the Vault Gear Items should be enabled or not.").define("enableVaultGearRecipes", true);
            this.enableGemReagents = builder.comment("Whether recipes for the Vault Gem Reagents should be enabled or not.").define("enableGemReagents", true);
            this.enableGemDropsOverhaul = builder.comment("Whether vault ores should drop smashed vault gems instead of nothing.").define("enableGemDropsOverhaul", true);
            builder.pop();
        }

    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
