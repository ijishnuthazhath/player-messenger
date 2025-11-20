package org.pm.reference;

import org.pm.actor.SimpleActor;
import org.pm.message.Message;

/**
 * This class is to hold the reference for local actors.
 */
public class LocalActorRef<M extends Message> implements ActorRef<M> {

    private final SimpleActor<M> actor;

    public LocalActorRef(SimpleActor<M> actor) {
        this.actor = actor;
    }

    @Override
    public void tell(M message) {
        this.actor.tell(message);
    }

    @Override
    public String id() {
        return this.actor.name();
    }
}
