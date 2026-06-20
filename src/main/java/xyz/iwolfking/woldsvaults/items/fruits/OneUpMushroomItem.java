package xyz.iwolfking.woldsvaults.items.fruits;

import iskallia.vault.VaultMod;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.item.ItemVaultFruit;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.init.ModSounds;
import xyz.iwolfking.woldsvaults.util.VaultModifierUtils;

import java.util.List;

public class OneUpMushroomItem extends ItemVaultFruit {
    //Adds 1x Phoenix Stack
    public OneUpMushroomItem(ResourceLocation id) {
        super(id, 0);
    }
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add((new TextComponent("Adds")).withStyle(ChatFormatting.GRAY).append((new TextComponent(" 1x Phoenix Stack")).withStyle(ChatFormatting.GREEN)).append(new TextComponent(" to the Vault.").withStyle(ChatFormatting.GRAY)));
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
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), ModSounds.ONE_UP, SoundSource.MASTER, 0.5F, 1.0F);
        }

        return entityLiving.eat(level, stack);
    }

    @Override
    protected void successEaten(Level level, ServerPlayer sPlayer) {
        if(ServerVaults.get(level).isPresent()) {
            Vault vault = ServerVaults.get(level).get();
            VaultModifier<?> phoenixStack = VaultModifierRegistry.get(VaultMod.id("phoenix"));
            if(phoenixStack != null) {
                VaultModifierUtils.sendModifierAddedMessage(sPlayer, phoenixStack, 1);
                vault.get(Vault.MODIFIERS).addModifier(VaultModifierRegistry.get(VaultMod.id("phoenix")), 1, true, ChunkRandom.any());
            }

        }
    }

}
