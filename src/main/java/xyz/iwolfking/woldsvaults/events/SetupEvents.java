package xyz.iwolfking.woldsvaults.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.vhapi.VHAPI;
import xyz.iwolfking.vhapi.api.data.api.CustomGearModelRolls;
import xyz.iwolfking.vhapi.api.events.VaultConfigEvent;
import xyz.iwolfking.vhapi.api.util.VHAPIProcesserUtils;
import xyz.iwolfking.vhapi.api.util.vhapi.VHAPILoggerUtils;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.core.WoldsVaultsConfig;
import xyz.iwolfking.woldsvaults.configs.gear.CustomVaultGearModelRollRaritiesConfig;
import xyz.iwolfking.woldsvaults.init.ModEntities;
import xyz.iwolfking.woldsvaults.init.ModItems;

import java.io.IOException;
import java.io.InputStream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = "woldsvaults")
public class SetupEvents {


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ModEntities.registerAttributes(event);
    }

    public static void registerCustomModelRolls(VaultConfigEvent.End event) {
        VHAPILoggerUtils.info(CustomGearModelRolls.CUSTOM_MODEL_ROLLS_MAP.toString());
        if(!CustomGearModelRolls.CUSTOM_MODEL_ROLLS_MAP.isEmpty()) {
            CustomVaultGearModelRollRaritiesConfig.BATTLESTAFF_MODEL_ROLLS = CustomGearModelRolls.CUSTOM_MODEL_ROLLS_MAP.get(ModItems.BATTLESTAFF.getRegistryName());
            CustomVaultGearModelRollRaritiesConfig.TRIDENT_MODEL_ROLLS = CustomGearModelRolls.CUSTOM_MODEL_ROLLS_MAP.get(ModItems.TRIDENT.getRegistryName());
            CustomVaultGearModelRollRaritiesConfig.LOOT_SACKS_MODEL_ROLLS = CustomGearModelRolls.CUSTOM_MODEL_ROLLS_MAP.get(ModItems.LOOT_SACK.getRegistryName());
            CustomVaultGearModelRollRaritiesConfig.PLUSHIE_MODEL_ROLLS = CustomGearModelRolls.CUSTOM_MODEL_ROLLS_MAP.get(ModItems.PLUSHIE.getRegistryName());
            CustomVaultGearModelRollRaritiesConfig.RANG_MODEL_ROLLS = CustomGearModelRolls.CUSTOM_MODEL_ROLLS_MAP.get(ModItems.RANG.getRegistryName());
        }
    }

    public static void addManualConfigs() {
        VHAPILoggerUtils.info("Registering manual configs!");
        registerManualConfigFile("/vhapi_configs/custom_spawners.json", WoldsVaults.id("custom_entity_spawners/wold_custom_spawners"));
        registerManualConfigFile("/vhapi_configs/objective_seals.json", WoldsVaults.id("vault/crystal/wold_objective_seals"));
        registerManualConfigFile("/vhapi_configs/wold_expertises.json", WoldsVaults.id("expertise/expertises/wold_expertises"));
        registerManualConfigFile("/vhapi_configs/wold_expertises_gui.json", WoldsVaults.id("expertise/expertise_gui/wold_expertise_gui"));
        registerManualConfigFile("/vhapi_configs/wold_modifier_pools.json", WoldsVaults.id("vault/modifier_pools/wold_modifier_pools"));
        registerManualConfigFile("/vhapi_configs/wold_modifiers.json", WoldsVaults.id("vault/modifiers/wold_modifiers"));
        registerManualConfigFile("/vhapi_configs/custom_objective_loot_tables.json", WoldsVaults.id("legacy_loot_tables/wold_objective_tables"));
        registerManualConfigFile("/vhapi_configs/custom_objective_stats.json", WoldsVaults.id("vault/stats/wold_objective_stats"));
        registerManualConfigFile("/vhapi_configs/expertise_descriptions.json", WoldsVaults.id("skill/descriptions/wold_skill_descriptions"));
        registerManualConfigFile("/vhapi_configs/wold_mobs.json", WoldsVaults.id("vault/mobs/wold_mobs"));

        //Gear Model Rolls
        registerManualConfigFile("/vhapi_configs/custom_model_rolls/battlestaff.json", VHAPI.of("gear/custom/model_rolls/battlestaff"));
        registerManualConfigFile("/vhapi_configs/custom_model_rolls/loot_sack.json", VHAPI.of("gear/custom/model_rolls/loot_sack"));
        registerManualConfigFile("/vhapi_configs/custom_model_rolls/plushie.json", VHAPI.of("gear/custom/model_rolls/plushie"));
        registerManualConfigFile("/vhapi_configs/custom_model_rolls/trident.json", VHAPI.of("gear/custom/model_rolls/trident"));
        registerManualConfigFile("/vhapi_configs/custom_model_rolls/rang.json", VHAPI.of("gear/custom/model_rolls/rang"));

        //Gear Modifiers
        registerManualConfigFile("/vhapi_configs/gear_modifiers/overwrite/battlestaff.json", VHAPI.of("gear/gear_modifiers/overwrite/battlestaff"));
        registerManualConfigFile("/vhapi_configs/gear_modifiers/overwrite/loot_sack.json", VHAPI.of("gear/gear_modifiers/overwrite/loot_sack"));
        registerManualConfigFile("/vhapi_configs/gear_modifiers/overwrite/plushie.json", VHAPI.of("gear/gear_modifiers/overwrite/plushie"));
        registerManualConfigFile("/vhapi_configs/gear_modifiers/overwrite/trident.json", VHAPI.of("gear/gear_modifiers/overwrite/trident"));
        registerManualConfigFile("/vhapi_configs/gear_modifiers/overwrite/rang.json", VHAPI.of("gear/gear_modifiers/overwrite/rang"));

        //Gear Workbench
//        registerManualConfigFile("/vhapi_configs/gear_workbench/battlestaff.json", VHAPI.of("gear/gear_workbench/battlestaff"));
//        registerManualConfigFile("/vhapi_configs/gear_workbench/loot_sack.json", VHAPI.of("gear/gear_workbench/loot_sack"));
//        registerManualConfigFile("/vhapi_configs/gear_workbench/trident.json", VHAPI.of("gear/gear_workbench/trident"));
//        registerManualConfigFile("/vhapi_configs/gear_workbench/plushie.json", VHAPI.of("gear/gear_workbench/plushie"));
//        registerManualConfigFile("/vhapi_configs/gear_workbench/rang.json", VHAPI.of("gear/gear_workbench/rang"));


        //Tooltips
        registerManualConfigFile("/vhapi_configs/tooltips.json", WoldsVaults.id("tooltips/wold_tooltips"));

        //Vault Gear Recipes
        if(WoldsVaultsConfig.COMMON.enableVaultGearRecipes.get()) {
            registerManualConfigFile("/vhapi_configs/recipes/gear_recipes.json", WoldsVaults.id("vault_recipes/gear/wold_gear_recipes"));
        }



    }

    public static void registerManualConfigFile(String filePath, ResourceLocation vhapiRegistryId) {
            try (InputStream stream = WoldsVaults.class.getResourceAsStream(filePath)) {
                if (stream == null) {
                    throw new IOException();
                }
                VHAPIProcesserUtils.addManualConfigFile(stream, vhapiRegistryId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

}
