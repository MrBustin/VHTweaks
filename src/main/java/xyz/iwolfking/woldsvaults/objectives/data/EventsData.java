package xyz.iwolfking.woldsvaults.objectives.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.Map;

public class EventsData {
    public static Map<Component, Double> getMotivationalMessages() {
        return Map.of(new TextComponent("Go get em' tiger!"), 1.0, new TextComponent("I am so proud."), 3.0);
    }
}
