package xyz.iwolfking.woldsvaults.events.data;

import iskallia.vault.core.util.WeightedList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class FlightCancellationStrings {
    public static WeightedList<Component> flightCancellationStrings = new WeightedList<>();

    static
    {
        initFlightCancellationStrings();
    }

    public static void initFlightCancellationStrings() {
        flightCancellationStrings.add((new TextComponent("You cannot fly inside The Vault!")), 10);
        flightCancellationStrings.add((new TextComponent("A great power weighs you down!")), 10);
        flightCancellationStrings.add((new TextComponent("You don't have your flying license!")), 10);
        flightCancellationStrings.add((new TextComponent("Flying doesn't work here!")), 10);
        flightCancellationStrings.add((new TextComponent("Wold wouldn't like you flying about!")), 10);
    }


    public static Component getMessage() {
        if(!flightCancellationStrings.isEmpty()) {
                return flightCancellationStrings.getRandom().get().copy().withStyle(ChatFormatting.RED);

        }

        //WoldsVaults.LOGGER.warn("Flight Cancellation messages were empty!");
        return (Component) Component.EMPTY;
    }
}
