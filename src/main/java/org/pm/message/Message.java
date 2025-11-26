package org.pm.message;

/**
 * Marker interface for messages used by the SimpleActor.
 * This restricts all the players and other actors that inherit SimpleActor to use only messages of this type.
 * This is simple and straight forwarder than using generic types.
 */
public interface Message {
}
