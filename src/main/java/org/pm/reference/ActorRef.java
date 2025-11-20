package org.pm.reference;

import org.pm.message.Message;

/**
 * This represents an actor reference - local or remote.
 */
public interface ActorRef<M extends Message> {
    void tell(M message);
    String id();
}
