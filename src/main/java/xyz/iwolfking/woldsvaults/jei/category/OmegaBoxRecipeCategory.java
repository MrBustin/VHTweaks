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
import xyz.iwolfking.woldsvaults.configs.OmegaBoxConfig;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.init.ModItems;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class OmegaBoxRecipeCategory implements IRecipeCategory<OmegaBoxConfig> {
    private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/jei/loot_info.png");
    public static final RecipeType<OmegaBoxConfig> RECIPE_TYPE = RecipeType.create("woldsvaults", "omega_box_info", OmegaBoxConfig.class);

    private static final TextComponent TITLE = new TextComponent("Omega Box");
    private final IDrawable background;
    private final IDrawable icon;

    public OmegaBoxRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 162, 108);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModItems.OMEGA_BOX.getDefaultInstance());
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
    public RecipeType<OmegaBoxConfig> getRecipeType() {
        return this.RECIPE_TYPE;
    }

    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @Nonnull
    public Class<? extends OmegaBoxConfig> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }

    @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, OmegaBoxConfig recipe, IFocusGroup focuses) {
        List<ItemStack> itemList = new ArrayList<>();
        recipe.POOL.forEach((productEntry, aDouble) -> itemList.add(productEntry.generateItemStack()));

        int count = itemList.size();

        for(int i = 0; i < count; ++i) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addItemStack(addChanceTooltip((ItemStack)itemList.get(i)));
        }

    }

    public static ItemStack addChanceTooltip(ItemStack stack)
    {
        double totalWeight = ModConfigs.OMEGA_BOX.POOL.getTotalWeight();
        


        WeightedList<ProductEntry> entries = ModConfigs.OMEGA_BOX.POOL;
        for(WeightedList.Entry<ProductEntry> entry : entries) {
            if(entry.value.getItem().equals(stack.getItem()) && (stack.getTag() == null || stack.getTag().equals(entry.value.getNBT()))) {
                CompoundTag nbt = stack.getOrCreateTagElement("display");
                ListTag list = nbt.getList("Lore", 8);
                MutableComponent component = new TextComponent("Chance: ");
                double chance = ((double) entry.weight / totalWeight) * 100;
                component.append(String.format("%.2f", chance));
                component.append("%");
                list.add(StringTag.valueOf(Component.Serializer.toJson(component.withStyle(ChatFormatting.YELLOW))));
                nbt.put("Lore", list);
            }
        }

        return stack;
    }

}
