package io.github.a1qs.vaultadditions.vault.gear.gecko;

import io.github.a1qs.vaultadditions.util.ModelUtil;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VaultGeckoModelProvider<T extends Item & VaultGearItem & IAnimatable> extends AnimatedGeoModel<T> {
    private static final ResourceLocation DEFAULT_MODEL = VaultMod.id("geo/armor/grogu.geo.json");
    private static final ResourceLocation DEFAULT_TEXTURE = VaultMod.id("textures/item/gear/armor/grogu/texture.png");
    private static final ResourceLocation DEFAULT_ANIMATION = VaultMod.id("animations/armor/grogu.animation.json");

    private ItemStack lastItemStack;

    public void using(ItemStack itemStack) {
        if (itemStack != null && !itemStack.isEmpty()) {
            this.lastItemStack = itemStack;
        }
    }

    public ResourceLocation getModelLocation(Item item) {
        return getModelLocation(lastItemStack);
    }

    public ResourceLocation getTextureLocation(Item item) {
        return getTextureLocation(lastItemStack);
    }

    public ResourceLocation getAnimationFileLocation(Item item) {
        return getAnimationFileLocation(lastItemStack);
    }

    public ResourceLocation getModelLocation(ItemStack itemStack) {
        this.lastItemStack = itemStack;
        return ModelUtil.getDynamicModel(itemStack) instanceof VaultGeckoModel model
                ? model.getModelPath()
                : DEFAULT_MODEL;
    }

    public ResourceLocation getTextureLocation(ItemStack itemStack) {
        this.lastItemStack = itemStack;
        return ModelUtil.getDynamicModel(itemStack) instanceof VaultGeckoModel model
                ? model.getTexturePath()
                : DEFAULT_TEXTURE;
    }

    public ResourceLocation getAnimationFileLocation(ItemStack itemStack) {
        this.lastItemStack = itemStack;
        return ModelUtil.getDynamicModel(itemStack) instanceof VaultGeckoModel model
                ? model.getAnimationPath()
                : DEFAULT_ANIMATION;
    }
}
