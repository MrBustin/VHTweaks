package xyz.iwolfking.woldsvaults.core;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.PathResourcePack;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.core.WoldsVaultsConfig;

import java.nio.file.Path;

public class AddonLoader {


    public static void onAddPackFinders(final AddPackFindersEvent event) {
        if(event.getPackType() == PackType.SERVER_DATA) {
            if(WoldsVaultsConfig.COMMON.enableGemDropsOverhaul.get()) {
                registerAddon(event, "wolds_vault_gem_drops_overhaul");
            }

            if(WoldsVaultsConfig.COMMON.enableGemReagents.get()) {
                registerAddon(event, "wolds_gem_reagents");
            }
        }
    }

    private static void registerAddon(final AddPackFindersEvent event, final String packName) {
        event.addRepositorySource((packConsumer, constructor) -> {
            Pack pack = Pack.create(WoldsVaults.MODID + ":" + packName, true, () -> {
                Path path = ModList.get().getModFileById(WoldsVaults.MODID).getFile().findResource("/" + packName);
                return new PathResourcePack(packName, path);
            }, constructor, Pack.Position.TOP, PackSource.DEFAULT);

            if (pack != null) {
                packConsumer.accept(pack);
            }
        });
    }
}
