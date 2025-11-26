package org.pm.actor;

import org.pm.message.Message;
import org.pm.reference.ActorRef;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an interesting actor that routes messages to players based on the player's name.
 * Right now this is utilized only for routing remote messages.
 * It can also be used to route local messages but direct reference is used at the moment.
 * <p>
 * Is this class really required ?
 * Actually no, we can send messages to the players from the PlayerSystem that runs on the port.
 * I chose to separate the routing logic and player to local-reference mapping and the state management to a separate actor (Uncle Bob smiling :D).
 * <p>
 * If we have other actors in the system, we use this to route messages them too by adding new routing types.
 */
public class PlayerRouter extends SimpleActor {
    private static final String ID = "player-router";

    // Since only one thread executes the mailbox, there is no need to synchronize access to the map. The crust of actor model here.
    private final Map<String, ActorRef> actorRefByName = new HashMap<>();

    public PlayerRouter() {
        super(ID);
    }

    /**
     * All the actor state is managed here. Very similar to the Player actor.
     *
     * @param msg
     */
    @Override
    protected void onReceive(final Message msg) {
        if (msg instanceof PlayerRegisterMessage regMsg) {
            handlePlayerRegister(regMsg);
        } else if (msg instanceof PlayerRouteMessage routeMsg) {
            handlePlayerRoute(routeMsg);
        } else if (msg instanceof ShutdownMessage) {
            handleShutdown();
        } else if (msg instanceof KillPill) {
            this.shutdown();
        } else {
            System.out.printf("Unknown message type: %s%n", msg.getClass());
        }
    }

    /**
     * All the actors created in the player system is registered here in this router.
     * This is the message command handler to register an actor with its reference.
     */
    private void handlePlayerRegister(final PlayerRegisterMessage regMsg) {
        if (regMsg != null) {
            this.actorRefByName.put(regMsg.playerRef.id(), regMsg.playerRef);
        } else {
            System.out.println("Registration failed for player");
        }
    }

    /**
     * This message handler message command handler sends the message to the appropriate player.
     * If the player does not exist then it prints an error message.
     */
    private void handlePlayerRoute(final PlayerRouteMessage routeMsg) {
        if (routeMsg != null) {
            final ActorRef toPlayerRef = this.actorRefByName.get(routeMsg.to);

            if (toPlayerRef != null) {
                toPlayerRef.tell(routeMsg.message);
            } else {
                System.out.printf("Could not find player '%s' for routing message.%n", routeMsg.to);
            }
        }
    }

    /**
     * This command handler shuts down this actor after draining the mailbox. Similar to the Player shutdown behaviour.
     */
    private void handleShutdown() {
        this.actorRefByName.values()
                .forEach(ref -> ref.tell(new Player.ShutdownMessage()));
        System.out.printf("Killing %s%n", this.name());
        this.tell(new KillPill());
    }

    public interface RouterMessage extends Message {
    }

    public record PlayerRegisterMessage(ActorRef playerRef) implements RouterMessage {
    }

    public record PlayerRouteMessage(String to, Message message) implements RouterMessage {
    }

    // This will add a kill message at the end of the mailbox. The mailbox will be processed until it reaches this message.
    public record ShutdownMessage() implements RouterMessage {
    }

    // Immediate shutdown and do not wait for the actors to finish processing their mailboxes.
    public record KillPill() implements RouterMessage {
    }
}
