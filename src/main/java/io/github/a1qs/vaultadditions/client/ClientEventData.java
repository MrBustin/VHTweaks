package io.github.a1qs.vaultadditions.client;

public class ClientEventData {
    private static boolean isGlobeExpanderRequired = false;
    private static boolean isEventActive = false;

    public static boolean isGlobeExpanderRequired() {
        return isGlobeExpanderRequired;
    }

    public static boolean isEventActive() {
        return isEventActive;
    }

    public static void setIsGlobeExpanderRequired(boolean isGlobeExpanderRequired) {
        ClientEventData.isGlobeExpanderRequired = isGlobeExpanderRequired;
    }

    public static void setIsEventActive(boolean isEventActive) {
        ClientEventData.isEventActive = isEventActive;
    }


    public static void update(boolean isGlobeExpanderRequired, boolean isEventActive) {
        ClientEventData.isGlobeExpanderRequired = isGlobeExpanderRequired;
        ClientEventData.isEventActive = isEventActive;
    }
}
