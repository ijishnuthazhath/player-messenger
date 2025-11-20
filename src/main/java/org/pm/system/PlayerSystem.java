package org.pm.system;

import org.pm.actor.Player;
import org.pm.actor.PlayerRouter;
import org.pm.reference.ActorRef;
import org.pm.reference.LocalActorRef;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This represents a group of actors, and provides the ability to create and manage them.
 * <p>
 * Build in a very basic and straight-forward way. Runs a basic TCP network listener that listens for incoming messages,
 * and delegates the routing to the player-registry actor.
 *
 * Handles two types of messages for now. We can expand it for other types too.
 * - Shutdown: shuts down the system
 * - Direct: sends a message directly to another player
 *
 * The serialization format is as follows:
 * to|<type>|msg|from|host|port
 */
public class PlayerSystem {
    private final int port;

    private final ActorRef<PlayerRouter.RouterMessage> localServerRef;
    private final ExecutorService executorService;

    private volatile boolean running;
    private final Context ctx;
    private final DeserializerRegistry deserializerRegistry;

    public PlayerSystem(final int port, final boolean isRemote) throws UnknownHostException {
        this.port = port;

        this.executorService = Executors.newSingleThreadExecutor();
        this.localServerRef = new LocalActorRef<>(new PlayerRouter());

        this.ctx = new Context(InetAddress.getLocalHost().getHostName(), port, isRemote);
        this.deserializerRegistry = new DeserializerRegistry();
        initializeDeserializers();

        if (isRemote) {
            this.running = true;
            startNetworkListener();
        }
    }

    /**
     * Initializes the deserializers for different types of messages.
     */
    private void initializeDeserializers() {
        deserializerRegistry.register(new ShutdownMessageDeserializer());
        deserializerRegistry.register(new DirectMessageDeserializer(ctx));
    }

    /**
     * Listens for remote messages on a port. Deserialized them and routes it.
     */
    private void startNetworkListener() {
        executorService.submit(() -> {
            try {
                final ServerSocket server = new ServerSocket(port);

                while (running) {
                    final Socket socket = server.accept();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    final String line = reader.readLine();
                    if (line != null) {
                        try {
                            final PlayerRouter.RouterMessage message = deserializerRegistry.deserialize(line);
                            this.localServerRef.tell(message);
                        } catch (final IllegalArgumentException e) {
                            System.out.printf("Failed to deserialize message: %s%n", e.getMessage());
                        }
                    }

                    socket.close();
                }

                if (!server.isClosed()) {
                    server.close();
                }

            } catch (final Exception e) {
                System.out.printf("Error listening for network connections: %s%n", e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public ActorRef<Player.PlayerMessage> createPlayer(final String name) {
        System.out.printf("Creating player %s%n", name);
        final ActorRef<Player.PlayerMessage> player = new LocalActorRef<>(new Player(name));
        this.localServerRef.tell(new PlayerRouter.PlayerRegisterMessage(player));
        return player;
    }

    /**
     * This shuts down all the actors in the system.
     */
    public void shutdown() {
        // shutdown all actors
        this.localServerRef.tell(new PlayerRouter.ShutdownMessage());

        running = false;
        executorService.shutdownNow();
    }

    public Context getContext() {
        return this.ctx;
    }

    /**
     * This holds the generic meta-data of the player system.
     */
    public record Context(String host, int port, boolean remote) {
    }
}
