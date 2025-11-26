package org.pm;

import org.pm.actor.Player;
import org.pm.reference.ActorRef;
import org.pm.system.PlayerSystem;

import java.net.UnknownHostException;

/**
 * This runs the application with two players with default or name passed in the args.
 * And one player initiates the messages. The other player just receives the message.
 *
 * In the end, shuts down all the actors and the system itself.
 * Note that initiator player will send a shutdown message to both of them.
 *
 */
public class MainSinglePID {
    public static void main(String[] args) throws UnknownHostException {
        // Create the server and two players.

        final String initiatorName = args.length > 0 ? args[0] : "initiator";
        final String receiverName = args.length > 1 ? args[1] : "receiver";

        System.out.println("Creating player system");
        final PlayerSystem playerSystem = new PlayerSystem(-1, false);

        final ActorRef initiator = playerSystem.createPlayer(initiatorName);
        final ActorRef receiver = playerSystem.createPlayer(receiverName);

        // Send a message from initiator to receiver.
        System.out.printf("Initiate Chat between %s and %s ===> \n", initiator.id(), receiver.id());
        initiator.tell(new Player.SendMessage("Hey", receiver));

        // since the entire system is async, lets wait for some time and shutdown the system.
        try {
            Thread.sleep(1500);
        } catch (final InterruptedException ignored) {
        }

        // Shutdown the system.
        // The players can communicate a shutdown message themselves, the remaining actors if any are shutdown along with the system by the system.
        playerSystem.shutdown();
    }
}