package io.github.a1qs.vaultadditions.util;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Random;

public class TimeUtil {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final Random random = new Random();
    private static final String[] messages = {
            "%d %s, %d %s, and %d %s remain until the World can no longer grow.",
            "%d %s, %d %s, and %d %s left until the World cant grow anymore!",
            "Time is ticking! Only %d %s, %d %s, and %d %s to go until the World will no longer be able to expand.",
            "Just %d %s, %d %s, and %d %s left for the World to stop expanding.",
            "The clock is ticking: %d %s, %d %s, and %d %s remain until the World can no longer be expanded.",
            "Only %d %s, %d, %s, and %d %s until the World border is frozen.",
            "Only %d %s, %d, %s, and %d %s until the World Expansion will halt."
    };


    public static boolean pastDate() {
        if(!ServerConfigs.LIMIT_TIME_FOR_EXPANSION.get()) return true;

        String date = ServerConfigs.STOP_ACCEPTING_GEMSTONES_DATE.get();
        try {
            LocalDate configDate = LocalDate.parse(date, TIME_FORMAT);
            LocalDate current = LocalDate.now(); //Gets the current time based off of SystemTime

            if (current.isEqual(configDate) || current.isAfter(configDate)) {
                return true;
            }
        } catch (DateTimeParseException e) {
            VaultAdditions.LOGGER.error(e.toString());
        }
        return false;
    }

    public static MutableComponent untilDateMessage() {
        String date = ServerConfigs.STOP_ACCEPTING_GEMSTONES_DATE.get();
        try {
            LocalDateTime targetDateTime = LocalDateTime.parse(date, TIME_FORMAT);
            LocalDateTime currentDateTime = LocalDateTime.now();

            if (currentDateTime.isBefore(targetDateTime)) {
                Duration duration = Duration.between(currentDateTime, targetDateTime);

                long days = duration.toDays();
                long hours = duration.toHours() % 24;
                long minutes = duration.toMinutes() % 60;

                String messageTemplate = messages[random.nextInt(messages.length)];

                String dayText = days == 1 ? "Day" : "Days";
                String hourText = hours == 1 ? "Hour" : "Hours";
                String minuteText = minutes == 1 ? "Minute" : "Minutes";

                String formattedMessage = String.format(messageTemplate, days, dayText, hours, hourText, minutes, minuteText);

                return new TextComponent(formattedMessage);
            }
        } catch (DateTimeParseException e) {
            VaultAdditions.LOGGER.error(e.toString());
        }
        return new TextComponent("What did you do. (please report this)").withStyle(ChatFormatting.DARK_RED);
    }

    public static long convertToEpochMillis(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, TIME_FORMAT).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            return -1;
        }
    }


    /**
     *
     * @param timestamp the given timestamp stored in a "dd/MM/yyyy HH:mm:ss" format
     *
     * @return a long array with the values for the time until the given timestamp from execution, contains seconds, minutes, hours and days
     */
    public static long[] untilTimestamp(String timestamp) {
        if (timestamp.equals("No upcoming events!")) return null;

        long targetTime = TimeUtil.convertToEpochMillis(timestamp);
        long currentTime = System.currentTimeMillis();
        long remainingTime = targetTime - currentTime;

        long seconds = (remainingTime / 1000) % 60;
        long minutes = (remainingTime / (1000 * 60)) % 60;
        long hours = (remainingTime / (1000 * 60 * 60)) % 24;
        long days = remainingTime / (1000 * 60 * 60 * 24);

        return new long[]{seconds, minutes, hours, days};
    }

    /**
     * Calculates the time remaining until a given timestamp in a specific timezone.
     *
     * @param timestamp The given timestamp in "dd/MM/yyyy HH:mm:ss" format.
     * @param timezone  The timezone in which the timestamp is set (e.g., "America/New_York", "UTC").
     * @return A long array containing time remaining in seconds, minutes, hours, and days.
     */
    public static long[] untilTimestamp(String timestamp, String timezone) {
        if (timestamp.equals("No upcoming events!")) return null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        ZonedDateTime targetTime = LocalDateTime.parse(timestamp, formatter)
                .atZone(ZoneId.of(timezone));

        Instant targetInstant = targetTime.toInstant();
        Instant currentInstant = Instant.now();
        long remainingTime = Duration.between(currentInstant, targetInstant).toMillis();

        if (remainingTime < 0) {
            return new long[]{0, 0, 0, 0};
        }

        long seconds = (remainingTime / 1000) % 60;
        long minutes = (remainingTime / (1000 * 60)) % 60;
        long hours = (remainingTime / (1000 * 60 * 60)) % 24;
        long days = remainingTime / (1000 * 60 * 60 * 24);

        return new long[]{seconds, minutes, hours, days};
    }
}
