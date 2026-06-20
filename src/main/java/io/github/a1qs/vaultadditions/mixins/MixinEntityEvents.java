package io.github.a1qs.vaultadditions.mixins;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.event.EntityEvents;
import iskallia.vault.item.gear.FocusItem;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.item.gear.VaultShieldItem;
import iskallia.vault.item.gear.WandItem;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityEvents.class)
public class MixinEntityEvents {
    @Inject(method = "onDamageTotem", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onDamageTotem(LivingHurtEvent event, CallbackInfo ci) {
        if (event.getSource().isBypassArmor() || !(event.getEntityLiving() instanceof Player player)) {
            ci.cancel();
            return;
        }

        Level world = player.getCommandSenderWorld();
        if (world.isClientSide() || !(world instanceof ServerLevel) || ServerVaults.get(world).isEmpty()) {
            ci.cancel();
            return;
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof IdolItem || offHand.getItem() instanceof VaultShieldItem || offHand.getItem() instanceof WandItem || offHand.getItem() instanceof FocusItem) {
            float damage = CommonEvents.PLAYER_STAT.invoke(PlayerStat.DURABILITY_DAMAGE, player, Math.max(1.0F, event.getAmount() / 6.0F)).getValue();
            if (damage <= 0 || (damage < 1 && world.random.nextFloat() > damage)) {
                ci.cancel();
                return;
            }
            offHand.hurtAndBreak(Math.max(1, (int) damage), event.getEntityLiving(), entity -> entity.broadcastBreakEvent(EquipmentSlot.OFFHAND));
        }
        ci.cancel();
    }
}
