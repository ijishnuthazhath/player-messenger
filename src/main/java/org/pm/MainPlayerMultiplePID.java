package org.pm;

import org.pm.actor.Player;
import org.pm.reference.ActorRef;
import org.pm.reference.RemoteActorRef;
import org.pm.system.PlayerSystem;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main class which can be used to create a player system and create players.
 * This accepts command line arguments as follows:
 * 8096 initiator1,initiator2 receiver1:localhost:8095
 *
 * The last part it optional. If no receiver is passed, then the first player created will initiate chat.
 * Otherwise, the first player created will initiate chat with the specified receiver.
 */
public class MainPlayerMultiplePID {
    public static void main(final String[] args) throws UnknownHostException {

        final int systemPort = Integer.parseInt(args[0]);
        final String playerNames = args[1];

        System.out.println("Creating player system");
        final PlayerSystem playerSystem = new PlayerSystem(systemPort, true);

        final String[] players = playerNames.split(",");

        /*
         * I gave the possibility of creating multiple players in the system.
         * When there are more than one player, then the first one created will initiate the chat.
         */
        final List<ActorRef> playersList = new ArrayList<>();
        for (final String playerName : players) {
            playersList.add(playerSystem.createPlayer(playerName.trim()));
        }

        /*
         * Only if there is a remote receiver defined - only then would the initiator player be able to initiate chat.
         * So, when you create the first system with this "general purpose" main class, do not pass receiver because there wont be any remote receiver listening.
         * The system won't fail but your message will be lost in oblivion.
         */
        if (args.length > 2) {
            final String receiverArg = args[2];
            final String[] receiverParts = receiverArg.split(":");

            final String receiverName = receiverParts[0];
            final String receiverHost = receiverParts[1];
            final int receiverPort = Integer.parseInt(receiverParts[2]);

            System.out.printf("Initiate Chat between %s and %s ===> \n", playersList.getFirst().id(), receiverName);
            playersList.getFirst().tell(new Player.SendMessage("Hello", new RemoteActorRef(receiverName, receiverHost, receiverPort, playerSystem.getContext())));
        }

        // Note that we cannot call for player-system shutdown here because it will try to stop all players in this system,
        // but the player might be waiting to receive a message that might be delayed by some network partition.
        // Cntrl + C should be used to terminate the program. Note that the players in action will shutndown gracefully.
        // The initiator player will send a shutdown to the other player after the 10 messages are sent and shutdown itself.
        // But any other players created should wait for the entire system to stop.
    }
}
