package xyz.iwolfking.woldsvaults.items.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.gear.*;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.item.VaultGearToolTier;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.VaultSwordItem;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.models.Battlestaffs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class VaultBattleStaffItem extends SwordItem implements VaultGearItem, DyeableLeatherItem {

    public VaultBattleStaffItem(ResourceLocation id, Properties builder) {
        /*  49 */     super((Tier) VaultGearToolTier.INSTANCE, 0, -2.4F, builder);
        /*  50 */     setRegistryName(id);
        /*     */   }
    /*     */
    /*     */
    /*     */   @Nullable
    public ResourceLocation getRandomModel(ItemStack stack, Random random, @Nullable Player player, @Nullable DiscoveredModelsData discoveredModelsData) {
        VaultGearData gearData = VaultGearData.read(stack);
        EquipmentSlot intendedSlot = this.getGearType(stack).getEquipmentSlot();
        return ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRandomRoll(stack, gearData, intendedSlot, random, player, discoveredModelsData);
    }
    /*     */
    /*     */
    /*     */   public Optional<? extends DynamicModel<?>> resolveDynamicModel(ItemStack stack, ResourceLocation key) {
        /*  66 */     return Battlestaffs.REGISTRY.get(key);
        /*     */   }
    /*     */
    /*     */
    /*     */   @Nullable
    /*     */   public EquipmentSlot getIntendedSlot(ItemStack stack) {
        /*  72 */     return EquipmentSlot.MAINHAND;
        /*     */   }
    /*     */
    /*     */
    /*     */   @NotNull
    /*     */   public VaultGearClassification getClassification(ItemStack stack) {
        /*  78 */     return VaultGearClassification.SWORD;
        /*     */   }
    /*     */
    /*     */
    /*     */   @Nonnull
    /*     */   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
        /*  84 */     return ProficiencyType.SWORD;
        /*     */   }

    @NotNull
    @Override
    public VaultGearType getGearType(ItemStack itemStack) {
        return VaultGearType.SWORD;
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   public float getDestroySpeed(ItemStack stack, BlockState state) {
        /*  91 */     return 1.0F;
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        /*  96 */     return false;
        /*     */   }
    /*     */
    /*     */
    /*     */   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        /* 101 */     return VaultGearHelper.getModifiers(stack, slot);
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        /* 106 */     return VaultGearHelper.shouldPlayGearReequipAnimation(oldStack, newStack, slotChanged);
        /*     */   }
    /*     */
    /*     */
    /*     */   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        /* 111 */     if (allowdedIn(group)) {
            /* 112 */       items.add(defaultItem());
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
        /* 118 */     return super.getDefaultTooltipHideFlags(stack) | ItemStack.TooltipPart.MODIFIERS.getMask();
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean isRepairable(ItemStack stack) {
        /* 123 */     return false;
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean isDamageable(ItemStack stack) {
        /* 128 */     return (VaultGearData.read(stack).getState() == VaultGearState.IDENTIFIED);
        /*     */   }
    /*     */
    /*     */
    /*     */   public int getMaxDamage(ItemStack stack) {
        /* 133 */     return ((Integer)VaultGearData.read(stack).get(ModGearAttributes.DURABILITY, VaultGearAttributeTypeMerger.intSum())).intValue();
        /*     */   }
    /*     */
    /*     */
    /*     */   public Component getName(ItemStack stack) {
        /* 138 */     return VaultGearHelper.getDisplayName(stack, super.getName(stack));
        /*     */   }
    /*     */
    /*     */
    /*     */   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        /* 143 */     return VaultGearHelper.rightClick(world, player, hand, super.use(world, player, hand));
        /*     */   }
    /*     */
    /*     */
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
            super.inventoryTick(stack, world, entity, itemSlot, isSelected);
            if (entity instanceof ServerPlayer) { ServerPlayer player = (ServerPlayer)entity;
                vaultGearTick(stack, player); }
    }

    /*     */   @OnlyIn(Dist.CLIENT)
    /*     */   public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        /* 157 */     super.appendHoverText(stack, world, tooltip, flag);
        /* 158 */     tooltip.addAll(createTooltip(stack, GearTooltip.itemTooltip()));
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        /* 163 */     return (ToolActions.SWORD_SWEEP == toolAction);
        /*     */   }
    /*     */ }


