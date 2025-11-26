package org.pm.reference;

import org.pm.actor.SimpleActor;
import org.pm.message.Message;

/**
 * This class is to hold the reference for local actors.
 */
public class LocalActorRef implements ActorRef {

    private final SimpleActor actor;

    public LocalActorRef(SimpleActor actor) {
        this.actor = actor;
    }

    @Override
    public void tell(Message message) {
        this.actor.tell(message);
    }

    @Override
    public String id() {
        return this.actor.name();
    }
}
