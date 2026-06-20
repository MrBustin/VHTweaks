package xyz.iwolfking.woldsvaults.configs;

import iskallia.vault.config.ScavengerConfig;
import xyz.iwolfking.vhapi.api.lib.core.readers.CustomVaultConfigReader;
import xyz.iwolfking.vhapi.api.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;

public class UnhingedScavengerConfig extends ScavengerConfig {

    public static UnhingedScavengerConfig INSTANCE = null;

    public UnhingedScavengerConfig() {

    }

    @Override
    public String getName() {
        return "unhinged_scavenger";
    }

    @Override
    protected void reset() {
        super.reset();
    }


}
