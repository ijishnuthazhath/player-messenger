package org.pm.system;

import org.pm.actor.PlayerRouter;

public class ShutdownMessageDeserializer implements MessageDeserializer {
    @Override
    public PlayerRouter.RouterMessage deserialize(final String[] parts) {
        return new PlayerRouter.ShutdownMessage();
    }

    @Override
    public String getMessageType() {
        return "shutdown";
    }
}
