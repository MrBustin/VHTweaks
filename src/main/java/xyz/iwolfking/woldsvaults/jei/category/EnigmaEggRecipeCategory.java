package xyz.iwolfking.woldsvaults.jei.category;

import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.configs.EnigmaEggConfig;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.init.ModItems;
import xyz.iwolfking.woldsvaults.util.WoldListUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class EnigmaEggRecipeCategory implements IRecipeCategory<EnigmaEggConfig> {
    private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/jei/loot_info.png");
    public static final RecipeType<EnigmaEggConfig> RECIPE_TYPE = RecipeType.create("the_vault", "enigma_egg_info", EnigmaEggConfig.class);

    private static final TextComponent TITLE = new TextComponent("Enigma Egg");
    private final IDrawable background;
    private final IDrawable icon;

    public EnigmaEggRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 162, 108);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModItems.ENIGMA_EGG.getDefaultInstance());
    }

    @Nonnull
    public Component getTitle() {
        return TITLE;
    }

    @Nonnull
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    public IDrawable getIcon() {
        return this.icon;
    }

    @Nonnull
    public RecipeType<EnigmaEggConfig> getRecipeType() {
        return this.RECIPE_TYPE;
    }

    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @Nonnull
    public Class<? extends EnigmaEggConfig> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }

    @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, EnigmaEggConfig recipe, IFocusGroup focuses) {
        List<ItemStack> itemList = new ArrayList<>();

        recipe.POOL.forEach(b -> itemList.add(addChanceTooltip(b.value.generateItemStack())));

        List<List<ItemStack>> batchedList = WoldListUtils.getEvenlySplitList(itemList, 54);

        for(int i = 0; i < batchedList.size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addItemStacks(batchedList.get(i));
        }

    }

    public static ItemStack addChanceTooltip(ItemStack stack)
    {
        double totalWeight = ModConfigs.ENIGMA_EGG.POOL.getTotalWeight();
        CompoundTag nbt = stack.getOrCreateTagElement("display");
        ListTag list = nbt.getList("Lore", 8);

        WeightedList<ProductEntry> entries = ModConfigs.ENIGMA_EGG.POOL;
        for(WeightedList.Entry<ProductEntry> entry : entries) {
            if(entry.value.generateItemStack().getItem().equals(stack.getItem())) {
                MutableComponent component = new TextComponent("Chance: ");
                double chance = ((double) entry.weight / totalWeight) * 100;
                component.append(String.format("%.2f", chance));
                component.append("%");
                list.add(StringTag.valueOf(Component.Serializer.toJson(component.withStyle(ChatFormatting.YELLOW))));
            }
        }


        nbt.put("Lore", list);
        return stack;
    }


}
