package xyz.iwolfking.woldsvaults.client.events;

import iskallia.vault.core.event.Event;
import xyz.iwolfking.woldsvaults.mixin.accessors.ClientEventsAccessor;

public class WoldClientEvents {
    public static final TooltipEvent TOOLTIP_EVENT = register(new TooltipEvent());


    private static <T extends Event<?, ?>> T register(T event) {
        ClientEventsAccessor.getREGISTRY().add(event);
        return event;
    }
}
