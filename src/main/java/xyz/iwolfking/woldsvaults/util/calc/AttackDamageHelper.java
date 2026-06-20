package xyz.iwolfking.woldsvaults.util.calc;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class AttackDamageHelper {
    public static float getAttackDamage(Player player) {
        return getAttackDamage(player, AttributeSnapshotHelper.getInstance().getSnapshot(player));
    }

    public static float getAttackDamage(Player player, AttributeSnapshot snapshot) {
        float multiplier = 0.0F;
        multiplier += (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        multiplier += snapshot.getAttributeValue(ModGearAttributes.ATTACK_DAMAGE, VaultGearAttributeTypeMerger.doubleSum());
        multiplier += snapshot.getAttributeValue(ModGearAttributes.DAMAGE_INCREASE, VaultGearAttributeTypeMerger.floatSum()) * multiplier;
        return multiplier;
    }
}
