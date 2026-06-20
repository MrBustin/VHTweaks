package xyz.iwolfking.woldsvaults.mixin.bettercombat;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.collision.OrientedBoundingBox;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.client.collision.WeaponHitBoxes;
import net.bettercombat.compatibility.CompatibilityFlags;
import net.bettercombat.compatibility.PehkuiHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.quark.base.module.config.Config;

import java.util.List;

@Restriction(
        require = {
                @Condition(type = Condition.Type.MOD, value = "bettercombat")
        }
)
@Mixin(value = TargetFinder.class, remap = false)
public abstract class MixinBetterCombatAttackRange {
    /**
     * @author iwolfking
     * @reason Add vault hunters attack range support
     */
    @Overwrite
    public static TargetFinder.TargetResult findAttackTargetResult(Player player, Entity cursorTarget, WeaponAttributes.Attack attack, double attackRange) {
        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
        double range = snapshot.getAttributeValue(ModGearAttributes.ATTACK_RANGE, VaultGearAttributeTypeMerger.doubleSum());
        attackRange += range;
        Vec3 origin = getInitialTracingPoint(player);
        List<Entity> entities = getInitialTargets(player, cursorTarget, attackRange);
        if (CompatibilityFlags.usePehkui) {
            attackRange *= PehkuiHelper.getScale(player);
        }

        boolean isSpinAttack = attack.angle() > 180.0;
        Vec3 size = WeaponHitBoxes.createHitbox(attack.hitbox(), attackRange, isSpinAttack);
        OrientedBoundingBox obb = new OrientedBoundingBox(origin, size, player.getXRot(), player.getYRot());
        if (!isSpinAttack) {
            obb = obb.offsetAlongAxisZ(size.z / 2.0);
        }

        obb.updateVertex();
        TargetFinder.CollisionFilter collisionFilter = new TargetFinder.CollisionFilter(obb);
        entities = collisionFilter.filter(entities);
        TargetFinder.RadialFilter radialFilter = new TargetFinder.RadialFilter(origin, obb.axisZ, attackRange, attack.angle());
        entities = radialFilter.filter(entities);
        return new TargetFinder.TargetResult(entities, obb);
    }


    @Shadow
    public static Vec3 getInitialTracingPoint(Player player) {
        return null;
    }

    @Shadow
    public static List<Entity> getInitialTargets(Player player, Entity cursorTarget, double attackRange) {
        return null;
    }
}