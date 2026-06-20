package io.github.a1qs.vaultadditions.config;

import io.github.a1qs.vaultadditions.config.vault.*;

public class Configs {
    public static PowerGUIConfig POWERS_GUI;
    public static PowerConfig POWERS;
    public static VaultStatueLootConfig STATUE_LOOT_VAULT;
    public static GiftStatueLootConfig STATUE_LOOT_GIFT;
    public static MegaGiftStatueLootConfig STATUE_LOOT_MEGA_GIFT;
    public static ArenaGiftStatueLootConfig STATUE_LOOT_ARENA;
    public static OmegaStatueLootConfig STATUE_LOOT_OMEGA;
    public static EventConfig EVENT_CONFIG;
    public static NameProviderConfig NAME_PROVIDER_CONFIG;
    public static ExtraVaultChestMeta EXTRA_VAULT_CHEST_META;
    public static ExtraVaultTimeContributionsConfig EXTRA_VAULT_TIME_CONTRIBUTIONS;
    public static TransmogUnlocksConfig TRANSMOG_UNLOCKS;
    public static TransmogRoleUnlocksConfig TRANSMOG_ROLE_UNLOCKS;
    public static RaidPlaqueConfig RAID_PLAQUE_CONFIG;
    public static TransmogEffectsConfig TRANSMOG_EFFECTS_CONFIG;

    public static void registerCustomConfigs() {
        POWERS_GUI = new PowerGUIConfig().readConfig();
        POWERS = new PowerConfig().readConfig();
        STATUE_LOOT_VAULT = new VaultStatueLootConfig().readConfig();
        STATUE_LOOT_GIFT = new GiftStatueLootConfig().readConfig();
        STATUE_LOOT_MEGA_GIFT = new MegaGiftStatueLootConfig().readConfig();
        STATUE_LOOT_ARENA = new ArenaGiftStatueLootConfig().readConfig();
        STATUE_LOOT_OMEGA = new OmegaStatueLootConfig().readConfig();
        NAME_PROVIDER_CONFIG = new NameProviderConfig().readConfig();
        EVENT_CONFIG = new EventConfig().readConfig();
        EXTRA_VAULT_CHEST_META = new ExtraVaultChestMeta().readConfig();
        EXTRA_VAULT_TIME_CONTRIBUTIONS = new ExtraVaultTimeContributionsConfig().readConfig();
        TRANSMOG_UNLOCKS = new TransmogUnlocksConfig().readConfig();
        TRANSMOG_ROLE_UNLOCKS = new TransmogRoleUnlocksConfig().readConfig();
        RAID_PLAQUE_CONFIG = new RaidPlaqueConfig().readConfig();
        TRANSMOG_EFFECTS_CONFIG = new TransmogEffectsConfig().readConfig();
    }
}
