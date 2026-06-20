package io.github.a1qs.vaultadditions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModBlocks;
import io.github.a1qs.vaultadditions.item.LootStatueBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class SpecialCommands {
    private static final DateTimeFormatter STATUE_LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SHARED_LOG_FILE_NAME = "all.log";
    private static final List<ItemStack> STATUES = List.of(
            new ItemStack(ModBlocks.LOOT_STATUE_VAULT.get()),
            new ItemStack(ModBlocks.LOOT_STATUE_GIFT.get()),
            new ItemStack(ModBlocks.LOOT_STATUE_GIFT_MEGA.get()),
            new ItemStack(ModBlocks.LOOT_STATUE_ARENA.get())
    );

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_STATUES = (context, builder) -> {
        List<ResourceLocation> itemIdentifiers = STATUES.stream()
                .map(itemStack -> itemStack.getItem().getRegistryName())
                .toList();

        return SharedSuggestionProvider.suggestResource(itemIdentifiers.stream(), builder);
    };

    public SpecialCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("grantLootStatue")
                .requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()))
                .then(Commands.argument("ItemId", ResourceLocationArgument.id())
                        .suggests(SUGGEST_STATUES)
                        .then(Commands.argument("PlayerName", StringArgumentType.greedyString())
                                .executes(this::grantLootStatue)
                        )
                )


        );
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    private int grantLootStatue(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String playerName = StringArgumentType.getString(context, "PlayerName");
        ResourceLocation itemId = ResourceLocationArgument.getId(context, "ItemId");

        ItemStack statue = new ItemStack(ForgeRegistries.ITEMS.getValue(itemId));
        LootStatueBlockItem.setStatueName(statue, playerName);
        ServerPlayer player = context.getSource().getPlayerOrException();

        boolean added = player.getInventory().add(statue);

        if (!added || !statue.isEmpty()) {
            // Drop the item if it couldn't be fully added
            player.drop(statue, false);
        }
        writeStatueRedeemLog(player, playerName, itemId);
        VaultAdditions.LOGGER.info("{} has been granted a Loot Statue of type {} with statue_name {}", player.getName().getString(), itemId, playerName);
        context.getSource().sendSuccess(new TextComponent("You've been granted a Loot Statue").withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static void writeStatueRedeemLog(ServerPlayer executor, String statueName, ResourceLocation itemId) {
        MinecraftServer server = executor.getServer();
        String executorName = executor.getGameProfile().getName();
        String timestamp = LocalDateTime.now().format(STATUE_LOG_TIME_FORMAT);
        String logEntry = String.format("%s | executor=%s | statue_name=%s | item=%s%n", timestamp, executorName, statueName, itemId);

        try {
            for (Path logDirectory : getLogDirectories(server)) {
                Files.createDirectories(logDirectory);

                Path playerLogFile = logDirectory.resolve(sanitizeFileName(executorName) + ".log");
                Path sharedLogFile = logDirectory.resolve(SHARED_LOG_FILE_NAME);

                appendLogLine(playerLogFile, logEntry);
                appendLogLine(sharedLogFile, logEntry);
                VaultAdditions.LOGGER.info("StatueRedeems log written to {}", playerLogFile.toAbsolutePath());
            }
        } catch (Exception e) {
            VaultAdditions.LOGGER.error("Failed to write StatueRedeems log entry for {}", executorName, e);
        }
    }

    private static Set<Path> getLogDirectories(MinecraftServer server) {
        Set<Path> logDirectories = new LinkedHashSet<>();
        logDirectories.add(server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize().resolve("logs").resolve("StatueRedeems"));
        logDirectories.add(server.getServerDirectory().toPath().toAbsolutePath().normalize().resolve("logs").resolve("StatueRedeems"));
        return logDirectories;
    }

    private static void appendLogLine(Path logFile, String logEntry) throws IOException {
        Files.writeString(logFile, logEntry, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static String sanitizeFileName(String playerName) {
        String sanitized = playerName.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        return sanitized.isEmpty() ? "unknown_player" : sanitized;
    }


}
