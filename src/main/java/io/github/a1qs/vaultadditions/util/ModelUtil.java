package io.github.a1qs.vaultadditions.util;

import io.github.a1qs.vaultadditions.init.ModModels;
import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModel;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;


public class ModelUtil {
    private static final Map<VaultGearItem, AnimationFactory> ANIMATION_FACTORIES = new HashMap<>();

    public static DynamicModel<?> getDynamicModel(ItemStack itemStack) {
        return getDynamicModel(itemStack, true);
    }

    public static DynamicModel<?> getDynamicModel(ItemStack itemStack, boolean parent) {
        if (itemStack == null) {
            return null;
        }

        GearDataCache cache = GearDataCache.of(itemStack);
        return cache.getGearModel().flatMap(model -> ModDynamicModels.REGISTRIES.getModel(itemStack.getItem(), model))
                .map(model -> parent && model instanceof ArmorPieceModel piece ? piece.getArmorModel() : model)
                .orElse(null);
    }

    public static ArmorModel getArmorModel(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        GearDataCache cache = GearDataCache.of(itemStack);
        return cache.getGearModel().flatMap(ModDynamicModels.Armor.PIECE_REGISTRY::get)
                .map(ArmorPieceModel::getArmorModel)
                .orElse(null);
    }

    public static ArmorModel getWornSet(Player player) {
        Inventory inventory = player.getInventory();
        slots: for (int slot = 0; slot < 4; slot++) {
            ArmorModel model = getArmorModel(inventory.getArmor(slot));
            if (model != null) {
                for (EquipmentSlot gearSlots : model.getPieces().keySet()) {
                    if (getArmorModel(inventory.getArmor(gearSlots.getIndex())) != model) {
                        continue slots;
                    }
                }
                return model;
            }
        }
        return null;
    }

    public static ResourceLocation getArmorModelId(ItemStack itemStack) {
        ArmorModel model = getArmorModel(itemStack);
        return model == null ? null : model.getId();
    }

    public static boolean isWearingHoySet(Player player) {
        ArmorModel model = getWornSet(player);
        return model != null && ModModels.HOY_ARMOR.contains(getWornSet(player));
    }

    public static boolean isWearingHokageRobesSet(Player player) {
        ArmorModel model = getWornSet(player);
        return model != null && ModModels.HOKAGE_ARMOR.contains(model);
    }

    public static boolean isWearingJetpackPiece(Player player) {
        ArmorModel chestModel = getArmorModel(player.getInventory().getArmor(EquipmentSlot.CHEST.getIndex()));
        return chestModel != null && ModModels.HOY_ARMOR.contains(chestModel);
    }


    public static boolean isWearingArmorPiece(ArmorModel model, EquipmentSlot slot, Player player) {
        if (model.getPiece(slot).isPresent()) {
            ItemStack itemStack = player.getInventory().getArmor(slot.getIndex());
            return getArmorModel(itemStack) == model;
        }
        return false;
    }

    public static boolean isWearingArmorSet(ArmorModel armorSet, Player player) {
         return armorSet.getPieces().entrySet().stream().allMatch(entry -> {
            EquipmentSlot slot = entry.getKey();
            ItemStack itemStack = player.getInventory().getArmor(slot.getIndex());
            return getArmorModel(itemStack) == armorSet;
        });
    }

    public static <I extends VaultGearItem & IAnimatable> AnimationFactory getAnimationFactory(I item) {
        return ANIMATION_FACTORIES.computeIfAbsent(item, i -> GeckoLibUtil.createFactory(item));
    }

    public static VaultGeckoModel getGeckoModel(DynamicModel<?> model) {
        if (model instanceof VaultGeckoModel gecko) {
            return gecko;
        } else if (model instanceof ArmorPieceModel piece && piece.getArmorModel() instanceof VaultGeckoModel gecko) {
            return gecko;
        }
        return null;
    }
}
