package io.github.a1qs.vaultadditions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.a1qs.vaultadditions.vault.gear.effect.AttributeTransmogEffect;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.mana.Mana;
import iskallia.vault.mana.ManaAction;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;

import java.util.List;
import java.util.Locale;

public class DebugCommands {
    public DebugCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vaultadditions")
                .requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()))
                .then(Commands.literal("debug")
                        .then(Commands.literal("giveMeMyMana")
                                .executes(this::giveMeMyMana)
                        )
                        .then(Commands.literal("resetCooldowns")
                                .executes(this::resetCooldowns)
                        )
                        .then(Commands.literal("configureWorldBorders")
                                .then(Commands.argument("worldBorderSize", DoubleArgumentType.doubleArg(-5.9999968E7D, 5.9999968E7D))
                                        .executes(this::configureWorldBorders)
                                )
                        )
                        .then(Commands.literal("serializeHeldModifiers")
                                .executes(this::serializeHeldModifiers))
                )
        );
    }

    private int giveMeMyMana(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Mana.increase(player, ManaAction.PLAYER_ACTION, Integer.MAX_VALUE);
        context.getSource().sendSuccess(new TextComponent("Added " + Integer.MAX_VALUE + " Mana!").withStyle(ChatFormatting.AQUA), true);
        return Command.SINGLE_SUCCESS;
    }

    private int resetCooldowns(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get(player.getLevel());
        AbilityTree abilityTree = abilitiesData.getAbilities(player);

        abilityTree.iterate(Ability.class, (ability) ->
                ability.getCooldown().ifPresent(cooldown -> {
                    ability.reduceCooldownBy(1000000);
                })
        );

        abilityTree.sync(SkillContext.of(player));
        context.getSource().sendSuccess(new TextComponent("Reset the Cooldown of player: " + player.getName().getString() + "!"), true);
        return Command.SINGLE_SUCCESS;
    }

    private int configureWorldBorders(CommandContext<CommandSourceStack> context) {
        double amount = DoubleArgumentType.getDouble(context, "worldBorderSize");

        List<ResourceKey<Level>> levels = List.of(Level.OVERWORLD, Level.NETHER, Level.END);

        for(ResourceKey<Level> level : levels) {
            WorldBorder border = context.getSource().getServer().getLevel(level).getWorldBorder();
            border.setSize(amount);
        }

        context.getSource().sendSuccess(new TranslatableComponent("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", amount)), true);
        return Command.SINGLE_SUCCESS;
    }

    private int serializeHeldModifiers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !VaultGearData.hasData(stack)) {
            context.getSource().sendFailure(new TextComponent("Held item has no modifiers!"));
            return 0;
        }
        context.getSource().sendSuccess(new TextComponent("Serialized Modifiers:"), true);
        VaultGearData data = VaultGearData.read(stack);
        for (VaultGearModifier.AffixType type : VaultGearModifier.AffixType.values()) {
            for (VaultGearModifier<?> modifier : data.getModifiers(type)) {
                context.getSource().sendSuccess(new TextComponent(" "), true);
                context.getSource().sendSuccess(new TextComponent(new AttributeTransmogEffect<>(modifier).toString()), true);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }
}
