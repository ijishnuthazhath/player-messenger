package org.pm.system;

import org.pm.actor.Player;
import org.pm.actor.PlayerRouter;
import org.pm.reference.RemoteActorRef;

/**
 * Deserializer for direct player messages
 */
public class DirectMessageDeserializer implements MessageDeserializer {
    private final PlayerSystem.Context context;

    public DirectMessageDeserializer(final PlayerSystem.Context context) {
        this.context = context;
    }

    @Override
    public PlayerRouter.RouterMessage deserialize(final String[] parts) {
        final String to = parts[0];
        final String msg = parts[2];
        final String from = parts[3];
        final String host = parts[4];
        final int port = Integer.parseInt(parts[5]);

        return new PlayerRouter.PlayerRouteMessage(
                to,
                new Player.DirectMessage(msg, new RemoteActorRef(from, host, port, context))
        );
    }

    @Override
    public String getMessageType() {
        return "direct";
    }
}
