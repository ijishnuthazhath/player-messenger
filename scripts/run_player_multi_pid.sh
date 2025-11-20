#!/bin/bash

# Usage:
# ./run_player_multi_pid.sh <port> <initiator1,initiator2,..> <name>:<host>:<port>
#
# Example:
# ./run_player_multi_pid.sh 8095 receiver1,receiver2
# ./run_player_multi_pid.sh 8096 initiator1,initiator2 receiver1:localhost:8095

# Navigate to project root
cd "$(dirname "$0")/.." || exit 1

echo "Building project..."
mvn clean compile -q

echo "Creating player system running on posrt $1 with actors $2"
mvn exec:java -Dexec.mainClass="org.pm.MainPlayerMultiplePID" -Dexec.args="$1 $2 $3" -q
