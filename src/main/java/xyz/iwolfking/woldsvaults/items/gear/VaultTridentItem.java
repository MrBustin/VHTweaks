package xyz.iwolfking.woldsvaults.items.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.gear.*;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.models.Tridents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class VaultTridentItem extends TridentItem implements VaultGearItem, DyeableLeatherItem {
    public VaultTridentItem(ResourceLocation id, Properties builder) {
        /*  49 */     super(builder);
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
        /*  66 */     return Tridents.REGISTRY.get(key);
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
        /*  78 */     return VaultGearClassification.AXE;
        /*     */   }
    /*     */
    /*     */
    /*     */   @Nonnull
    /*     */   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
        /*  84 */     return ProficiencyType.AXE;
        /*     */   }

    @NotNull
    @Override
    public VaultGearType getGearType(ItemStack itemStack) {
        return VaultGearType.AXE;
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

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if(enchantment.equals(Enchantments.LOYALTY) || enchantment.equals(Enchantments.RIPTIDE) || enchantment.equals(Enchantments.CHANNELING)) {
            return false;
        }
        else if(enchantment.equals(Enchantments.MOB_LOOTING)) {
            return true;
        }
        else {
            return super.canApplyAtEnchantingTable(stack, enchantment);
        }
    }

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

    @Override
    public boolean isFireResistant() {
        return true;
    }

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
    

    @Override
    public int getUseDuration(ItemStack stack) {
        //VaultGearData data = VaultGearData.read(stack);
        //float percentDecrease = data.get(xyz.iwolfking.woldsvaults.init.ModGearAttributes.TRIDENT_WINDUP, VaultGearAttributeTypeMerger.floatSum());
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int duration) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            VaultGearData data = VaultGearData.read(stack);
            float percentDecrease = data.get(xyz.iwolfking.woldsvaults.init.ModGearAttributes.TRIDENT_WINDUP, VaultGearAttributeTypeMerger.floatSum());
            int i = this.getUseDuration(stack) - duration;
            if (i >= (10 * (1 -percentDecrease))) {
                int j = data.get(xyz.iwolfking.woldsvaults.init.ModGearAttributes.TRIDENT_RIPTIDE, VaultGearAttributeTypeMerger.intSum());
                if (j <= 0 || player.isInWaterOrRain()) {
                    if (!level.isClientSide) {
                        stack.hurtAndBreak(1, player, (p_43388_) -> {
                            p_43388_.broadcastBreakEvent(entity.getUsedItemHand());
                        });
                        if (j == 0) {
                            ThrownTrident throwntrident = new ThrownTrident(level, player, stack);
                            throwntrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
                            if (player.getAbilities().instabuild) {
                                throwntrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            level.addFreshEntity(throwntrident);
                            level.playSound((Player)null, throwntrident, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!player.getAbilities().instabuild) {
                                player.getInventory().removeItem(stack);
                            }
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (j > 0) {
                        float f7 = player.getYRot();
                        float f = player.getXRot();
                        float f1 = -Mth.sin(f7 * ((float)Math.PI / 180F)) * Mth.cos(f * ((float)Math.PI / 180F));
                        float f2 = -Mth.sin(f * ((float)Math.PI / 180F));
                        float f3 = Mth.cos(f7 * ((float)Math.PI / 180F)) * Mth.cos(f * ((float)Math.PI / 180F));
                        float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                        float f5 = 3.0F * ((1.0F + (float)j) / 4.0F);
                        f1 *= f5 / f4;
                        f2 *= f5 / f4;
                        f3 *= f5 / f4;
                        player.push((double)f1, (double)f2, (double)f3);
                        player.startAutoSpinAttack(20);
                        if (player.isOnGround()) {
                            float f6 = 1.1999999F;
                            player.move(MoverType.SELF, new Vec3(0.0D, (double)1.1999999F, 0.0D));
                        }

                        SoundEvent soundevent;
                        if (j >= 3) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
                        } else if (j == 2) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
                        } else {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
                        }

                        level.playSound((Player)null, player, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }

    /*     */
    /*     */
    /*     */   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        /* 143 */     return VaultGearHelper.rightClick(world, player, hand, super.use(world, player, hand));
        /*     */   }
    /*     */
    /*     */
    /*     */   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        /* 148 */     super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        /* 149 */     if (entity instanceof ServerPlayer) { ServerPlayer player = (ServerPlayer)entity;
            /* 150 */       vaultGearTick(stack, player); }
        /*     */
        /*     */   }
    /*     */
    /*     */
    /*     */   @OnlyIn(Dist.CLIENT)
    /*     */   public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        /* 157 */     super.appendHoverText(stack, world, tooltip, flag);
        /* 158 */     tooltip.addAll(createTooltip(stack, GearTooltip.itemTooltip()));
        /*     */   }

    /*     */ }


