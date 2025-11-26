package org.pm.reference;

import org.pm.message.Message;

/**
 * This represents an actor reference - local or remote.
 */
public interface ActorRef {
    void tell(Message message);
    String id();
}
