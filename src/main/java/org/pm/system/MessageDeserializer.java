package org.pm.system;

import org.pm.actor.PlayerRouter;

/**
 * Abstraction for a message deserializer used by the network listener to convert incoming messages.
 */
public interface MessageDeserializer {
    PlayerRouter.RouterMessage deserialize(String[] parts);

    String getMessageType();
}
