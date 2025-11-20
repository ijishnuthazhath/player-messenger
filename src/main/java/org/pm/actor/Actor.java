package org.pm.actor;

/**
 * Abstraction for an actor. Defines the only way actors can be communicated with.
 * @param <M> represents a message type that actors can communicate with.
 */
public interface Actor<M> {
    void tell(M message);
}
