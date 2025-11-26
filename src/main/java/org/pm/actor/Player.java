package org.pm.actor;

import org.pm.message.Message;
import org.pm.reference.ActorRef;
import org.pm.reference.LocalActorRef;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The star of the show.
 * This class represents a player which can
 * 1. Send a message to another player.
 * 2. Receive messages from other players. When a message is received it will append the count of messages sent so far.
 * If the count exceeds {@link #maxCount} then both the sender and receiver are shut down.
 * 3. Supports for shutdown.
 * 4. The counter contains the messages sent by this player.
 */
public class Player extends SimpleActor {

    private final AtomicInteger counter = new AtomicInteger(0);
    private static final int maxCount = 10;

    public Player(final String name) {
        super(name);
    }

    /**
     * Handles incoming messages.
     * Ok, this method is weird, and it, do not really follow the open/closed principle (Uncle Bob will kill me if he sees this :D)
     * When I need to add new supported message, I would have to modify this method.
     * <p>
     * Things I thought about:
     * - Use Visitor pattern and delegate the work to each message type. But that would result in another class trying to modify actor state.
     * or return the call back to each specific handler method in the actor (Player) itself, which would result again existing code modification.
     * <p>
     * - Create a map of message type to handler method, but still need add separate handler for each message type.
     * This design would be more useful if we could de-couple the behaviour from the actor.
     */
    @Override
    protected void onReceive(final Message msg) {
        if (msg instanceof SendMessage sendMessage) {
            handleSendMessage(sendMessage);
        } else if (msg instanceof DirectMessage directMessage) {
            handleDirectMessage(directMessage);
        } else if (msg instanceof ShutdownMessage) {
            handleShutDown();
        } else if (msg instanceof KillPill) {
            shutdown();
        } else {
            System.err.println("Unknown message type");
        }
    }

    /**
     * This method handles the send-message command. It sends a direct message to the target player with an incremented message count.
     * If the count reaches the maximum limit, it shuts down both the sender and receiver actors.
     *
     */
    private void handleSendMessage(final SendMessage sendMessage) {
        final int currentCount = this.counter.incrementAndGet();
        if (currentCount <= maxCount) {
            sendMessage.sendTo.tell(new DirectMessage(sendMessage.message, new LocalActorRef(this)));
        } else {
            sendMessage.sendTo.tell(new ShutdownMessage());
            this.tell(new ShutdownMessage());
        }
    }

    private void handleDirectMessage(final DirectMessage directMessage) {
        System.out.printf("[%s] %s%n", directMessage.replyTo.id(), directMessage.message);
        final String newMessage = String.format("%s %d", directMessage.message, this.counter.get());
        this.tell(new SendMessage(newMessage, directMessage.replyTo));

    }

    public void handleShutDown() {
        System.out.printf("Killing %s%n", this.name());
        this.tell(new KillPill());
    }

    /**
     * Represents a message that can be processed by this actor.
     * Only the messages supported by the remote feature overrides toString for serialization.
     */
    public interface PlayerMessage extends Message {
    }

    public record SendMessage(String message, ActorRef sendTo) implements PlayerMessage {
    }

    /**
     * The message send between the players.
     * Implements a very minimal serialization by overriding the toString method. This is used when sending the message over network.
     * Hard coded the type as a string to identify the message type when deserializing. Can be done better.
     */
    public record DirectMessage(String message, ActorRef replyTo) implements PlayerMessage {
        @Override
        public String toString() {
            return String.format("%s|%s|%s", "direct", message, replyTo.id());
        }
    }

    /**
     * The interesting thing about shutting down an actor is that you might still have some messages in the player mailbox to process.
     * So, when Shutdown command is received the player will append the KillPill command the end of the mailbox so the player actor is actually shutdown when KillPill command is processed.
     */
    public record ShutdownMessage() implements PlayerMessage {
        @Override
        public String toString() {
            return "shutdown";
        }
    }

    /**
     * This message commend immediately kills the actor. This command do not destroy the object but stops the message processing.
     */
    public record KillPill() implements PlayerMessage {
    }
}
