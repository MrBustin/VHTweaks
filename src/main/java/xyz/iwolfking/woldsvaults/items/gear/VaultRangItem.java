package xyz.iwolfking.woldsvaults.items.gear;

import com.google.common.collect.Multimap;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.VaultGearType;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.BasicItem;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import vazkii.quark.base.handler.QuarkSounds;
import xyz.iwolfking.woldsvaults.items.gear.rang.VaultRangEntity;
import xyz.iwolfking.woldsvaults.items.gear.rang.VaultRangLogic;
import xyz.iwolfking.woldsvaults.models.Rangs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "woldsvaults")
public class VaultRangItem extends BasicItem implements VaultGearItem, DyeableLeatherItem {

    public static final String TAG_RANG_FLIGHT_ID = "RangFlightId";

    public VaultRangItem(ResourceLocation id,  Item.Properties properties) {
        super(id, properties);
    }

    public static boolean isInFlight(ItemStack stack) {
        return stack.getItem() instanceof VaultRangItem
                && stack.hasTag()
                && stack.getTag().hasUUID(TAG_RANG_FLIGHT_ID);
    }

    @Nullable
    public static UUID getInFlightEntityId(ItemStack stack) {
        return isInFlight(stack) ? stack.getTag().getUUID(TAG_RANG_FLIGHT_ID) : null;
    }

    private static void clearInFlight(ItemStack stack) {
        if (stack.hasTag()) {
            stack.getTag().remove(TAG_RANG_FLIGHT_ID);
        }
    }

    @Nullable
    private static VaultRangEntity findInFlightEntity(ItemStack stack, ServerLevel level) {
        UUID flightId = getInFlightEntityId(stack);
        if (flightId == null) return null;
        return level.getEntity(flightId) instanceof VaultRangEntity rang ? rang : null;
    }

    private static void cleanupInFlightStack(ItemStack stack, Player player) {
        if (!isInFlight(stack)) return;
        if (player.level instanceof ServerLevel serverLevel) {
            VaultRangEntity rang = findInFlightEntity(stack, serverLevel);
            if (rang != null) rang.discard();
        }
        clearInFlight(stack);
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntityItem().getItem();
        if (isInFlight(stack)) {
            cleanupInFlightStack(stack, event.getPlayer());
        }
    }


    @Override
    public boolean isCorrectToolForDrops(@Nonnull BlockState blockIn) {
        return false;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return VaultGearHelper.getDisplayName(stack, super.getName(stack));
    }



    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (isInFlight(itemstack)) {
            return InteractionResultHolder.fail(itemstack);
        }
        if(VaultGearHelper.rightClick(worldIn, playerIn, handIn, super.use(worldIn, playerIn, handIn)).getResult().equals(InteractionResult.FAIL)) {
            return InteractionResultHolder.fail(itemstack);
        }

        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(playerIn);
        float velocity = VaultRangLogic.getVelocity(itemstack);
        Double attackSpeed = snapshot.getAttributeValue(ModGearAttributes.ATTACK_SPEED, VaultGearAttributeTypeMerger.doubleSum());
        Vec3 pos = playerIn.position();
        worldIn.playSound(null, pos.x, pos.y, pos.z, QuarkSounds.ENTITY_PICKARANG_THROW, SoundSource.NEUTRAL, 0.5F + velocity * 0.14F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

        if(!worldIn.isClientSide) {
            Inventory inventory = playerIn.getInventory();
            int slot = handIn == InteractionHand.OFF_HAND ? inventory.getContainerSize() - 1 : inventory.selected;
            VaultRangEntity entity = new VaultRangEntity(worldIn, playerIn);
            entity.setThrowData(slot, itemstack);
            entity.shoot(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F + velocity * 0.325F, 0F);
            worldIn.addFreshEntity(entity);

            ItemStack placeholder = itemstack.copy();
            placeholder.getOrCreateTag().putUUID(TAG_RANG_FLIGHT_ID, entity.getUUID());
            playerIn.setItemInHand(handIn, placeholder);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    @Override
    public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
        return 0F;
    }

    @Override
    public Optional<? extends DynamicModel<?>> resolveDynamicModel(ItemStack stack, ResourceLocation key) {
        return Rangs.REGISTRY.get(key);
    }

    @NotNull
    @Override
    public VaultGearClassification getClassification(ItemStack itemStack) {
        return VaultGearClassification.SWORD;
    }

//    @NotNull
//    @Override
//    @SuppressWarnings({"deprecation","removal"})
//    public ProficiencyType getCraftingProficiencyType(ItemStack itemStack) {
//        return ProficiencyType.SWORD;
//    }

    @NotNull
    @Override
    public VaultGearType getGearType(ItemStack itemStack) {
        return VaultGearType.SWORD;
    }

    @Nullable
    public ResourceLocation getRandomModel(ItemStack stack, Random random, @Nullable Player player, @Nullable DiscoveredModelsData discoveredModelsData) {
        VaultGearData gearData = VaultGearData.read(stack);
        EquipmentSlot intendedSlot = this.getGearType(stack).getEquipmentSlot();
        return ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRandomRoll(stack, gearData, intendedSlot, random, player, discoveredModelsData);
    }


    @Override
    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
        return super.getDefaultTooltipHideFlags(stack) | ItemStack.TooltipPart.MODIFIERS.getMask();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return VaultGearHelper.getModifiers(stack, slot);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (entity instanceof ServerPlayer player) {
            if (isInFlight(stack) && world instanceof ServerLevel serverLevel) {
                VaultRangEntity rang = findInFlightEntity(stack, serverLevel);
                if (rang == null || !rang.isAlive()) {
                    clearInFlight(stack);
                }
            }
            this.vaultGearTick(stack, player);
        }

    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        if (isInFlight(item)) {
            cleanupInFlightStack(item, player);
        }
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isInFlight(stack)) {
            tooltip.add(new TextComponent("(In Flight)").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        }
        tooltip.addAll(this.createTooltip(stack, GearTooltip.itemTooltip()));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(this.defaultItem());
        }

    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return VaultGearData.read(stack).getState() == VaultGearState.IDENTIFIED;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return VaultGearData.read(stack).get(ModGearAttributes.DURABILITY, VaultGearAttributeTypeMerger.intSum());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if(enchantment.equals(Enchantments.UNBREAKING) || enchantment.equals(Enchantments.MOB_LOOTING)) {
            return true;
        }
        else {
            return false;
        }
    }
}
