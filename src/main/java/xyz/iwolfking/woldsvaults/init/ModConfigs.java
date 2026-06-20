package xyz.iwolfking.woldsvaults.init;

import xyz.iwolfking.vhapi.api.lib.core.readers.CustomVaultConfigReader;
import xyz.iwolfking.vhapi.api.util.JsonUtils;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.*;

import java.io.IOException;
import java.io.InputStream;

public class ModConfigs {
    public static GemBoxConfig GEM_BOX;
    public static SupplyBoxConfig SUPPLY_BOX;
    public static AugmentBoxConfig AUGMENT_BOX;
    public static InscriptionBoxConfig INSCRIPTION_BOX;
    public static OmegaBoxConfig OMEGA_BOX;
    public static CatalystBoxConfig CATALYST_BOX;
    public static EnigmaEggConfig ENIGMA_EGG;
    public static VaultarBoxConfig VAULTAR_BOX;
    public static UnhingedScavengerConfig UNHINGED_SCAVENGER;
    public static BallisticBingoConfig BALLISTIC_BINGO_CONFIG;

    public static HauntedBraziersConfig HAUNTED_BRAZIERS;
    public static EnchantedElixirConfig ENCHANTED_ELIXIR;
    public static AlchemyObjectiveConfig ALCHEMY_OBJECTIVE;

    public static void register() {
//        try (InputStream stream = WoldsVaults.class.getResourceAsStream("/default_configs/unhinged_scavenger.json")) {
//            CustomVaultConfigReader<UnhingedScavengerConfig> reader = new CustomVaultConfigReader<>();
//            if(stream == null) {
//                throw new IOException();
//            }
//            UNHINGED_SCAVENGER = reader.readCustomConfig("unhinged_scavenger", JsonUtils.parseJsonContentFromStream(stream), UnhingedScavengerConfig.class);
//        } catch (IOException e) {
//            System.out.println("Failed to read default Unhinged Scavenger config...");
//            throw new RuntimeException(e);
//        }
        GEM_BOX = (GemBoxConfig) (new GemBoxConfig()).readConfig();
        SUPPLY_BOX = (SupplyBoxConfig) (new SupplyBoxConfig()).readConfig();
        AUGMENT_BOX = (AugmentBoxConfig) (new AugmentBoxConfig()).readConfig();
        INSCRIPTION_BOX = (InscriptionBoxConfig) (new InscriptionBoxConfig()).readConfig();
        OMEGA_BOX = (OmegaBoxConfig) (new OmegaBoxConfig()).readConfig();
        CATALYST_BOX = (CatalystBoxConfig) (new CatalystBoxConfig()).readConfig();
        ENIGMA_EGG = (EnigmaEggConfig) (new EnigmaEggConfig()).readConfig();
        VAULTAR_BOX = (VaultarBoxConfig) (new VaultarBoxConfig()).readConfig();
        BALLISTIC_BINGO_CONFIG = (BallisticBingoConfig) (new BallisticBingoConfig().readConfig());
        HAUNTED_BRAZIERS = (HauntedBraziersConfig) (new HauntedBraziersConfig().readConfig());
        ENCHANTED_ELIXIR = (EnchantedElixirConfig) (new EnchantedElixirConfig().readConfig());
        ALCHEMY_OBJECTIVE = (AlchemyObjectiveConfig) (new AlchemyObjectiveConfig().readConfig());
        UNHINGED_SCAVENGER = (UnhingedScavengerConfig) (new UnhingedScavengerConfig().readConfig());
    }
}
