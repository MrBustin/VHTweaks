package xyz.iwolfking.woldsvaults.mixin.the_vault;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.client.gui.screen.player.StatisticsElementContainerScreenData;
import iskallia.vault.client.gui.screen.player.element.GearAttributeStatLabel;
import iskallia.vault.client.gui.screen.player.element.StatLabelElementBuilder;
import iskallia.vault.util.calc.AttributeLimitHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.gear.attribute.ModAttributeLimitHelper;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

import java.util.List;

@Mixin(value = StatisticsElementContainerScreenData.class, remap = false)
public class MixinStatisticsElementContainerScreenData {

    @Shadow
    @Final
    protected Player player;

    @Inject(method = "getStatListPlayer", at = @At(value = "INVOKE",
            target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"))
    private void addNewDamageStats(CallbackInfoReturnable<List<StatLabelElementBuilder<?>>> cir, @Local List<StatLabelElementBuilder<?>> result) {
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.AP_SCALING_DAMAGE));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.CHAINING_DAMAGE));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.CHANNELING_CHANCE));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.ECHOING_CHANCE));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.ECHOING_DAMAGE, ModAttributeLimitHelper::getEchoingDamageLimit));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.HEXING_CHANCE));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.EXECUTION_DAMAGE));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.REAVING_DAMAGE));
        result.add( GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.MANA_COST_REDUCTION, ModAttributeLimitHelper::getManaCostReductionLimit));
    }
}
