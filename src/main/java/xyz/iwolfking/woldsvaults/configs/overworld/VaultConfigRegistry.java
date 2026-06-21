package xyz.iwolfking.woldsvaults.configs.overworld;

import xyz.iwolfking.woldsvaults.configs.overworld.vault.OverworldVaultsGeneralConfig;
import xyz.iwolfking.woldsvaults.configs.overworld.vault.OverworldVaultsMobConfig;
import xyz.iwolfking.woldsvaults.configs.overworld.vault.OverworldVaultsPortalConfig;

public class VaultConfigRegistry {
    public static OverworldVaultsPortalConfig OVERWORLD_VAULTS_PORTAL_CONFIG;
    public static OverworldVaultsGeneralConfig OVERWORLD_VAULTS_GENERAL_CONFIG;
    public static OverworldVaultsMobConfig OVERWORLD_VAULTS_MOB_CONFIG;


    public static void registerCustomConfigs() {
        OVERWORLD_VAULTS_PORTAL_CONFIG = new OverworldVaultsPortalConfig().readConfig();
        OVERWORLD_VAULTS_GENERAL_CONFIG = new OverworldVaultsGeneralConfig().readConfig();
        OVERWORLD_VAULTS_MOB_CONFIG = new OverworldVaultsMobConfig().readConfig();
    }
}
