# Player Messenger
This is a simple messaging system for players to communicate with each other either remote or local. The application is build using the actor model.
Each actor has a mailbox where it can receive messages and send them to another actor. State management is done by processing each messages.

## How to build
```bash
mvn clean install
```

## How To Run - Single PID
```bash
cd scripts
chmod +x run_players_single_pid.sh
./run_players_single_pid.sh initiator receiver
```

## How To Run - Multiple PID
Run the commands in two different terminal windows. Create players to receive messages first before sending messages.

```bash
cd scripts
chmod +x run_player_multi_pid.sh
./run_player_multi_pid.sh 8095 receiver1,receiver2
./run_player_multi_pid.sh 8096 initiator1,initiator2 receiver1:localhost:8095
```


### Improvements
With my limited knowledge in the actor model, build a simple application. There are many improvements that could be made.
There are coupling between actors and their mailbox, and their behaviour. 
Ideal way is to de-couple them. For example, since actor and its mailbox are coupled we cannot destroy actor that has corrupt state and reattach it to the previous mailbox.
Define behaviours to actors in a scalable way.
This design do not consider actors spawning actors and becoming a tree structure. Hence, there is no actor path only single actors and their local reference.
We could create an ActorRef in each player that maps to a player child actor, when player is capable of creating child actors.

