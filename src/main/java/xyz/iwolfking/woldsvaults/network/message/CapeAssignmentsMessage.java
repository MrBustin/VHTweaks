package xyz.iwolfking.woldsvaults.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.client.data.ClientPlayerCapeRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class CapeAssignmentsMessage {

    public record Assignment(String capeId, boolean enabled) {}

    private final Map<UUID, Assignment> assignments;

    public CapeAssignmentsMessage(Map<UUID, Assignment> assignments) {
        this.assignments = assignments;
    }

    public Map<UUID, Assignment> getAssignments() {
        return this.assignments;
    }

    public static void encode(CapeAssignmentsMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.assignments.size());
        for (Map.Entry<UUID, Assignment> entry : message.assignments.entrySet()) {
            buffer.writeUUID(entry.getKey());
            Assignment assignment = entry.getValue();
            buffer.writeBoolean(assignment.capeId() != null);
            if (assignment.capeId() != null) {
                buffer.writeUtf(assignment.capeId());
            }
            buffer.writeBoolean(assignment.enabled());
        }
    }

    public static CapeAssignmentsMessage decode(FriendlyByteBuf buffer) {
        int count = buffer.readInt();
        Map<UUID, Assignment> assignments = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            UUID uuid = buffer.readUUID();
            String capeId = buffer.readBoolean() ? buffer.readUtf() : null;
            boolean enabled = buffer.readBoolean();
            assignments.put(uuid, new Assignment(capeId, enabled));
        }
        return new CapeAssignmentsMessage(assignments);
    }

    public static void handle(CapeAssignmentsMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPlayerCapeRegistry.receiveAssignments(message.assignments));
        context.setPacketHandled(true);
    }
}
