package org.pm.actor;

import org.pm.message.Message;

/**
 * Abstraction for an actor. Defines the only way actors can be communicated with.
 * Message is the base type of all messages in the system.
 */
public interface Actor {
    void tell(Message message);
}
