package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.mojang.brigadier.context.CommandContext;
import iskallia.vault.command.debug.DebugCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.world.data.DiscoveredCapesData;

@Mixin(value = DebugCommand.class, remap = false)
public abstract class MixinDebugCommand {

    @Inject(method = "applyPreset", at = @At("RETURN"))
    private void woldsvaults$discoverPresetCapes(CommandContext<CommandSourceStack> context,
                                                 CallbackInfoReturnable<Integer> cir) {
        Object preset = context.getArgument("preset", Object.class);
        if (!(preset instanceof Enum<?> presetEnum) || "RESET".equals(presetEnum.name())
                || ModConfigs.PLAYER_CAPES == null) {
            return;
        }

        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(context, "player");
        } catch (Exception ignored) {
            try {
                player = context.getSource().getPlayerOrException();
            } catch (Exception noPlayer) {
                return;
            }
        }

        DiscoveredCapesData data = DiscoveredCapesData.get(player.getLevel());
        for (String capeId : ModConfigs.PLAYER_CAPES.getAll().keySet()) {
            data.discoverCape(player.getUUID(), capeId);
        }
    }
}
