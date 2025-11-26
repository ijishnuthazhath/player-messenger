package org.pm.system;

import org.pm.actor.Player;
import org.pm.actor.PlayerRouter;

public class ShutdownMessageDeserializer implements MessageDeserializer {
    @Override
    public PlayerRouter.RouterMessage deserialize(final String[] parts) {
        return new PlayerRouter.PlayerRouteMessage(parts[0], new Player.ShutdownMessage());
    }

    @Override
    public String getMessageType() {
        return "shutdown";
    }
}
