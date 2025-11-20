package org.pm.system;

import org.pm.actor.PlayerRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for message deserializers that are supported in the system.
 * This is very fundamental.
 */
public class DeserializerRegistry {
    private final Map<String, MessageDeserializer> deserializers = new HashMap<>();

    public void register(final MessageDeserializer deserializer) {
        deserializers.put(deserializer.getMessageType(), deserializer);
    }

    public PlayerRouter.RouterMessage deserialize(final String message) {
        final String[] parts = message.split("\\|");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid message format");
        }

        final String messageType = parts[1];
        final MessageDeserializer deserializer = deserializers.get(messageType);

        if (deserializer == null) {
            throw new IllegalArgumentException("No deserializer found for message type: " + messageType);
        }

        return deserializer.deserialize(parts);
    }
}
