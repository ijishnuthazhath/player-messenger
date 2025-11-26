package org.pm.actor;

import org.pm.message.Message;

import java.util.concurrent.*;

/**
 * This is an actor abstract implementation.
 * This defines a category of actors that can accept any messages that are subclasses of {@link Message}.
 * The developers can define another actor implementation like this with different base behaviour if required.
 * <p>
 * Encapsulates the actor mailbox which stores the messages and processes them one by one.
 * It also implements the {@code tell} method to send messages
 * <p>
 * The messages in the mailbox is processed sequentially using a single thread executor service.
 * If there are multiple threads sending messages to the same actor,
 * then they will be queued up and processed one after the other.
 *
 * The mailbox is unbounded. Guess it's okay since our messages are lightweight.
 *
 * SimpleActor is designed to handler Message type. It's simpler in this case.
 * We could say, use SimpleActor<T> instead and avoid Message abstraction so that all actors in the system could create any type of messages.
 */
public abstract class SimpleActor implements Actor {
    private final String name;

    private final BlockingQueue<Message> mailbox = new LinkedBlockingDeque<>();
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    protected volatile boolean running;

    protected SimpleActor(String name) {
        this.name = name;
        this.running = true;
        this.service.submit(this::processMailbox);
    }

    /**
     * Processes all the messages in the mailbox one by one in a separate thread.
     * If the mailbox is empty, the thread sleeps until a message arrives.
     */
    private void processMailbox() {
        while (running) {
            try {
                final Message msg = mailbox.take();
                try {
                    onReceive(msg);
                } catch (final Exception ex) {
                    System.out.printf("Actor %s crashed while processing the message %s. Revival is not supported at the moment. Sorry! :/%n", this.name, msg);
                    ex.printStackTrace();
                }
            } catch (final InterruptedException ex) {
                System.out.println("Interrupted while waiting for a message.");
            }
        }
    }

    /**
     * Sends a message to this actor.
     * The message is added to the mailbox and processed later.
     * If the mailbox is full, the caller waits until space becomes available or times out.
     */
    public final void tell(final Message msg) {
        try {
            this.mailbox.offer(msg, 600, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException ex) {
            System.out.println("The message was not enqueued in time.");
        }
    }

    protected abstract void onReceive(Message msg);

    protected void shutdown() {
        this.running = false;
        this.service.shutdownNow();
    }

    public String name() {
        return this.name;
    }
}
