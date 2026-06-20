package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;

import java.util.ArrayList;
import java.util.List;

public class NameProviderConfig extends Config {
    @Expose
    public boolean IS_USED;

    @Expose
    public List<String> NAME_LIST = new ArrayList<>();

    @Override
    public String getName() {
        return "vaultadditions_name_provider";
    }

    @Override
    protected void reset() {
        IS_USED = false;
        NAME_LIST.add("KaptainWutax");
        NAME_LIST.add("HellFirePVP");
        NAME_LIST.add("HoY_82");
        NAME_LIST.add("a1qs");
        NAME_LIST.add("JustAHuman_xD");
        NAME_LIST.add("Jrowez");
        NAME_LIST.add("attackeight");
    }

    public List<String> getNameList() {
        return NAME_LIST;
    }

    public boolean isUsed() {
        return IS_USED;
    }

    public String getRandomName() {
        return NAME_LIST.get(rand.nextInt(NAME_LIST.size()));
    }
}
