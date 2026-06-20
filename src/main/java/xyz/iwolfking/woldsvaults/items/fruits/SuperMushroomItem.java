package xyz.iwolfking.woldsvaults.items.fruits;


import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.init.ModEffects;
import xyz.iwolfking.woldsvaults.init.ModSounds;

import java.util.List;

public class SuperMushroomItem extends ItemVaultFruit {
    //Adds 30 seconds of Colossus Ability
    private static final int COLOSSUS_DURATION_TICKS = 30 * 20;
    private static final int DOUBLE_SIZE_AMPLIFIER = 1;

    public SuperMushroomItem(ResourceLocation id) {
        super(id, 0);
    }
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add((new TextComponent("Doubles Your")).withStyle(ChatFormatting.GRAY).append((new TextComponent(" Size")).withStyle(ChatFormatting.GREEN)).append(new TextComponent(" For 30 Seconds.").withStyle(ChatFormatting.GRAY)));
        tooltip.add(TextComponent.EMPTY);
        tooltip.add((new TextComponent("Only edible inside a Vault")).withStyle(ChatFormatting.RED));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide() && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(level, player)) {
                return stack;
            }

            this.successEaten(level, player);
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), ModSounds.SUPER_MUSHROOM, SoundSource.MASTER, 1.0F, 1.0F);
        }

        return entityLiving.eat(level, stack);
    }
    @Override
    protected void successEaten(Level level, ServerPlayer sPlayer) {
        sPlayer.addEffect(new MobEffectInstance(ModEffects.COLOSSUS, COLOSSUS_DURATION_TICKS, DOUBLE_SIZE_AMPLIFIER, false, false, true));
    }
}
