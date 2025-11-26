package org.pm.reference;

import org.pm.message.Message;
import org.pm.system.PlayerSystem;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * This represents a reference to the remote actor. Hence, contains the player name, host, port.
 * This will serialize the message and add meta-data like host and port of the sender which can be used to reply.
 * This reference is not saved in the router because it is not part of the current system.
 *
 * Uses very basic serialization and socket connection for now. Can use Java NIO or other better solutions for production grade.
 */
public class RemoteActorRef implements ActorRef {
    private final String playerName;
    private final String toHost;
    private final int toPort;

    private final PlayerSystem.Context ctx;

    public RemoteActorRef(String playerName, String toHost, int toPort, PlayerSystem.Context ctx) {
        this.playerName = playerName;
        this.toHost = toHost;
        this.toPort = toPort;
        this.ctx = ctx;
    }

    @Override
    public void tell(Message message) {
        try {
            final Socket socket = new Socket(toHost, toPort);
            final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            final String remoteSerializedMessage = String.format("%s|%s|%s|%s", this.playerName, message.toString(), ctx.host(), ctx.port());
//            System.out.printf("Sending message remote %s %d %n", remoteSerializedMessage, toPort);
            writer.println(remoteSerializedMessage);

            writer.close();
            socket.close();
        } catch (final Exception ex) {
            System.out.printf("Error sending remote message: Make sure the other player system is up and running. Duh! at %d%n", toPort);
            ex.printStackTrace();
        }
    }

    @Override
    public String id() {
        return playerName;
    }
}
