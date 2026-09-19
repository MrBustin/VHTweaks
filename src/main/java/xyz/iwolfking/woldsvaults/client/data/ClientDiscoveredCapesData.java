package xyz.iwolfking.woldsvaults.client.data;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientDiscoveredCapesData {

    private static Set<String> discoveredCapes = new HashSet<>();
    private static String selectedCape = null;
    private static boolean capeEnabled = true;

    public static void receiveUpdate(Set<String> discovered, @Nullable String selected, boolean enabled) {
        discoveredCapes = new HashSet<>(discovered);
        selectedCape = selected;
        capeEnabled = enabled;
    }

    public static Set<String> getDiscoveredCapes() {
        return Collections.unmodifiableSet(discoveredCapes);
    }

    public static boolean hasDiscoveredCape(String capeId) {
        return discoveredCapes.contains(capeId);
    }

    @Nullable
    public static String getSelectedCape() {
        return selectedCape;
    }

    public static boolean isCapeEnabled() {
        return capeEnabled;
    }

    public static void clear() {
        discoveredCapes.clear();
        selectedCape = null;
        capeEnabled = true;
    }
}
