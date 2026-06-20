package xyz.iwolfking.woldsvaults.items.gear.rang;

import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class VaultRangLogic {
    public static int timeout = 20;

    private static final ThreadLocal<VaultRangEntity> ACTIVE_RANG = new ThreadLocal<>();

    public static void setActiveRang(VaultRangEntity pickarang) {
        ACTIVE_RANG.set(pickarang);
    }

    public static int getTimeout(ItemStack stack) {
        if(stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            Optional<Float> timeout = data.getFirstValue(ModGearAttributes.RANGE);
            if(timeout.isPresent()) {
                return timeout.get().intValue();
            }
        }

        return timeout;
    }

    public static float getVelocity(ItemStack stack) {
        if(stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            Optional<Float> velocity = data.getFirstValue(ModGearAttributes.VELOCITY);
            if(velocity.isPresent()) {
                return velocity.get() * 100;
            }
        }

        return 1F;
    }

    public static float getReturningDamage(ItemStack stack) {
        if(stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            Optional<Float> returningDmg = data.getFirstValue(xyz.iwolfking.woldsvaults.init.ModGearAttributes.RETURNING_DAMAGE);
            if(returningDmg.isPresent()) {
                return returningDmg.get();
            }
        }

        return 0F;
    }

    public static int getPiercing(ItemStack stack) {
        if(stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            Optional<Integer> piercing = data.getFirstValue(xyz.iwolfking.woldsvaults.init.ModGearAttributes.PIERCING);
            if(piercing.isPresent()) {
                return piercing.get();
            }
        }

        return 0;
    }

    public static int getRicochet(ItemStack stack) {
        if(stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            Optional<Integer> ricochet = data.getFirstValue(xyz.iwolfking.woldsvaults.init.ModGearAttributes.RICOCHET);
            if(ricochet.isPresent()) {
                return ricochet.get();
            }
        }

        return 0;
    }

    public static DamageSource createDamageSource(Player player) {
        VaultRangEntity rang = ACTIVE_RANG.get();

        if (rang == null)
            return null;

        return new IndirectEntityDamageSource("player", rang, player).setProjectile();
    }

    public static boolean getIsFireResistant(boolean vanillaVal, Entity entity) {
        return true;
    }
}
