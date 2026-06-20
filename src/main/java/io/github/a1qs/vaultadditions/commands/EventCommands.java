package io.github.a1qs.vaultadditions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.events.VaultAdditionsEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class EventCommands {
    public EventCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vaultadditions")
                .requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()))
                .then(Commands.literal("event")
                        .then(Commands.literal("stopEvent")
                                .executes(this::stopEvent)
                        )
                        .then(Commands.literal("getCurrentEvent")
                                .executes(this::getCurrentEvent)
                        )
                        .then(Commands.literal("scheduleEventDate")
                                .then(Commands.argument("dd/MM/yyyy HH:mm:ss", StringArgumentType.string())
                                        .executes(this::scheduleEventDate)
                                )
                        )
                        .then(Commands.literal("clearScheduledEvents")
                                .executes(this::clearScheduledEvents)
                        )
                )
        );
    }



    private int stopEvent(CommandContext<CommandSourceStack> context) {
        EventData data = EventData.get(ServerLifecycleHooks.getCurrentServer());
        data.stopEvent();
        return 0;
    }

    private int getCurrentEvent(CommandContext<CommandSourceStack> context) {
        EventData data = EventData.get(ServerLifecycleHooks.getCurrentServer());
        VaultAdditionsEvent activeEvent = data.getActiveEvent();
        if(activeEvent != null) {
            context.getSource().sendSuccess(new TextComponent("=== ")
                    .append(new TextComponent("Current active Event").withStyle(ChatFormatting.AQUA))
                    .append(new TextComponent(" ===\n").withStyle(ChatFormatting.WHITE))
                    .append(new TextComponent("    Event ID: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(new TextComponent(activeEvent.getEventId() + "\n").withStyle(ChatFormatting.YELLOW))
                    .append(new TextComponent("    Event Start Message: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(activeEvent.getEventStartMessage() + "\n")
                    .append(new TextComponent("    Event Duration: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(new TextComponent(activeEvent.getEventDuration() + "\n").withStyle(ChatFormatting.YELLOW))
                    .append(new TextComponent("    Is Crystal Submission: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(new TextComponent(activeEvent.isCrystalSubmissionEvent() + "\n").withStyle(ChatFormatting.YELLOW))
                    .append(new TextComponent("    Required Crystal Submission: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(new TextComponent(activeEvent.getCrystalsSubmitted() + " / " + activeEvent.getRequiredCrystals())),
                    false);
            return 0;
        } else {
            context.getSource().sendFailure(new TextComponent("No active event found!"));
            return 1;
        }
    }

    private int clearScheduledEvents(CommandContext<CommandSourceStack> context) {
        EventData data = EventData.get(ServerLifecycleHooks.getCurrentServer());
        if(data.getScheduledEvents().isEmpty()) {
            context.getSource().sendFailure(new TextComponent("No scheduled events!"));
            return 1;
        }
        data.getScheduledEvents().clear();
        context.getSource().sendSuccess(new TextComponent("Cleared all scheduled Events!").withStyle(ChatFormatting.YELLOW), true);
        return 0;
    }

    private int scheduleEventDate(CommandContext<CommandSourceStack> context) {
        EventData data = EventData.get(ServerLifecycleHooks.getCurrentServer());
        data.addEventDate(StringArgumentType.getString(context, "dd/MM/yyyy HH:mm:ss"));
        return 0;
    }

    private int getRequiredPermissionLevel() {
        return 2;
    }
}
