package xyz.iwolfking.woldsvaults.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.WoldsVaults;

public class CakeWorldData extends SavedData {
    private static final String DATA_NAME = "woldsvaults_cake_data";
    private boolean fruitCake = false;
    private String cakeType = "";

    public CakeWorldData() {
        WoldsVaults.LOGGER.debug("Creating new cake world data");
    }

    public void setFruitCake(boolean fruitCake) {
        this.fruitCake = fruitCake;
        this.setDirty();
    }

    public void setCakeType(String cakeType) {
        this.cakeType = cakeType;
        this.setDirty();
    }

    public boolean isFruitCake() {
        return this.fruitCake;
    }

    public String getCakeType() {
        return this.cakeType;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        WoldsVaults.LOGGER.debug("Saving cake world data");
        compoundTag.putBoolean("fruit_cake", this.fruitCake);
        compoundTag.putString("cake_type", this.cakeType);
        return compoundTag;
    }

    public static CakeWorldData load(CompoundTag tag) {
        WoldsVaults.LOGGER.debug("Loading cake world data");
        CakeWorldData data = new CakeWorldData();
        data.fruitCake = tag.getBoolean("fruit_cake");
        data.cakeType = tag.getString("cake_type");
        return data;
    }

    public static CakeWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(CakeWorldData::load, CakeWorldData::new, DATA_NAME);
    }

    public static boolean isFruitCake(ServerLevel level) {
        return level != null && get(level).isFruitCake();
    }
}
